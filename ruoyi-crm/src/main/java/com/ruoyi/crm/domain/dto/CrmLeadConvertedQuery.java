package com.ruoyi.crm.domain.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 已转化线索列表查询
 */
public class CrmLeadConvertedQuery extends BaseEntity
{
    private String leadType = "business";
    private String leadName;
    private String mobilePhone;
    private String owner;
    private String contactId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date convertTimeFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date convertTimeTo;

    private Long currentUserId;

    public String getLeadType()
    {
        return leadType;
    }

    public void setLeadType(String leadType)
    {
        this.leadType = leadType;
    }

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public Date getConvertTimeFrom()
    {
        return convertTimeFrom;
    }

    public void setConvertTimeFrom(Date convertTimeFrom)
    {
        this.convertTimeFrom = convertTimeFrom;
    }

    public Date getConvertTimeTo()
    {
        return convertTimeTo;
    }

    public void setConvertTimeTo(Date convertTimeTo)
    {
        this.convertTimeTo = convertTimeTo;
    }

    public Long getCurrentUserId()
    {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId)
    {
        this.currentUserId = currentUserId;
    }
}
