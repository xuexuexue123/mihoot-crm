package com.ruoyi.crm.domain.vo;

/**
 * 问卷投放统计
 */
public class SurveyOutreachStatsVo
{
    private long wecomSent;
    private long mpSent;
    private long responses;
    private double completionRate;

    public long getWecomSent()
    {
        return wecomSent;
    }

    public void setWecomSent(long wecomSent)
    {
        this.wecomSent = wecomSent;
    }

    public long getMpSent()
    {
        return mpSent;
    }

    public void setMpSent(long mpSent)
    {
        this.mpSent = mpSent;
    }

    public long getResponses()
    {
        return responses;
    }

    public void setResponses(long responses)
    {
        this.responses = responses;
    }

    public double getCompletionRate()
    {
        return completionRate;
    }

    public void setCompletionRate(double completionRate)
    {
        this.completionRate = completionRate;
    }
}
