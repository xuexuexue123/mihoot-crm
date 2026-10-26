package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 企微客户群提醒物化队列
 */
public class CrmWecomGroupReminder
{
    private String id;
    private String tenantId;
    private String contactId;
    private String ruleId;
    private String sourceRef;
    private String chatId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueAt;
    private String status;
    private String payloadJson;
    private String renderedText;
    private Integer errcode;
    private String errmsg;
    private Integer retryCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sentAt;
    private String skippedReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date claimLeaseUntil;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 调试 pending 富化字段（不落库） */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String groupName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contactName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String touser;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }

    public String getSourceRef()
    {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef)
    {
        this.sourceRef = sourceRef;
    }

    public String getChatId()
    {
        return chatId;
    }

    public void setChatId(String chatId)
    {
        this.chatId = chatId;
    }

    public Date getDueAt()
    {
        return dueAt;
    }

    public void setDueAt(Date dueAt)
    {
        this.dueAt = dueAt;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPayloadJson()
    {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson)
    {
        this.payloadJson = payloadJson;
    }

    public String getRenderedText()
    {
        return renderedText;
    }

    public void setRenderedText(String renderedText)
    {
        this.renderedText = renderedText;
    }

    public Integer getErrcode()
    {
        return errcode;
    }

    public void setErrcode(Integer errcode)
    {
        this.errcode = errcode;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public void setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
    }

    public Integer getRetryCount()
    {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount)
    {
        this.retryCount = retryCount;
    }

    public Date getSentAt()
    {
        return sentAt;
    }

    public void setSentAt(Date sentAt)
    {
        this.sentAt = sentAt;
    }

    public String getSkippedReason()
    {
        return skippedReason;
    }

    public void setSkippedReason(String skippedReason)
    {
        this.skippedReason = skippedReason;
    }

    public Date getClaimLeaseUntil()
    {
        return claimLeaseUntil;
    }

    public void setClaimLeaseUntil(Date claimLeaseUntil)
    {
        this.claimLeaseUntil = claimLeaseUntil;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getTouser()
    {
        return touser;
    }

    public void setTouser(String touser)
    {
        this.touser = touser;
    }
}
