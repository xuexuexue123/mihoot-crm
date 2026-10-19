package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 企业微信统计快照 · 租户级初始化标记（单行 id=1）
 */
public class CrmWecomStatsMeta
{
    public static final String STATUS_NONE = "none";
    public static final String STATUS_RUNNING = "running";
    public static final String STATUS_DONE = "done";
    public static final String STATUS_FAILED = "failed";

    private Integer id;
    private String initStatus;
    private Date backfilledAt;
    private Integer backfillDays;
    private Date lockUntil;
    private String lastError;
    private Date createdAt;
    private Date updatedAt;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getInitStatus()
    {
        return initStatus;
    }

    public void setInitStatus(String initStatus)
    {
        this.initStatus = initStatus;
    }

    public Date getBackfilledAt()
    {
        return backfilledAt;
    }

    public void setBackfilledAt(Date backfilledAt)
    {
        this.backfilledAt = backfilledAt;
    }

    public Integer getBackfillDays()
    {
        return backfillDays;
    }

    public void setBackfillDays(Integer backfillDays)
    {
        this.backfillDays = backfillDays;
    }

    public Date getLockUntil()
    {
        return lockUntil;
    }

    public void setLockUntil(Date lockUntil)
    {
        this.lockUntil = lockUntil;
    }

    public String getLastError()
    {
        return lastError;
    }

    public void setLastError(String lastError)
    {
        this.lastError = lastError;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
