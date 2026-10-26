package com.ruoyi.crm.domain.dto;

/**
 * 签到/Lead 更新响应中的 evaluate 摘要
 */
public class LeadConvertEvaluateSummary
{
    private boolean eligible;
    private String reason;

    public LeadConvertEvaluateSummary()
    {
    }

    public LeadConvertEvaluateSummary(boolean eligible, String reason)
    {
        this.eligible = eligible;
        this.reason = reason;
    }

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

    public static LeadConvertEvaluateSummary from(LeadConvertEvaluateResult result)
    {
        if (result == null)
        {
            return null;
        }
        return new LeadConvertEvaluateSummary(result.isEligible(), result.getReason());
    }
}
