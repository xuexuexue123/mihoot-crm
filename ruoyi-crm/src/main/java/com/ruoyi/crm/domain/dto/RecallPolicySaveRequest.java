package com.ruoyi.crm.domain.dto;

/**
 * 复诊策略保存请求
 */
public class RecallPolicySaveRequest
{
    private String policyId;
    private String name;
    private String description;
    private String enabled;
    private Integer priority;
    private String matchJson;
    private Integer intervalValue;
    private String intervalUnit;

    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEnabled() { return enabled; }
    public void setEnabled(String enabled) { this.enabled = enabled; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getMatchJson() { return matchJson; }
    public void setMatchJson(String matchJson) { this.matchJson = matchJson; }
    public Integer getIntervalValue() { return intervalValue; }
    public void setIntervalValue(Integer intervalValue) { this.intervalValue = intervalValue; }
    public String getIntervalUnit() { return intervalUnit; }
    public void setIntervalUnit(String intervalUnit) { this.intervalUnit = intervalUnit; }
}
