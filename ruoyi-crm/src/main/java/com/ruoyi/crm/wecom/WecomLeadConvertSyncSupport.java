package com.ruoyi.crm.wecom;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmConversionHistory;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmSubjectMap;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.service.ICrmSubjectMapService;

/**
 * Lead 转化为 Contact 后，闭环企微身份（identity + 映射表 + subject_map + 展示字段）。
 */
@Component
public class WecomLeadConvertSyncSupport
{
    private static final Logger log = LoggerFactory.getLogger(WecomLeadConvertSyncSupport.class);

    private static final String LEAD_SOURCE_WECOM = "wecom";

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CrmWecomExternalContactMapper wecomExternalContactMapper;

    @Autowired
    private WecomExternalContactProfileResolver profileResolver;

    @Autowired
    private WecomIdentityResolutionSupport identityResolutionSupport;

    @Autowired
    private WecomContactConvertSyncSupport wecomContactConvertSyncSupport;

    @Autowired
    private ICrmSubjectMapService subjectMapService;

    /**
     * Lead 转 Contact 后最佳努力同步企微数据；失败不抛异常。
     */
    public void syncAfterLeadConverted(String leadId, String contactId)
    {
        if (!StringUtils.hasText(leadId) || !StringUtils.hasText(contactId))
        {
            return;
        }
        try
        {
            CrmLead lead = crmLeadMapper.selectCrmLeadByLeadId(leadId.trim());
            CrmContact contact = crmContactMapper.selectCrmContactByContactId(contactId.trim());
            if (lead == null || contact == null)
            {
                return;
            }
            if (!isWecomLead(lead))
            {
                upsertSubjectMap(leadId.trim(), contactId.trim());
                return;
            }

            String externalUserid = resolveExternalUserid(lead);
            if (!StringUtils.hasText(externalUserid))
            {
                upsertSubjectMap(leadId.trim(), contactId.trim());
                return;
            }
            externalUserid = externalUserid.trim();

            mergeLeadFieldsToContact(lead, contact, externalUserid);

            String unionId = resolveUnionId(externalUserid);
            identityResolutionSupport.bindWecomIdentities(externalUserid, unionId, contactId.trim(),
                    WecomIdentityResolutionSupport.SOURCE_LEAD_CONVERT);

            CrmContact refreshed = crmContactMapper.selectCrmContactByContactId(contactId.trim());
            if (refreshed != null)
            {
                if (!StringUtils.hasText(refreshed.getWecomExternalUserid()))
                {
                    refreshed.setWecomExternalUserid(externalUserid);
                }
                wecomContactConvertSyncSupport.syncAfterLeadConverted(lead, refreshed);
            }

            upsertSubjectMap(leadId.trim(), contactId.trim());
        }
        catch (Exception e)
        {
            log.warn("WeCom lead convert sync failed leadId={} contactId={}: {}",
                    leadId, contactId, e.getMessage());
        }
    }

    /**
     * conversionhistory 是否为 Lead → Contact 转化。
     */
    public static boolean isLeadToContactConversion(CrmConversionHistory history)
    {
        if (history == null)
        {
            return false;
        }
        String type = history.getConversionType();
        if (type == null || type.trim().isEmpty())
        {
            return false;
        }
        if (!"Contact".equalsIgnoreCase(type.trim()))
        {
            return false;
        }
        return StringUtils.hasText(history.getLeadId()) && StringUtils.hasText(history.getTargetId());
    }

    /**
     * Lead 是否首次关联 Contact（contactId 从无到有）。
     */
    public static boolean isNewContactLink(CrmLead before, CrmLead after)
    {
        if (before == null || after == null)
        {
            return false;
        }
        return !StringUtils.hasText(before.getContactId()) && StringUtils.hasText(after.getContactId());
    }

    private boolean isWecomLead(CrmLead lead)
    {
        if (lead == null)
        {
            return false;
        }
        if (StringUtils.hasText(lead.getWecomExternalUserid()))
        {
            return true;
        }
        String source = lead.getLeadSource();
        if (source == null)
        {
            return false;
        }
        String normalized = source.trim().toLowerCase();
        return normalized.equals(LEAD_SOURCE_WECOM) || normalized.startsWith(LEAD_SOURCE_WECOM + ":");
    }

    private String resolveExternalUserid(CrmLead lead)
    {
        if (StringUtils.hasText(lead.getWecomExternalUserid()))
        {
            return lead.getWecomExternalUserid().trim();
        }
        CrmWecomExternalContact query = new CrmWecomExternalContact();
        query.setCrmLeadId(lead.getLeadId());
        List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectList(query);
        if (rows == null)
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

    private void mergeLeadFieldsToContact(CrmLead lead, CrmContact contact, String externalUserid)
    {
        CrmContact upd = new CrmContact();
        upd.setContactId(contact.getContactId());
        upd.setWecomExternalUserid(externalUserid);

        if (!StringUtils.hasText(contact.getName()))
        {
            String displayName = firstNonBlank(lead.getContactName(), lead.getLeadName());
            if (StringUtils.hasText(displayName))
            {
                upd.setName(displayName.trim());
            }
        }
        if (!StringUtils.hasText(contact.getAvatar()) && StringUtils.hasText(lead.getAvatar()))
        {
            upd.setAvatar(lead.getAvatar().trim());
        }
        if (!StringUtils.hasText(contact.getTag()) && StringUtils.hasText(lead.getTag()))
        {
            upd.setTag(lead.getTag());
        }

        String rawJson = fetchRawJsonFromMapping(externalUserid);
        if (StringUtils.hasText(rawJson))
        {
            CrmContact wecomPatch = new CrmContact();
            wecomPatch.setContactId(contact.getContactId());
            WecomContactInfoSyncSupport.fillWecomInfoFromRawJson(wecomPatch, rawJson);
            if (!StringUtils.hasText(contact.getWecomName()) && StringUtils.hasText(wecomPatch.getWecomName()))
            {
                upd.setWecomName(wecomPatch.getWecomName());
            }
            if (!StringUtils.hasText(contact.getWecomAvatar()) && StringUtils.hasText(wecomPatch.getWecomAvatar()))
            {
                upd.setWecomAvatar(wecomPatch.getWecomAvatar());
            }
            if (contact.getWecomGender() == null && wecomPatch.getWecomGender() != null)
            {
                upd.setWecomGender(wecomPatch.getWecomGender());
            }
            if (contact.getWecomType() == null && wecomPatch.getWecomType() != null)
            {
                upd.setWecomType(wecomPatch.getWecomType());
            }
        }

        upd.setUpdateTime(DateUtils.getNowDate());
        crmContactMapper.updateCrmContact(upd);
    }

    private String resolveUnionId(String externalUserid)
    {
        Optional<WecomExternalContactProfile> profileOpt = profileResolver.fetch(externalUserid);
        if (profileOpt.isPresent() && StringUtils.hasText(profileOpt.get().getUnionId()))
        {
            return profileOpt.get().getUnionId().trim();
        }
        String rawJson = fetchRawJsonFromMapping(externalUserid);
        WecomExternalContactProfile profile = WecomExternalContactProfileResolver
                .parseProfileFromRaw(rawJson, externalUserid);
        if (profile != null && StringUtils.hasText(profile.getUnionId()))
        {
            return profile.getUnionId().trim();
        }
        return null;
    }

    private String fetchRawJsonFromMapping(String externalUserid)
    {
        List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectByExternalUserid(externalUserid);
        if (rows == null)
        {
            return null;
        }
        for (CrmWecomExternalContact row : rows)
        {
            if (row != null && StringUtils.hasText(row.getRawJson()))
            {
                return row.getRawJson();
            }
        }
        return null;
    }

    private void upsertSubjectMap(String leadId, String contactId)
    {
        CrmSubjectMap map = new CrmSubjectMap();
        map.setParentSubjectType("Contact");
        map.setParentSubjectId(contactId);
        map.setChildSubjectType("Lead");
        map.setChildSubjectId(leadId);
        subjectMapService.upsert(map);
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
