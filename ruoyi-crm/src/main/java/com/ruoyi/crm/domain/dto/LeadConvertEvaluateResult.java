package com.ruoyi.crm.domain.dto;

import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;

/**
 * POST /crm/lead/{leadId}/convert/evaluate 响应 data
 */
public class LeadConvertEvaluateResult
{
    private boolean eligible;
    private String reason;
    private String orgId;
    private LeadConvertLeadSummary lead;
    private LeadConvertConfigVo config;

    public boolean isEligible()
    {
        return eligible;
    }

    public void setEligible(boolean eligible)
    {
        this.eligible = eligible;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public LeadConvertLeadSummary getLead()
    {
        return lead;
    }

    public void setLead(LeadConvertLeadSummary lead)
    {
        this.lead = lead;
    }

    public LeadConvertConfigVo getConfig()
    {
        return config;
    }

    public void setConfig(LeadConvertConfigVo config)
    {
        this.config = config;
    }
}
