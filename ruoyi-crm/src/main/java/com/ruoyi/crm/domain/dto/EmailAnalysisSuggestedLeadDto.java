package com.ruoyi.crm.domain.dto;

/**
 * 邮件 AI 分析建议线索
 */
public class EmailAnalysisSuggestedLeadDto
{
    private String leadName;
    private String workEmail;
    private String description;
    private String leadSource;

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
    }

    public String getWorkEmail()
    {
        return workEmail;
    }

    public void setWorkEmail(String workEmail)
    {
        this.workEmail = workEmail;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLeadSource()
    {
        return leadSource;
    }

    public void setLeadSource(String leadSource)
    {
        this.leadSource = leadSource;
    }
}
