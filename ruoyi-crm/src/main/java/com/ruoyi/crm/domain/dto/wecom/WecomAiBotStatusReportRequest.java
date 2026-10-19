package com.ruoyi.crm.domain.dto.wecom;

/**
 * bot-server 上报连接状态
 */
public class WecomAiBotStatusReportRequest
{
    private String tenant;
    private String status;
    private java.time.LocalDateTime lastMessageAt;
    private String errorMessage;
    private int errorCount;

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
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

    public int getErrorCount()
    {
        return errorCount;
    }

    public void setErrorCount(int errorCount)
    {
        this.errorCount = errorCount;
    }
}
