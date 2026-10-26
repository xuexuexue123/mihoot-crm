package com.ruoyi.crm.domain.dto.wecom;

/**
 * 企微统计冷启动初始化状态（供前端轮询）
 */
public class CrmWecomStatsStatusResponse
{
    private Boolean initialized;
    private Boolean initializing;
    private Long backfilledAt;

    public Boolean getInitialized()
    {
        return initialized;
    }

    public void setInitialized(Boolean initialized)
    {
        this.initialized = initialized;
    }

    public Boolean getInitializing()
    {
        return initializing;
    }

    public void setInitializing(Boolean initializing)
    {
        this.initializing = initializing;
    }

    public Long getBackfilledAt()
    {
        return backfilledAt;
    }

    public void setBackfilledAt(Long backfilledAt)
    {
        this.backfilledAt = backfilledAt;
    }
}
