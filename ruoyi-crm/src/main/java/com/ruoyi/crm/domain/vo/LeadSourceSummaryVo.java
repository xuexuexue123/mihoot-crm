package com.ruoyi.crm.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Contact 反查来源 Lead 摘要
 */
public class LeadSourceSummaryVo
{
    private String leadId;
    private String leadName;
    private String leadSource;
    private String owner;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date convertTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String contactId;

    public String getLeadId()
    {
        return leadId;
    }

    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
    }

    public String getLeadSource()
    {
        return leadSource;
    }

    public void setLeadSource(String leadSource)
    {
        this.leadSource = leadSource;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public Date getConvertTime()
    {
        return convertTime;
    }

    public void setConvertTime(Date convertTime)
    {
        this.convertTime = convertTime;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }
}
