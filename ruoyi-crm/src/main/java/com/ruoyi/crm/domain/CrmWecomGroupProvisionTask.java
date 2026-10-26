package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 企微服务群建群任务
 */
public class CrmWecomGroupProvisionTask
{
    private String taskId;
    private String tenantId;
    private String contactId;
    private String assigneeUserid;
    private String assigneeCrmUserId;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date notifiedAt;
    private Integer remindCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiresAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
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

    public String getAssigneeUserid()
    {
        return assigneeUserid;
    }

    public void setAssigneeUserid(String assigneeUserid)
    {
        this.assigneeUserid = assigneeUserid;
    }

    public String getAssigneeCrmUserId()
    {
        return assigneeCrmUserId;
    }

    public void setAssigneeCrmUserId(String assigneeCrmUserId)
    {
        this.assigneeCrmUserId = assigneeCrmUserId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getNotifiedAt()
    {
        return notifiedAt;
    }

    public void setNotifiedAt(Date notifiedAt)
    {
        this.notifiedAt = notifiedAt;
    }

    public Integer getRemindCount()
    {
        return remindCount;
    }

    public void setRemindCount(Integer remindCount)
    {
        this.remindCount = remindCount;
    }

    public Date getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt)
    {
        this.expiresAt = expiresAt;
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
