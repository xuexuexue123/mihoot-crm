package com.ruoyi.crm.domain.dto;

import java.util.HashMap;
import java.util.Map;

import com.ruoyi.crm.domain.CrmLead;

/**
 * 9090 → Next BFF POST /workflow-trigger/lead-convert/ 请求体
 */
public class LeadConvertWorkflowTriggerRequest
{
    private String leadId;
    private Map<String, Object> lead;
    private String trigger;
    private String orgId;
    private String eventId;

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public Map<String, Object> getLead()
    {
        return lead;
    }

    public void setLead(Map<String, Object> lead)
    {
        this.lead = lead;
    }

    public String getTrigger()
    {
        return trigger;
    }

    public void setTrigger(String trigger)
    {
        this.trigger = trigger;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getEventId()
    {
        return eventId;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public static LeadConvertWorkflowTriggerRequest from(CrmLead lead, String trigger, String orgId, String eventId)
    {
        LeadConvertWorkflowTriggerRequest req = new LeadConvertWorkflowTriggerRequest();
        if (lead == null)
        {
            return req;
        }
        req.setLeadId(lead.getLeadId());
        req.setTrigger(trigger);
        req.setOrgId(orgId);
        req.setEventId(eventId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("leadId", lead.getLeadId());
        summary.put("leadName", firstNonBlank(lead.getLeadName(), lead.getContactName()));
        summary.put("contactName", firstNonBlank(lead.getContactName(), lead.getLeadName()));
        summary.put("mobilePhone", lead.getMobilePhone());
        summary.put("orgId", orgId != null ? orgId : lead.getOrgId());
        summary.put("stage", lead.getStage());
        req.setLead(summary);
        return req;
    }

    private static String firstNonBlank(String a, String b)
    {
        if (a != null && !a.trim().isEmpty())
        {
            return a.trim();
        }
        if (b != null && !b.trim().isEmpty())
        {
            return b.trim();
        }
        return null;
    }
}
