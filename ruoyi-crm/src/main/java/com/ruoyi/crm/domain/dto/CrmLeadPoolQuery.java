package com.ruoyi.crm.domain.dto;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 线索池列表查询
 */
public class CrmLeadPoolQuery extends BaseEntity
{
    private String poolScope = "pending";
    private String leadType = "business";
    private String orgId;
    private Long deptId;
    private String leadName;
    private String mobilePhone;
    private String leadSource;
    private String stage;
    private Long currentUserId;

    public String getPoolScope()
    {
        return poolScope;
    }

    public void setPoolScope(String poolScope)
    {
        this.poolScope = poolScope;
    }

    /** 前端 query 参数 scope 别名 */
    public void setScope(String scope)
    {
        this.poolScope = scope;
    }

    public String getScope()
    {
        return poolScope;
    }

    public String getLeadType()
    {
        return leadType;
    }

    public void setLeadType(String leadType)
    {
        this.leadType = leadType;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getLeadName()
    {
        return leadName;
    }

    public void setLeadName(String leadName)
    {
        this.leadName = leadName;
    }

    public String getMobilePhone()
    {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public String getLeadSource()
    {
        return leadSource;
    }

    public void setLeadSource(String leadSource)
    {
        this.leadSource = leadSource;
    }

    public String getStage()
    {
        return stage;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public Long getCurrentUserId()
    {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId)
    {
        this.currentUserId = currentUserId;
    }
}
