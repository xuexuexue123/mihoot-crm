package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 自动推送执行日志对象 smart_push_execution_log
 *
 * @author ruoyi
 * @date 2026-07-13
 */
public class SmartPushExecutionLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long strategyId;
    private String strategyName;
    private String targetUserSn;
    private String targetUserName;
    private String contactId;
    private String contactName;
    private Boolean success;
    private String errorMsg;
    private String eventType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getStrategyId()
    {
        return strategyId;
    }

    public void setStrategyId(Long strategyId)
    {
        this.strategyId = strategyId;
    }

    public String getStrategyName()
    {
        return strategyName;
    }

    public void setStrategyName(String strategyName)
    {
        this.strategyName = strategyName;
    }

    public String getTargetUserSn()
    {
        return targetUserSn;
    }

    public void setTargetUserSn(String targetUserSn)
    {
        this.targetUserSn = targetUserSn;
    }

    public String getTargetUserName()
    {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName)
    {
        this.targetUserName = targetUserName;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public Boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(Boolean success)
    {
        this.success = success;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public Date getExecutedAt()
    {
        return executedAt;
    }

    public void setExecutedAt(Date executedAt)
    {
        this.executedAt = executedAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }
}
