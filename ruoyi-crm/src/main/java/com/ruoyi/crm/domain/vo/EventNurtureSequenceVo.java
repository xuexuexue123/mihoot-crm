package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 活动培育序列 API 视图
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventNurtureSequenceVo
{
    private Long id;
    private String eventId;
    private String name;
    private Boolean enabled;
    private String triggerType;
    private Map<String, Object> triggerConfig;
    private Boolean exitOnContact;
    private Integer stepCount;
    private Integer activeEnrollmentCount;
    private Boolean legacyMpAutomationConflict;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
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
    public Integer getStepCount() { return stepCount; }
    public void setStepCount(Integer stepCount) { this.stepCount = stepCount; }
    public Integer getActiveEnrollmentCount() { return activeEnrollmentCount; }
    public void setActiveEnrollmentCount(Integer activeEnrollmentCount) { this.activeEnrollmentCount = activeEnrollmentCount; }
    public Boolean getLegacyMpAutomationConflict() { return legacyMpAutomationConflict; }
    public void setLegacyMpAutomationConflict(Boolean legacyMpAutomationConflict) { this.legacyMpAutomationConflict = legacyMpAutomationConflict; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
