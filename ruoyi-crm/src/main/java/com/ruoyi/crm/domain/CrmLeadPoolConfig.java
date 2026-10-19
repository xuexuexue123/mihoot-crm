package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 租户公海配置 crm_lead_pool_config（一库一行；租户靠数据源隔离）
 */
public class CrmLeadPoolConfig extends BaseEntity
{
    private String configId;
    private Integer claimDailyLimit = 30;
    private Integer protectDays = 7;
    private Boolean recycleEnabled = true;
    private String defaultPoolOrgScope = "dept";
    /** 认定潜客 EMS Survey Template ID；未配置为 null */
    private String qualifyTemplateId;
    /** 未跟进转入公海小时数；权威口径，protectDays 仅为兼容 */
    private Integer idleToPoolHours;
    /** 私海最大持有量（status=Assigned） */
    private Integer maxHoldCount;

    public String getConfigId()
    {
        return configId;
    }

    public void setConfigId(String configId)
    {
        this.configId = configId;
    }

    public Integer getClaimDailyLimit()
    {
        return claimDailyLimit;
    }

    public void setClaimDailyLimit(Integer claimDailyLimit)
    {
        this.claimDailyLimit = claimDailyLimit;
    }

    public Integer getProtectDays()
    {
        return protectDays;
    }

    public void setProtectDays(Integer protectDays)
    {
        this.protectDays = protectDays;
    }

    public Boolean getRecycleEnabled()
    {
        return recycleEnabled;
    }

    public void setRecycleEnabled(Boolean recycleEnabled)
    {
        this.recycleEnabled = recycleEnabled;
    }

    public String getDefaultPoolOrgScope()
    {
        return defaultPoolOrgScope;
    }

    public void setDefaultPoolOrgScope(String defaultPoolOrgScope)
    {
        this.defaultPoolOrgScope = defaultPoolOrgScope;
    }

    public String getQualifyTemplateId()
    {
        return qualifyTemplateId;
    }

    public void setQualifyTemplateId(String qualifyTemplateId)
    {
        this.qualifyTemplateId = qualifyTemplateId;
    }

    public Integer getIdleToPoolHours()
    {
        return idleToPoolHours;
    }

    public void setIdleToPoolHours(Integer idleToPoolHours)
    {
        this.idleToPoolHours = idleToPoolHours;
    }

    public Integer getMaxHoldCount()
    {
        return maxHoldCount;
    }

    public void setMaxHoldCount(Integer maxHoldCount)
    {
        this.maxHoldCount = maxHoldCount;
    }
}
