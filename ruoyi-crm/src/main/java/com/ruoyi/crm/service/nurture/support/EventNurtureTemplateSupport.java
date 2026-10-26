package com.ruoyi.crm.service.nurture.support;

import java.util.HashMap;
import java.util.Map;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;

/**
 * 培育步骤模板变量渲染
 */
public final class EventNurtureTemplateSupport
{
    private EventNurtureTemplateSupport()
    {
    }

    public static Map<String, String> buildVars(CrmEvent event, CrmEventNurtureEnrollment enrollment,
            EventNurtureAudienceSnapshot audience)
    {
        Map<String, String> vars = new HashMap<>();
        if (event != null)
        {
            vars.put("eventId", nullToEmpty(event.getEventId()));
            vars.put("eventSubject", nullToEmpty(event.getSubject()));
        }
        if (enrollment != null)
        {
            vars.put("leadId", nullToEmpty(enrollment.getLeadId()));
            vars.put("contactId", nullToEmpty(enrollment.getContactId()));
            vars.put("displayName", nullToEmpty(enrollment.getDisplayName()));
            vars.put("mobilePhone", nullToEmpty(enrollment.getMobilePhone()));
        }
        if (audience != null)
        {
            if (StringUtils.isEmpty(vars.get("leadId")))
            {
                vars.put("leadId", nullToEmpty(audience.getLeadId()));
            }
            if (StringUtils.isEmpty(vars.get("contactId")))
            {
                vars.put("contactId", nullToEmpty(audience.getContactId()));
            }
            if (StringUtils.isEmpty(vars.get("displayName")))
            {
                vars.put("displayName", nullToEmpty(audience.getDisplayName()));
            }
            if (StringUtils.isEmpty(vars.get("mobilePhone")))
            {
                vars.put("mobilePhone", nullToEmpty(audience.getMobilePhone()));
            }
        }
        return vars;
    }

    public static String render(String template, Map<String, String> vars)
    {
        if (template == null)
        {
            return "";
        }
        String result = template;
        if (vars != null)
        {
            for (Map.Entry<String, String> entry : vars.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue() != null ? entry.getValue() : "";
                result = result.replace("{{" + key + "}}", value);
            }
        }
        return result;
    }

    private static String nullToEmpty(String value)
    {
        return value != null ? value : "";
    }
}
