package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadDedupeMatch;
import com.ruoyi.crm.domain.dto.LeadDedupeQuery;
import com.ruoyi.crm.domain.vo.LeadRecycleDedupeConfigVo;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadRecycleConfigService;
import com.ruoyi.crm.service.support.IdentityNormalizer;
import com.ruoyi.crm.service.support.LeadRecyclePolicyDefaults;

@Service
public class LeadDedupeService
{
    @Autowired
    private ICrmLeadRecycleConfigService recycleConfigService;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    public LeadDedupeMatch findDuplicateLead(CrmLead candidate)
    {
        if (candidate == null)
        {
            return null;
        }
        LeadRecycleDedupeConfigVo config = recycleConfigService.getDedupeConfig();
        List<String> fields = config.getLeadMatchFields();
        if (fields == null || fields.isEmpty())
        {
            fields = LeadRecyclePolicyDefaults.DEFAULT_LEAD_MATCH_FIELDS;
        }
        LeadDedupeQuery query = new LeadDedupeQuery();
        query.setExcludeLeadId(candidate.getLeadId());
        String matchedField = null;
        for (String field : fields)
        {
            String value = leadFieldValue(candidate, field);
            if (!StringUtils.hasText(value))
            {
                continue;
            }
            matchedField = field;
            applyLeadQueryField(query, field, value);
        }
        if (matchedField == null
                && query.getMobilePhone() == null && query.getWecomExternalUserid() == null
                && query.getWorkPhone() == null && query.getWorkEmail() == null && query.getWechat() == null)
        {
            return null;
        }
        CrmLead hit = crmLeadMapper.selectUnconvertedDuplicate(query);
        if (hit == null)
        {
            return null;
        }
        return LeadDedupeMatch.of("lead", hit.getLeadId(), firstMatchedLeadField(hit, candidate, fields));
    }

    public LeadDedupeMatch findDuplicateContact(CrmLead lead, String explicitContactId)
    {
        if (StringUtils.hasText(explicitContactId))
        {
            CrmContact byId = crmContactMapper.selectCrmContactByContactId(explicitContactId.trim());
            if (byId != null)
            {
                return LeadDedupeMatch.of("contact", byId.getContactId(), "contactId");
            }
        }
        if (lead == null)
        {
            return null;
        }
        LeadRecycleDedupeConfigVo config = recycleConfigService.getDedupeConfig();
        List<String> fields = config.getContactMatchFields();
        if (fields == null || fields.isEmpty())
        {
            fields = LeadRecyclePolicyDefaults.DEFAULT_CONTACT_MATCH_FIELDS;
        }
        for (String field : fields)
        {
            String value = contactFieldValueFromLead(lead, field);
            if (!StringUtils.hasText(value))
            {
                continue;
            }
            String identityType = toIdentityType(field);
            if (identityType != null)
            {
                String contactId = identityService.findContactByIdentity(identityType, value);
                if (StringUtils.hasText(contactId))
                {
                    return LeadDedupeMatch.of("contact", contactId, field);
                }
            }
            CrmContact probe = new CrmContact();
            applyContactProbe(probe, field, value);
            List<CrmContact> matches = crmContactMapper.checkContactExisting(probe);
            if (matches != null && !matches.isEmpty() && matches.get(0).getContactId() != null)
            {
                return LeadDedupeMatch.of("contact", matches.get(0).getContactId(), field);
            }
        }
        return null;
    }

    public CrmContact loadContact(String contactId)
    {
        if (!StringUtils.hasText(contactId))
        {
            return null;
        }
        return crmContactMapper.selectCrmContactByContactId(contactId.trim());
    }

    private static void applyLeadQueryField(LeadDedupeQuery query, String field, String value)
    {
        switch (field)
        {
            case "mobilePhone":
                query.setMobilePhone(IdentityNormalizer.normalizeMobile(value));
                break;
            case "wecomExternalUserid":
                query.setWecomExternalUserid(value.trim());
                break;
            case "workPhone":
                query.setWorkPhone(IdentityNormalizer.normalizeMobile(value));
                break;
            case "workEmail":
                query.setWorkEmail(value.trim().toLowerCase());
                break;
            case "wechat":
            case "wechat_id":
                query.setWechat(value.trim());
                break;
            default:
                break;
        }
    }

    private static String leadFieldValue(CrmLead lead, String field)
    {
        if (lead == null || field == null)
        {
            return null;
        }
        switch (field)
        {
            case "mobilePhone":
                return lead.getMobilePhone();
            case "wecomExternalUserid":
                return lead.getWecomExternalUserid();
            case "workPhone":
                return lead.getWorkPhone();
            case "workEmail":
                return lead.getWorkEmail();
            case "wechat":
            case "wechat_id":
                return jsonCustom(lead.getCustomField(), "wechat", "wechat_id");
            default:
                return null;
        }
    }

    private static String contactFieldValueFromLead(CrmLead lead, String field)
    {
        if (lead == null || field == null)
        {
            return null;
        }
        switch (field)
        {
            case "mobilePhone":
                return lead.getMobilePhone();
            case "wecomExternalUserid":
                return lead.getWecomExternalUserid();
            case "workEmail":
                return lead.getWorkEmail();
            case "personalEmail":
                return jsonCustom(lead.getCustomField(), "personalEmail", "personal_email");
            case "wechat_unionid":
                return jsonCustom(lead.getCustomField(), "wechat_unionid", "wechatUnionid");
            case "wechat_mp_openid":
                return lead.getWechatMpOpenid();
            default:
                return null;
        }
    }

    private static String toIdentityType(String field)
    {
        if (field == null)
        {
            return null;
        }
        switch (field)
        {
            case "mobilePhone":
                return "mobile_phone";
            case "wecomExternalUserid":
                return "wecom_external_userid";
            case "workEmail":
            case "personalEmail":
                return "email";
            case "wechat_unionid":
                return "wechat_unionid";
            case "wechat_mp_openid":
                return "wechat_mp_openid";
            default:
                return null;
        }
    }

    private static void applyContactProbe(CrmContact probe, String field, String value)
    {
        if ("mobilePhone".equals(field))
        {
            probe.setMobilePhone(value);
        }
        else if ("workEmail".equals(field) || "personalEmail".equals(field))
        {
            probe.setWorkEmail(value);
        }
        else if ("wecomExternalUserid".equals(field))
        {
            probe.setWecomExternalUserid(value);
        }
    }

    private static String firstMatchedLeadField(CrmLead hit, CrmLead candidate, List<String> fields)
    {
        for (String field : fields)
        {
            String left = leadFieldValue(hit, field);
            String right = leadFieldValue(candidate, field);
            if (!StringUtils.hasText(left) || !StringUtils.hasText(right))
            {
                continue;
            }
            if ("mobilePhone".equals(field) || "workPhone".equals(field))
            {
                if (IdentityNormalizer.normalizeMobile(left).equals(IdentityNormalizer.normalizeMobile(right)))
                {
                    return field;
                }
            }
            else if (left.trim().equalsIgnoreCase(right.trim()))
            {
                return field;
            }
        }
        return fields.isEmpty() ? "mobilePhone" : fields.get(0);
    }

    private static String jsonCustom(String customField, String... keys)
    {
        if (!StringUtils.hasText(customField) || keys == null)
        {
            return null;
        }
        try
        {
            JSONObject json = JSON.parseObject(customField);
            if (json == null)
            {
                return null;
            }
            for (String key : keys)
            {
                String value = json.getString(key);
                if (StringUtils.hasText(value))
                {
                    return value;
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }
}
