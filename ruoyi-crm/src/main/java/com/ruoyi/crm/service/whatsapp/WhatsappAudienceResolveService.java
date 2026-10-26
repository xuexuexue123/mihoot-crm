package com.ruoyi.crm.service.whatsapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.domain.vo.SegmentMemberVo;
import com.ruoyi.crm.mapper.CrmWhatsappAudienceMapper;
import com.ruoyi.crm.service.ICrmSegmentService;
import com.ruoyi.crm.service.support.IdentityNormalizer;

/**
 * 标签 / 分群 → WhatsApp wa_id 解析（预估与入队共用）。
 * <p>
 * 严格身份：仅认 {@code crm_customer_identity} 中的 {@code whatsapp_wa_id}，
 * 不回退 whatsapp_phone / mobile_phone 身份，也不用 Contact.mobile_phone 推导。
 */
@Service
public class WhatsappAudienceResolveService
{
    private static final int SEGMENT_PAGE_SIZE = 500;
    private static final int IDENTITY_BATCH = 500;

    public static final String REASON_NO_WHATSAPP_IDENTITY = "no_whatsapp_identity";

    @Autowired
    private CrmWhatsappAudienceMapper audienceMapper;

    @Autowired
    private ICrmSegmentService segmentService;

    /**
     * @param tagIds    CRM 标签 dict_value 列表（任意命中）
     * @param segmentId 分群 ID（字符串或数字均可，解析为 Long）
     */
    public Map<String, Object> resolve(String tenantId, List<String> tagIds, String segmentId)
    {
        boolean hasTags = tagIds != null && !tagIds.isEmpty();
        boolean hasSegment = StringUtils.isNotEmpty(segmentId);
        if (!hasTags && !hasSegment)
        {
            throw new ServiceException("tagIds or segmentId is required", 400);
        }

        Set<String> contactIds = new LinkedHashSet<>();

        if (hasTags)
        {
            List<String> cleaned = new ArrayList<>();
            for (String t : tagIds)
            {
                if (StringUtils.isNotEmpty(t))
                {
                    cleaned.add(t.trim());
                }
            }
            if (!cleaned.isEmpty())
            {
                List<Map<String, Object>> rows = audienceMapper.selectContactsByAnyTagIds(cleaned);
                if (rows != null)
                {
                    for (Map<String, Object> row : rows)
                    {
                        String cid = str(row.get("contactId"));
                        if (StringUtils.isEmpty(cid))
                        {
                            cid = str(row.get("contact_id"));
                        }
                        if (StringUtils.isNotEmpty(cid))
                        {
                            contactIds.add(cid);
                        }
                    }
                }
            }
        }

        if (hasSegment)
        {
            Long segId;
            try
            {
                segId = Long.parseLong(segmentId.trim());
            }
            catch (NumberFormatException e)
            {
                throw new ServiceException("Invalid segmentId: " + segmentId, 400);
            }
            int page = 1;
            while (true)
            {
                List<SegmentMemberVo> members = segmentService.selectMembers(
                        tenantId, segId, page, SEGMENT_PAGE_SIZE);
                if (members == null || members.isEmpty())
                {
                    break;
                }
                for (SegmentMemberVo m : members)
                {
                    if (m == null || StringUtils.isEmpty(m.getContactId()))
                    {
                        continue;
                    }
                    contactIds.add(m.getContactId());
                }
                if (members.size() < SEGMENT_PAGE_SIZE)
                {
                    break;
                }
                page++;
            }
        }

        List<String> waIds = new ArrayList<>();
        List<Map<String, Object>> skipped = new ArrayList<>();
        // waId -> contactId（去重 wa）
        Map<String, String> waToContact = new LinkedHashMap<>();

        List<String> allContactIds = new ArrayList<>(contactIds);
        Map<String, List<CrmCustomerIdentity>> identitiesByContact = loadIdentities(allContactIds);

        for (String contactId : allContactIds)
        {
            String waId = pickWaId(identitiesByContact.get(contactId));
            if (StringUtils.isEmpty(waId)
                    || !CrmWhatsappTemplateSendTaskServiceImpl.isValidWaId(waId))
            {
                Map<String, Object> skip = new HashMap<>();
                skip.put("contactId", contactId);
                skip.put("reason", REASON_NO_WHATSAPP_IDENTITY);
                skipped.add(skip);
                continue;
            }
            if (!waToContact.containsKey(waId))
            {
                waToContact.put(waId, contactId);
                waIds.add(waId);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("waIds", waIds);
        data.put("skipped", skipped);
        // 内部入队用：waId -> contactId
        data.put("_waContactMap", waToContact);
        return data;
    }

    /**
     * 解析结果转为入队用的条目列表（含 contactId，便于 item 落库）。
     */
    @SuppressWarnings("unchecked")
    public List<ResolvedRecipient> toRecipients(Map<String, Object> resolveResult)
    {
        List<ResolvedRecipient> list = new ArrayList<>();
        if (resolveResult == null)
        {
            return list;
        }
        List<String> waIds = (List<String>) resolveResult.get("waIds");
        Map<String, String> waContact = (Map<String, String>) resolveResult.get("_waContactMap");
        if (waIds == null)
        {
            return list;
        }
        for (String waId : waIds)
        {
            ResolvedRecipient r = new ResolvedRecipient();
            r.waId = waId;
            r.contactId = waContact != null ? waContact.get(waId) : null;
            list.add(r);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> skippedFrom(Map<String, Object> resolveResult)
    {
        if (resolveResult == null || resolveResult.get("skipped") == null)
        {
            return new ArrayList<>();
        }
        return (List<Map<String, Object>>) resolveResult.get("skipped");
    }

    private Map<String, List<CrmCustomerIdentity>> loadIdentities(List<String> contactIds)
    {
        Map<String, List<CrmCustomerIdentity>> map = new HashMap<>();
        if (contactIds == null || contactIds.isEmpty())
        {
            return map;
        }
        for (int i = 0; i < contactIds.size(); i += IDENTITY_BATCH)
        {
            List<String> batch = contactIds.subList(i, Math.min(i + IDENTITY_BATCH, contactIds.size()));
            List<CrmCustomerIdentity> rows = audienceMapper.selectWhatsappIdentitiesByContactIds(batch);
            if (rows == null)
            {
                continue;
            }
            for (CrmCustomerIdentity id : rows)
            {
                if (id == null || StringUtils.isEmpty(id.getContactId()))
                {
                    continue;
                }
                map.computeIfAbsent(id.getContactId(), k -> new ArrayList<>()).add(id);
            }
        }
        return map;
    }

    /**
     * 仅取 {@code whatsapp_wa_id}；无则返回 null（调用方记 skipped）。
     */
    static String pickWaId(List<CrmCustomerIdentity> identities)
    {
        if (identities == null)
        {
            return null;
        }
        for (CrmCustomerIdentity id : identities)
        {
            if (id != null
                    && IdentityNormalizer.TYPE_WHATSAPP_WA_ID.equals(id.getIdentityType())
                    && StringUtils.isNotEmpty(id.getIdentityValue()))
            {
                return id.getIdentityValue().trim();
            }
        }
        return null;
    }

    private static String str(Object o)
    {
        return o == null ? null : String.valueOf(o);
    }

    public static final class ResolvedRecipient
    {
        public String waId;
        public String contactId;
    }
}
