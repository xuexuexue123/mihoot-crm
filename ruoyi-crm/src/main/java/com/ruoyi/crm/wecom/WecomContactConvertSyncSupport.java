package com.ruoyi.crm.wecom;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.domain.dto.wecom.WecomFollowUserSnapshot;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.service.ICrmWecomExternalContactService;

/**
 * Lead 转化或新建 Contact 时，从企微拉取最新标签与展示字段写入 {@code crm_contact}，并回写映射表。
 */
@Component
public class WecomContactConvertSyncSupport
{
    private static final Logger log = LoggerFactory.getLogger(WecomContactConvertSyncSupport.class);

    @Autowired
    private WecomExternalContactProfileResolver profileResolver;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CrmWecomExternalContactMapper wecomExternalContactMapper;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private WecomLeadEnrichSupport wecomLeadEnrichSupport;

    @Autowired
    private ICrmWecomExternalContactService wecomExternalContactService;

    @Autowired
    private com.ruoyi.crm.service.wecom.contactgroup.WecomContactGroupService wecomContactGroupService;

    /**
     * 联系人新建后最佳努力同步企微标签与 wecom_* 字段；失败不抛异常。
     */
    public void syncAfterContactCreate(CrmContact contact)
    {
        if (contact == null || !StringUtils.hasText(contact.getContactId()))
        {
            return;
        }
        if (!StringUtils.hasText(contact.getWecomExternalUserid()))
        {
            return;
        }
        CrmLead lead = crmLeadMapper.selectByWecomExternalUserid(contact.getWecomExternalUserid().trim());
        if (lead != null)
        {
            syncAfterLeadConverted(lead, contact);
            return;
        }
        syncWecomProfile(contact.getContactId(), contact.getWecomExternalUserid().trim(), null, null);
    }

    /**
     * Lead 转化后同步企微资料与映射表（需 Lead 含 wecomExternalUserid 或可由映射表反查）。
     */
    public void syncAfterLeadConverted(CrmLead lead, CrmContact contact)
    {
        if (lead == null || contact == null
                || !StringUtils.hasText(contact.getContactId()))
        {
            return;
        }
        String externalUserid = firstNonBlank(lead.getWecomExternalUserid(), contact.getWecomExternalUserid());
        if (!StringUtils.hasText(externalUserid))
        {
            externalUserid = resolveExternalUseridFromMapping(lead.getLeadId());
        }
        if (!StringUtils.hasText(externalUserid))
        {
            return;
        }
        syncWecomProfile(contact.getContactId().trim(), externalUserid.trim(), lead, lead.getLeadId());
    }

    private void syncWecomProfile(String contactId, String externalUserid, CrmLead lead, String leadId)
    {
        try
        {
            String followUserid = resolveFollowUserid(externalUserid, lead);
            String rawJson = fetchRawJson(externalUserid);
            WecomFollowUserSnapshot follow = WecomExternalContactJsonSupport.resolveFollowUser(rawJson, followUserid);

            CrmContact upd = new CrmContact();
            upd.setContactId(contactId);
            upd.setWecomExternalUserid(externalUserid);
            if (StringUtils.hasText(rawJson))
            {
                WecomContactInfoSyncSupport.fillWecomInfoFromRawJson(upd, rawJson);
            }
            String tagJson = WecomLeadEnrichSupport.buildCrmLeadTagJson(follow != null ? follow.getTags() : null);
            if (tagJson != null)
            {
                upd.setTag(tagJson);
            }
            upd.setUpdateTime(DateUtils.getNowDate());
            crmContactMapper.updateCrmContact(upd);

            updateMappingRow(externalUserid, followUserid, contactId, leadId, rawJson, follow);
            try
            {
                wecomContactGroupService.provision(contactId);
            }
            catch (Exception pe)
            {
                log.debug("wecom contact group provision skipped: {}", pe.getMessage());
            }
        }
        catch (Exception e)
        {
            log.warn("WeCom contact convert sync failed contactId={} externalUserid={}: {}",
                    contactId, externalUserid, e.getMessage());
        }
    }

    private String fetchRawJson(String externalUserid)
    {
        Optional<WecomExternalContactProfile> profileOpt = profileResolver.fetch(externalUserid);
        if (profileOpt.isPresent() && StringUtils.hasText(profileOpt.get().getRawJson()))
        {
            return profileOpt.get().getRawJson();
        }
        List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectByExternalUserid(externalUserid);
        for (CrmWecomExternalContact row : rows)
        {
            if (row != null && StringUtils.hasText(row.getRawJson()))
            {
                return row.getRawJson();
            }
        }
        return null;
    }

    private String resolveFollowUserid(String externalUserid, CrmLead lead)
    {
        if (lead != null && StringUtils.hasText(lead.getWecomFollowUserid()))
        {
            return lead.getWecomFollowUserid().trim();
        }
        try
        {
            String snUserid = SecurityUtils.getLoginUser().getUser().getSnUserid();
            if (StringUtils.hasText(snUserid))
            {
                return snUserid.trim();
            }
        }
        catch (Exception ignored)
        {
            // 无登录态
        }

        CrmLead mappedLead = crmLeadMapper.selectByWecomExternalUserid(externalUserid);
        if (mappedLead != null && StringUtils.hasText(mappedLead.getWecomFollowUserid()))
        {
            return mappedLead.getWecomFollowUserid().trim();
        }

        List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectByExternalUserid(externalUserid);
        if (!rows.isEmpty() && StringUtils.hasText(rows.get(0).getFollowUserid()))
        {
            return rows.get(0).getFollowUserid().trim();
        }
        return null;
    }

    private String resolveExternalUseridFromMapping(String leadId)
    {
        if (!StringUtils.hasText(leadId))
        {
            return null;
        }
        CrmWecomExternalContact query = new CrmWecomExternalContact();
        query.setCrmLeadId(leadId.trim());
        List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectList(query);
        if (rows == null || rows.isEmpty())
        {
            return null;
        }
        for (CrmWecomExternalContact row : rows)
        {
            if (row != null && StringUtils.hasText(row.getExternalUserid()))
            {
                return row.getExternalUserid().trim();
            }
        }
        return null;
    }

    private void updateMappingRow(String externalUserid, String followUserid, String contactId, String leadId,
            String rawJson, WecomFollowUserSnapshot follow)
    {
        CrmWecomExternalContact existing = null;
        if (StringUtils.hasText(followUserid))
        {
            existing = wecomExternalContactMapper.selectByExternalAndFollow(externalUserid, followUserid);
        }
        if (existing == null)
        {
            List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectByExternalUserid(externalUserid);
            if (!rows.isEmpty())
            {
                existing = rows.get(0);
            }
        }

        if (existing != null)
        {
            CrmWecomExternalContact upd = new CrmWecomExternalContact();
            upd.setId(existing.getId());
            upd.setCrmContactId(contactId);
            if (StringUtils.hasText(leadId))
            {
                upd.setCrmLeadId(leadId);
            }
            if (StringUtils.hasText(rawJson))
            {
                upd.setRawJson(rawJson);
            }
            wecomLeadEnrichSupport.applyFollowToExternalContactRow(upd, follow);
            upd.setUpdateTime(DateUtils.getNowDate());
            wecomExternalContactMapper.update(upd);
            return;
        }

        String effectiveLeadId = StringUtils.hasText(leadId) ? leadId : resolveLeadId(externalUserid);
        wecomExternalContactService.upsertFromCrmBinding(externalUserid, followUserid, contactId, effectiveLeadId);
        if (StringUtils.hasText(followUserid))
        {
            CrmWecomExternalContact inserted = wecomExternalContactMapper
                    .selectByExternalAndFollow(externalUserid, followUserid);
            if (inserted != null && (StringUtils.hasText(rawJson) || follow != null))
            {
                CrmWecomExternalContact upd = new CrmWecomExternalContact();
                upd.setId(inserted.getId());
                if (StringUtils.hasText(rawJson))
                {
                    upd.setRawJson(rawJson);
                }
                wecomLeadEnrichSupport.applyFollowToExternalContactRow(upd, follow);
                upd.setUpdateTime(DateUtils.getNowDate());
                wecomExternalContactMapper.update(upd);
            }
        }
    }

    private String resolveLeadId(String externalUserid)
    {
        List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectByExternalUserid(externalUserid);
        for (CrmWecomExternalContact row : rows)
        {
            if (row != null && StringUtils.hasText(row.getCrmLeadId()))
            {
                return row.getCrmLeadId();
            }
        }
        CrmLead lead = crmLeadMapper.selectByWecomExternalUserid(externalUserid);
        return lead != null ? lead.getLeadId() : null;
    }

    private static String firstNonBlank(String a, String b)
    {
        if (StringUtils.hasText(a))
        {
            return a.trim();
        }
        if (StringUtils.hasText(b))
        {
            return b.trim();
        }
        return null;
    }
}
