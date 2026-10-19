package com.ruoyi.crm.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * GET /crm/lead/convert/config 响应
 */
public class LeadConvertConfigVo
{
    private Boolean autoConvertOnFirstVisit;
    private Boolean autoConvertOnCheckIn;
    private Boolean autoConvertOnRegistration;
    private Boolean mergeOnDuplicatePhone;
    private String defaultOrgId;
    private String contactOwnerMode;
    private Long contactOwnerUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Boolean getAutoConvertOnFirstVisit()
    {
        return autoConvertOnFirstVisit;
    }

    public void setAutoConvertOnFirstVisit(Boolean autoConvertOnFirstVisit)
    {
        this.autoConvertOnFirstVisit = autoConvertOnFirstVisit;
    }

    public Boolean getAutoConvertOnCheckIn()
    {
        return autoConvertOnCheckIn;
    }

    public void setAutoConvertOnCheckIn(Boolean autoConvertOnCheckIn)
    {
        this.autoConvertOnCheckIn = autoConvertOnCheckIn;
    }

    public Boolean getAutoConvertOnRegistration()
    {
        return autoConvertOnRegistration;
    }

    public void setAutoConvertOnRegistration(Boolean autoConvertOnRegistration)
    {
        this.autoConvertOnRegistration = autoConvertOnRegistration;
    }

    public Boolean getMergeOnDuplicatePhone()
    {
        return mergeOnDuplicatePhone;
    }

    public void setMergeOnDuplicatePhone(Boolean mergeOnDuplicatePhone)
    {
        this.mergeOnDuplicatePhone = mergeOnDuplicatePhone;
    }

    public String getDefaultOrgId()
    {
        return defaultOrgId;
    }

    public void setDefaultOrgId(String defaultOrgId)
    {
        this.defaultOrgId = defaultOrgId;
    }

    public String getContactOwnerMode()
    {
        return contactOwnerMode;
    }

    public void setContactOwnerMode(String contactOwnerMode)
    {
        this.contactOwnerMode = contactOwnerMode;
    }

    public Long getContactOwnerUserId()
    {
        return contactOwnerUserId;
    }

    public void setContactOwnerUserId(Long contactOwnerUserId)
    {
        this.contactOwnerUserId = contactOwnerUserId;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public static LeadConvertConfigVo defaults()
    {
        LeadConvertConfigVo vo = new LeadConvertConfigVo();
        vo.setAutoConvertOnFirstVisit(true);
        vo.setAutoConvertOnCheckIn(true);
        vo.setAutoConvertOnRegistration(false);
        vo.setMergeOnDuplicatePhone(true);
        vo.setContactOwnerMode("keep_lead_owner");
        vo.setContactOwnerUserId(null);
        return vo;
    }
}
