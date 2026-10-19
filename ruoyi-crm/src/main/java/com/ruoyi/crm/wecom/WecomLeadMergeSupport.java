package com.ruoyi.crm.wecom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.domain.dto.wecom.WecomFollowUserSnapshot;
import com.ruoyi.crm.service.ICrmLeadService;

/**
 * 企微回调命中已有 Initial Lead 时的只补不盖合并。
 */
@Component
public class WecomLeadMergeSupport
{
    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private WecomLeadEnrichSupport wecomLeadEnrichSupport;

    @Autowired
    private WecomFollowUserResolver wecomFollowUserResolver;

    /**
     * 将企微渠道字段合并进已有 Lead 并持久化。
     *
     * @return 更新后的 leadId
     */
    public String mergeWecomIntoExistingLead(CrmLead existing, WecomExternalContactProfile profile,
            WecomFollowUserSnapshot follow, String externalUserid, String followUserid,
            String displayName, WecomStateParseResult parsed, String mobileFromRemark, String createBy)
    {
        if (existing == null || !StringUtils.hasText(existing.getLeadId()))
        {
            return null;
        }

        CrmLead patch = new CrmLead();
        patch.setLeadId(existing.getLeadId());
        patch.setUpdateTime(DateUtils.getNowDate());
        patch.setUpdateBy(createBy != null ? createBy : "wecom-callback");

        mergeSidAndDescription(existing, patch, parsed);
        mergeLeadSource(existing, patch, parsed);
        mergeMobile(existing, patch, mobileFromRemark);

        wecomLeadEnrichSupport.enrichNewLead(patch, profile, follow, externalUserid, followUserid, displayName);
        mergeNonBlankLeadFields(existing, patch);

        if (existing.getUserId() == null)
        {
            wecomFollowUserResolver.applyLeadOwnership(patch, followUserid);
        }

        crmLeadService.updateCrmLead(patch);
        return existing.getLeadId();
    }

    private static void mergeSidAndDescription(CrmLead existing, CrmLead patch, WecomStateParseResult parsed)
    {
        if (parsed == null || !StringUtils.hasText(parsed.getCrmEventId()))
        {
            return;
        }
        String eventId = parsed.getCrmEventId().trim();
        if (!StringUtils.hasText(existing.getSid()))
        {
            patch.setSid(eventId);
            return;
        }
        if (!eventId.equals(existing.getSid().trim()))
        {
            String desc = existing.getDescription() != null ? existing.getDescription() : "";
            String marker = ";wecom_event=" + eventId;
            if (!desc.contains(marker))
            {
                patch.setDescription(desc + marker);
            }
        }
    }

    private static void mergeLeadSource(CrmLead existing, CrmLead patch, WecomStateParseResult parsed)
    {
        if (parsed == null || !StringUtils.hasText(parsed.getLeadSource()))
        {
            return;
        }
        String incoming = parsed.getLeadSource().trim();
        String current = existing.getLeadSource();
        if (!StringUtils.hasText(current))
        {
            patch.setLeadSource(incoming);
            return;
        }
        if (current.startsWith("wecom"))
        {
            patch.setLeadSource(incoming);
            return;
        }
        String desc = patch.getDescription() != null ? patch.getDescription()
                : (existing.getDescription() != null ? existing.getDescription() : "");
        String marker = ";wecom_source=" + incoming;
        if (!desc.contains(marker))
        {
            patch.setDescription(desc + marker);
        }
    }

    private static void mergeMobile(CrmLead existing, CrmLead patch, String mobileFromRemark)
    {
        if (StringUtils.hasText(existing.getMobilePhone()) || !StringUtils.hasText(mobileFromRemark))
        {
            return;
        }
        patch.setMobilePhone(mobileFromRemark.trim());
    }

    private static void mergeNonBlankLeadFields(CrmLead existing, CrmLead patch)
    {
        if (!StringUtils.hasText(existing.getLeadName()) && StringUtils.hasText(patch.getLeadName()))
        {
            // keep patch value
        }
        else if (StringUtils.hasText(existing.getLeadName()))
        {
            patch.setLeadName(null);
        }

        if (!StringUtils.hasText(existing.getContactName()) && StringUtils.hasText(patch.getContactName()))
        {
            // keep
        }
        else if (StringUtils.hasText(existing.getContactName()))
        {
            patch.setContactName(null);
        }

        if (!StringUtils.hasText(existing.getAvatar()) && StringUtils.hasText(patch.getAvatar()))
        {
            // keep
        }
        else if (StringUtils.hasText(existing.getAvatar()))
        {
            patch.setAvatar(null);
        }

        if (!StringUtils.hasText(existing.getFullName()) && StringUtils.hasText(patch.getFullName()))
        {
            // keep
        }
        else if (StringUtils.hasText(existing.getFullName()))
        {
            patch.setFullName(null);
        }

        if (!StringUtils.hasText(existing.getTag()) && StringUtils.hasText(patch.getTag()))
        {
            // keep
        }
        else if (StringUtils.hasText(existing.getTag()))
        {
            patch.setTag(null);
        }

        if (!StringUtils.hasText(existing.getCustomField()) && StringUtils.hasText(patch.getCustomField()))
        {
            // keep
        }
        else if (StringUtils.hasText(existing.getCustomField()))
        {
            patch.setCustomField(null);
        }
    }
}
