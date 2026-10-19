package com.ruoyi.crm.domain.vo;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 活动培育步骤 API 视图
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventNurtureStepVo
{
    private Long id;
    private Integer stepOrder;
    private String actionType;
    private Integer delaySeconds;
    private Map<String, Object> actionJson;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public Integer getDelaySeconds() { return delaySeconds; }
    public void setDelaySeconds(Integer delaySeconds) { this.delaySeconds = delaySeconds; }
    public Map<String, Object> getActionJson() { return actionJson; }
    public void setActionJson(Map<String, Object> actionJson) { this.actionJson = actionJson; }
}
