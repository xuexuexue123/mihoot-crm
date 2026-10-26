package com.ruoyi.crm.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.domain.CrmIdentityConflict;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.vo.IdentityResolveResultVo;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmCustomerIdentityMapper;
import com.ruoyi.crm.mapper.CrmIdentityConflictMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmWecomExternalContactService;
import com.ruoyi.crm.service.support.IdentityNormalizer;

/**
 * 客户身份Service业务层处理。
 *
 * <p><b>权威源约定（P2）</b>：{@code crm_customer_identity} 是客户身份的唯一权威源（single source of truth）。
 * {@code crm_lead.wechat_mp_openid} / {@code crm_lead.wecom_external_userid}、
 * {@code crm_wechat_mp_fan.contact_id}、{@code crm_contact.wecom_external_userid} 等均为归因快照/展示缓存，
 * <b>不作为身份解析依据</b>。所有解析统一走 {@link #findContactByIdentity}（内部 fallback 查 crm_contact 后回写 identity）。
 * 快照字段只应在写入 identity 的同一流程中同步更新，避免多处各写各的。
 * 一致性巡检见 {@code scripts/sql/patrol-identity-snapshot-consistency.sql}。
 *
 * @author luohd
 * @date 2026-06-16
 */
@Service
public class CrmCustomerIdentityServiceImpl implements ICrmCustomerIdentityService
{
    private static final Logger log = LoggerFactory.getLogger(CrmCustomerIdentityServiceImpl.class);

    @Autowired
    private CrmCustomerIdentityMapper identityMapper;

    @Autowired
    private CrmContactMapper contactMapper;

    @Autowired
    private CrmIdentityConflictMapper identityConflictMapper;

    @Autowired
    private CrmWecomExternalContactMapper wecomExternalContactMapper;

    @Autowired
    private CrmLeadMapper leadMapper;

    @Autowired
    private ICrmWecomExternalContactService wecomExternalContactService;

    @Override
    public String findContactByIdentity(String identityType, String identityValue)
    {
        IdentityResolveResultVo result = resolveIdentity(identityType, identityValue);
        return result != null ? result.getContactId() : null;
    }

    @Override
    public IdentityResolveResultVo resolveIdentity(String identityType, String identityValue)
    {
        IdentityResolveResultVo result = new IdentityResolveResultVo();
        if (identityType == null || identityType.isEmpty()
                || identityValue == null || identityValue.isEmpty())
        {
            return result;
        }

        // 归一化：保证不同格式（手机区号/空格、邮箱大小写）命中同一 identity 行
        identityValue = IdentityNormalizer.normalize(identityType, identityValue);
        if (identityValue == null || identityValue.isEmpty())
        {
            return result;
        }

        // 1. 优先查 crm_customer_identity
        CrmCustomerIdentity identity = identityMapper.selectByTypeValue(identityType, identityValue);
        if (identity != null && identity.getContactId() != null)
        {
            result.setContactId(identity.getContactId());
            result.setMatchedVia("identity_table");
            return result;
        }

        // 2. Fallback 到 crm_contact 直查
        String contactId = fallbackLookupInContact(identityType, identityValue);
        if (contactId != null)
        {
            result.setContactId(contactId);
            result.setMatchedVia("contact_fallback");
            try
            {
                CrmCustomerIdentity newId = new CrmCustomerIdentity();
                newId.setContactId(contactId);
                newId.setIdentityType(identityType);
                newId.setIdentityValue(identityValue);
                newId.setSource("sync_from_contact");
                newId.setIsPrimary(false);
                identityMapper.insertOrIgnore(newId);
            }
            catch (Exception e)
            {
                log.warn("Failed to backfill identity: type={}, value={}, contactId={}",
                        identityType, identityValue, contactId, e);
            }
            return result;
        }

        // 3. wecom_external_userid：查 crm_wecom_external_contact / crm_lead
        if ("wecom_external_userid".equals(identityType))
        {
            applyWecomExternalFallback(identityValue, result);
        }

        return result;
    }

    private void applyWecomExternalFallback(String externalUserid, IdentityResolveResultVo result)
    {
        List<CrmWecomExternalContact> extRows = wecomExternalContactMapper.selectByExternalUserid(externalUserid);
        if (extRows != null)
        {
            for (CrmWecomExternalContact row : extRows)
            {
                if (row.getCrmContactId() != null && !row.getCrmContactId().isEmpty())
                {
                    result.setContactId(row.getCrmContactId());
                    result.setMatchedVia("external_contact_table");
                    return;
                }
            }
            for (CrmWecomExternalContact row : extRows)
            {
                if (row.getCrmLeadId() != null && !row.getCrmLeadId().isEmpty())
                {
                    result.setLeadId(row.getCrmLeadId());
                    result.setMatchedVia("lead_table");
                    return;
                }
            }
        }

        CrmLead lead = leadMapper.selectByWecomExternalUserid(externalUserid);
        if (lead != null && lead.getLeadId() != null)
        {
            result.setLeadId(lead.getLeadId());
            result.setMatchedVia("lead_table");
        }
    }

    /**
     * Fallback: 直接查询 crm_contact 表按身份字段匹配。
     */
    private String fallbackLookupInContact(String identityType, String identityValue)
    {
        CrmContact query = new CrmContact();
        switch (identityType)
        {
            case "mobile_phone":
                query.setMobilePhone(identityValue);
                return firstContactId(contactMapper.selectCrmContactList(query));
            case "email":
            {
                // Query work_email and personal_email separately (selectCrmContactList uses AND logic)
                CrmContact q1 = new CrmContact();
                q1.setWorkEmail(identityValue);
                String result = firstContactId(contactMapper.selectCrmContactList(q1));
                if (result != null)
                {
                    return result;
                }
                CrmContact q2 = new CrmContact();
                q2.setPersonalEmail(identityValue);
                return firstContactId(contactMapper.selectCrmContactList(q2));
            }
            case "wecom_external_userid":
                query.setWecomExternalUserid(identityValue);
                return firstContactId(contactMapper.selectCrmContactList(query));
            case "wechat_id":
                query.setWechat(identityValue);
                return firstContactId(contactMapper.selectCrmContactList(query));
            case "whatsapp_wa_id":
                // wa_id is a WhatsApp-specific identifier, no direct crm_contact field.
                // Resolved via crm_customer_identity table.
                return null;
            case "whatsapp_phone":
                query.setMobilePhone(identityValue);
                return firstContactId(contactMapper.selectCrmContactList(query));
            default:
                return null;
        }
    }

    private String firstContactId(List<CrmContact> contacts)
    {
        if (contacts != null && !contacts.isEmpty() && contacts.get(0).getContactId() != null)
        {
            return contacts.get(0).getContactId();
        }
        return null;
    }

    @Override
    public Map<String, String> batchResolveIdentities(List<CrmCustomerIdentity> identities)
    {
        Map<String, String> result = new HashMap<>();
        if (identities == null || identities.isEmpty())
        {
            return result;
        }

        // 归一化查询值用于 DB 匹配；结果 map 仍以调用方原始 key 返回，避免破坏调用方查表
        List<CrmCustomerIdentity> normalizedQueries = new java.util.ArrayList<>();
        for (CrmCustomerIdentity q : identities)
        {
            CrmCustomerIdentity nq = new CrmCustomerIdentity();
            nq.setIdentityType(q.getIdentityType());
            nq.setIdentityValue(IdentityNormalizer.normalize(q.getIdentityType(), q.getIdentityValue()));
            normalizedQueries.add(nq);
        }

        // 1. 批量查 crm_customer_identity（归一化 key -> contactId）
        Map<String, String> normalizedHits = new HashMap<>();
        List<CrmCustomerIdentity> matched = identityMapper.batchResolve(normalizedQueries);
        if (matched != null)
        {
            for (CrmCustomerIdentity id : matched)
            {
                normalizedHits.put(id.getIdentityType() + ":" + id.getIdentityValue(), id.getContactId());
            }
        }

        // 2. 按原始输入回填结果；未命中的 fallback 查 crm_contact
        for (CrmCustomerIdentity q : identities)
        {
            String originalKey = q.getIdentityType() + ":" + q.getIdentityValue();
            if (result.containsKey(originalKey))
            {
                continue;
            }
            String normalizedValue = IdentityNormalizer.normalize(q.getIdentityType(), q.getIdentityValue());
            String normalizedKey = q.getIdentityType() + ":" + normalizedValue;
            String contactId = normalizedHits.get(normalizedKey);
            if (contactId == null)
            {
                contactId = fallbackLookupInContact(q.getIdentityType(), normalizedValue);
                if (contactId != null)
                {
                    // 异步补写（归一化值）
                    try
                    {
                        CrmCustomerIdentity newId = new CrmCustomerIdentity();
                        newId.setContactId(contactId);
                        newId.setIdentityType(q.getIdentityType());
                        newId.setIdentityValue(normalizedValue);
                        newId.setSource("sync_from_contact");
                        identityMapper.insertOrIgnore(newId);
                    }
                    catch (Exception e)
                    {
                        log.warn("Failed to batch backfill identity: type={}, value={}",
                                q.getIdentityType(), normalizedValue, e);
                    }
                }
            }
            result.put(originalKey, contactId);
        }

        return result;
    }

    @Override
    public List<IdentityResolveResultVo> batchResolveIdentitiesExtended(List<CrmCustomerIdentity> identities)
    {
        List<IdentityResolveResultVo> results = new java.util.ArrayList<>();
        if (identities == null || identities.isEmpty())
        {
            return results;
        }

        for (CrmCustomerIdentity item : identities)
        {
            IdentityResolveResultVo vo = resolveIdentity(
                    item != null ? item.getIdentityType() : null,
                    item != null ? item.getIdentityValue() : null);
            if (vo == null)
            {
                vo = new IdentityResolveResultVo();
            }
            vo.setIdentityType(item != null ? item.getIdentityType() : null);
            vo.setIdentityValue(item != null ? item.getIdentityValue() : null);
            results.add(vo);
        }
        return results;
    }

    @Override
    public void bindIdentity(String contactId, String identityType, String identityValue, String source)
    {
        if (contactId == null || identityType == null || identityValue == null)
        {
            throw new ServiceException("contactId, identityType, identityValue must not be null");
        }

        // 归一化：写入前统一格式，确保唯一约束 uk_crm_identity_type_value 真正去重
        identityValue = IdentityNormalizer.normalize(identityType, identityValue);
        if (identityValue == null || identityValue.isEmpty())
        {
            throw new ServiceException("identityValue must not be empty after normalization");
        }

        // 检查是否已绑定到其他 contact
        CrmCustomerIdentity existing = identityMapper.selectByTypeValue(identityType, identityValue);
        if (existing != null && existing.getContactId() != null
                && !existing.getContactId().equals(contactId))
        {
            throw new ServiceException(
                String.format("Identity %s=%s already bound to contactId=%s, currentContactId=%s",
                    identityType, identityValue, existing.getContactId(), contactId),
                409
            );
        }

        // 幂等插入
        CrmCustomerIdentity newId = new CrmCustomerIdentity();
        newId.setContactId(contactId);
        newId.setIdentityType(identityType);
        newId.setIdentityValue(identityValue);
        newId.setSource(source != null ? source : "manual");
        newId.setIsPrimary(false);
        identityMapper.insertOrIgnore(newId);

        if ("wecom_external_userid".equals(identityType))
        {
            wecomExternalContactService.upsertFromCrmBinding(
                    identityValue, null, contactId, null);
        }
    }

    @Override
    public void unbindIdentity(String contactId, String identityType, String identityValue)
    {
        identityMapper.deleteByContactTypeValue(contactId, identityType, identityValue);
    }

    @Override
    public void recordIdentityConflict(String identityType, String identityValue,
            String incomingContactId, String source)
    {
        if (identityType == null || identityType.isEmpty() || identityValue == null || identityValue.isEmpty())
        {
            return;
        }
        try
        {
            String normalized = IdentityNormalizer.normalize(identityType, identityValue);
            if (normalized == null || normalized.isEmpty())
            {
                return;
            }
            CrmCustomerIdentity existing = identityMapper.selectByTypeValue(identityType, normalized);
            String existingContactId = existing != null ? existing.getContactId() : null;
            // 仅当确实冲突（已绑到其他 Contact）时记录
            if (existingContactId == null
                    || (incomingContactId != null && existingContactId.equals(incomingContactId)))
            {
                return;
            }
            CrmIdentityConflict conflict = new CrmIdentityConflict();
            conflict.setIdentityType(identityType);
            conflict.setIdentityValue(normalized);
            conflict.setExistingContactId(existingContactId);
            conflict.setIncomingContactId(incomingContactId != null ? incomingContactId : "");
            conflict.setSource(source);
            conflict.setStatus("pending");
            identityConflictMapper.insertIgnoreOnConflict(conflict);
            log.info("Recorded identity conflict: type={}, value={}, existing={}, incoming={}, source={}",
                    identityType, normalized, existingContactId, incomingContactId, source);
        }
        catch (Exception e)
        {
            log.warn("Failed to record identity conflict: type={}, value={}: {}",
                    identityType, identityValue, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> mergeIdentity(String sourceContactId, String targetContactId)
    {
        Map<String, Integer> stats = new HashMap<>();

        // 校验两个 contact 都存在
        CrmContact sourceContact = contactMapper.selectCrmContactByContactId(sourceContactId);
        if (sourceContact == null)
        {
            throw new ServiceException("Source contact not found: " + sourceContactId);
        }
        CrmContact targetContact = contactMapper.selectCrmContactByContactId(targetContactId);
        if (targetContact == null)
        {
            throw new ServiceException("Target contact not found: " + targetContactId);
        }

        // 统计源身份总数
        List<CrmCustomerIdentity> sourceIdentities = identityMapper.selectByContactId(sourceContactId);
        int sourceCount = sourceIdentities != null ? sourceIdentities.size() : 0;

        // 迁移身份记录（冲突的记录会跳过）
        int updated = identityMapper.updateContactIdByContactId(sourceContactId, targetContactId);
        int skipped = sourceCount - updated;

        stats.put("mergedCount", updated);
        stats.put("skippedCount", skipped);

        log.info("mergeIdentity: sourceContactId={}, targetContactId={}, merged={}, skipped={}",
                sourceContactId, targetContactId, updated, skipped);

        return stats;
    }

    @Override
    public List<CrmCustomerIdentity> listIdentitiesByContact(String contactId)
    {
        return identityMapper.selectByContactId(contactId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPrimaryIdentity(String contactId, String identityType, String identityValue)
    {
        if (contactId == null || contactId.isEmpty()
                || identityType == null || identityType.isEmpty()
                || identityValue == null || identityValue.isEmpty())
        {
            throw new ServiceException("contactId, identityType, identityValue must not be null");
        }

        CrmCustomerIdentity identity = identityMapper.selectByTypeValue(identityType, identityValue);
        if (identity == null || !contactId.equals(identity.getContactId()))
        {
            throw new ServiceException(
                String.format("Identity not found for contactId=%s: %s=%s",
                    contactId, identityType, identityValue),
                404
            );
        }

        identityMapper.clearPrimaryByContactId(contactId);
        identityMapper.setPrimaryByContactTypeValue(contactId, identityType, identityValue);
    }
}
