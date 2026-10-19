package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 复诊策略 crm_recall_policy
 */
public class CrmRecallPolicy extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String policyId;
    private String tenantId;
    private String name;
    private String description;
    private String enabled;
    private String isSystem;
    private Integer priority;
    private String matchJson;
    private Integer intervalValue;
    private String intervalUnit;
    private String delFlag;

    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEnabled() { return enabled; }
    public void setEnabled(String enabled) { this.enabled = enabled; }
    public String getIsSystem() { return isSystem; }
    public void setIsSystem(String isSystem) { this.isSystem = isSystem; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getMatchJson() { return matchJson; }
    public void setMatchJson(String matchJson) { this.matchJson = matchJson; }
    public Integer getIntervalValue() { return intervalValue; }
    public void setIntervalValue(Integer intervalValue) { this.intervalValue = intervalValue; }
    public String getIntervalUnit() { return intervalUnit; }
    public void setIntervalUnit(String intervalUnit) { this.intervalUnit = intervalUnit; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
