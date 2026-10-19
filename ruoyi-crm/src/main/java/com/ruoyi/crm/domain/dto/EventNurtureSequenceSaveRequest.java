package com.ruoyi.crm.domain.dto;

import java.util.Map;

/**
 * 保存培育序列配置
 */
public class EventNurtureSequenceSaveRequest
{
    private String name;
    private Boolean enabled;
    private String triggerType;
    private Map<String, Object> triggerConfig;
    private Boolean exitOnContact;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
    public Map<String, Object> getTriggerConfig() { return triggerConfig; }
    public void setTriggerConfig(Map<String, Object> triggerConfig) { this.triggerConfig = triggerConfig; }
    public Boolean getExitOnContact() { return exitOnContact; }
    public void setExitOnContact(Boolean exitOnContact) { this.exitOnContact = exitOnContact; }
}
