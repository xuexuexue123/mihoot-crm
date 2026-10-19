package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 企业微信智能机器人连接状态（每租户库单行）
 */
public class CrmWecomAiBotConnection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenant;
    private String botId;
    /** disconnected | connecting | connected | disconnecting | reconnecting | error */
    private String status;
    private java.time.LocalDateTime lastMessageAt;
    private String errorMessage;
    private Integer errorCount;
    private java.time.LocalDateTime lastPingAt;
    private java.time.LocalDateTime updatedAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public String getBotId()
    {
        return botId;
    }

    public void setBotId(String botId)
    {
        this.botId = botId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public java.time.LocalDateTime getLastMessageAt()
    {
        return lastMessageAt;
    }

    public void setLastMessageAt(java.time.LocalDateTime lastMessageAt)
    {
        this.lastMessageAt = lastMessageAt;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCount()
    {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount)
    {
        this.errorCount = errorCount;
    }

    public java.time.LocalDateTime getLastPingAt()
    {
        return lastPingAt;
    }

    public void setLastPingAt(java.time.LocalDateTime lastPingAt)
    {
        this.lastPingAt = lastPingAt;
    }

    public java.time.LocalDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
