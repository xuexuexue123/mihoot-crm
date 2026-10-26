package com.ruoyi.crm.service.support;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadConvertCopyOptions;

/**
 * Lead → Contact 字段映射（只补不盖）
 */
@Component
public class LeadToContactFieldMapper
{
    /**
     * 新建 Contact 时从 Lead 填充全部可用字段
     */
    public CrmContact mapNewContact(CrmLead lead, String orgId, String name, String mobilePhone,
            LeadConvertCopyOptions options)
    {
        CrmContact contact = new CrmContact();
        contact.setOrgId(orgId);
        contact.setName(name);
        contact.setMobilePhone(mobilePhone);
        applyStandardFields(contact, lead, null, options, true);
        return contact;
    }

    /**
     * 合并已有 Contact：只补空字段
     */
    public CrmContact mapMergePatch(CrmLead lead, CrmContact existing, LeadConvertCopyOptions options)
    {
        CrmContact patch = new CrmContact();
        patch.setContactId(existing.getContactId());
        applyStandardFields(patch, lead, existing, options, false);
        return patch;
    }

    private void applyStandardFields(CrmContact target, CrmLead lead, CrmContact existing,
            LeadConvertCopyOptions options, boolean isNew)
    {
        if (lead == null || options == null)
        {
            return;
        }
        fillIfBlank(target, existing, isNew, firstNonBlank(lead.getContactName(), lead.getLeadName()),
                existing != null ? existing.getName() : null, target::setName);
        fillIfBlank(target, existing, isNew, lead.getMobilePhone(),
                existing != null ? existing.getMobilePhone() : null, target::setMobilePhone);
        fillIfBlank(target, existing, isNew, lead.getWorkPhone(),
                existing != null ? existing.getWorkPhone() : null, target::setWorkPhone);
        fillIfBlank(target, existing, isNew, lead.getWorkEmail(),
                existing != null ? existing.getWorkEmail() : null, target::setWorkEmail);
        fillIfBlank(target, existing, isNew, lead.getOrgId(),
                existing != null ? existing.getOrgId() : null, target::setOrgId);
        fillIfBlank(target, existing, isNew, lead.getOwner(),
                existing != null ? existing.getOwner() : null, target::setOwner);
        fillIfBlank(target, existing, isNew, lead.getTerritory(),
                existing != null ? existing.getTerritory() : null, target::setTerritory);
        fillIfBlank(target, existing, isNew, lead.getAvatar(),
                existing != null ? existing.getAvatar() : null, target::setAvatar);

        if (options.isCopyTags())
        {
            fillIfBlank(target, existing, isNew, lead.getTag(),
                    existing != null ? existing.getTag() : null, target::setTag);
        }

        if (options.isCopyAttribution())
        {
            if (StringUtils.hasText(lead.getSid()))
            {
                if (isNew || existing == null || !StringUtils.hasText(existing.getSid()))
                {
                    target.setSid(lead.getSid().trim());
                }
            }
            if (StringUtils.hasText(lead.getLeadSource()))
            {
                if (isNew || existing == null || !StringUtils.hasText(existing.getOriginalSource()))
                {
                    target.setOriginalSource(lead.getLeadSource());
                }
            }
        }

        if (options.isCopyWecomIds())
        {
            fillIfBlank(target, existing, isNew, lead.getWecomExternalUserid(),
                    existing != null ? existing.getWecomExternalUserid() : null, target::setWecomExternalUserid);
            fillIfBlank(target, existing, isNew, lead.getWecomFollowUserid(),
                    existing != null ? existing.getWecomFollowUserid() : null, target::setWecomFollowUserid);
        }

        if (lead.getUserId() != null && (isNew || existing == null || existing.getUserId() == null))
        {
            target.setUserId(lead.getUserId());
        }
        if (lead.getDeptId() != null && (isNew || existing == null || existing.getDeptId() == null))
        {
            target.setDeptId(lead.getDeptId());
        }

        if (options.isCopyCustomFields())
        {
            String merged = mergeCustomFieldJson(
                    existing != null ? existing.getCustomField() : null,
                    lead.getCustomField());
            if (StringUtils.hasText(merged))
            {
                target.setCustomField(merged);
            }
        }
    }

    /**
     * Contact customField：Lead 字段按 fieldName 合并，Contact 已有值优先
     */
    public static String mergeCustomFieldJson(String existingCustomField, String leadCustomField)
    {
        JSONObject existing = MpLeadIdentitySupport.parseCustomField(existingCustomField);
        JSONObject incoming = MpLeadIdentitySupport.parseCustomField(leadCustomField);
        for (String key : incoming.keySet())
        {
            if (!existing.containsKey(key) || existing.get(key) == null
                    || (existing.get(key) instanceof String && !StringUtils.hasText(existing.getString(key))))
            {
                existing.put(key, incoming.get(key));
            }
        }
        return existing.isEmpty() ? null : existing.toJSONString();
    }

    private static void fillIfBlank(CrmContact target, CrmContact existing, boolean isNew,
            String value, String existingValue, java.util.function.Consumer<String> setter)
    {
        if (!StringUtils.hasText(value))
        {
            return;
        }
        if (isNew || !StringUtils.hasText(existingValue))
        {
            setter.accept(value.trim());
        }
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
