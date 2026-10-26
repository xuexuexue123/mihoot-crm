package com.ruoyi.crm.domain.vo;

import com.ruoyi.crm.domain.vo.LeadSourceSummaryVo;

/**
 * source-lead API 响应
 */
public class LeadSourceLeadResponse
{
    private boolean found;
    private LeadSourceSummaryVo lead;

    public boolean isFound()
    {
        return found;
    }

    public void setFound(boolean found)
    {
        this.found = found;
    }

    public LeadSourceSummaryVo getLead()
    {
        return lead;
    }

    public void setLead(LeadSourceSummaryVo lead)
    {
        this.lead = lead;
    }
}
