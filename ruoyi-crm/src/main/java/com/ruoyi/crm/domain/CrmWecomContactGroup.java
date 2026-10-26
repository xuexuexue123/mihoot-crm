package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 企微一客一群绑定
 */
public class CrmWecomContactGroup
{
    private String id;
    private String tenantId;
    private String contactId;
    private String chatId;
    private String ownerUserid;
    private String groupName;
    private String status;
    private String provisionTaskId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date botActivatedAt;
    private String lastError;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

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

    public String getChatId()
    {
        return chatId;
    }

    public void setChatId(String chatId)
    {
        this.chatId = chatId;
    }

    public String getOwnerUserid()
    {
        return ownerUserid;
    }

    public void setOwnerUserid(String ownerUserid)
    {
        this.ownerUserid = ownerUserid;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getProvisionTaskId()
    {
        return provisionTaskId;
    }

    public void setProvisionTaskId(String provisionTaskId)
    {
        this.provisionTaskId = provisionTaskId;
    }

    public Date getBotActivatedAt()
    {
        return botActivatedAt;
    }

    public void setBotActivatedAt(Date botActivatedAt)
    {
        this.botActivatedAt = botActivatedAt;
    }

    public String getLastError()
    {
        return lastError;
    }

    public void setLastError(String lastError)
    {
        this.lastError = lastError;
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
}
