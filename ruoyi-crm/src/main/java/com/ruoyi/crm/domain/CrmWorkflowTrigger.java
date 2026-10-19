package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * worflowtrigger对象 crm_workflow_trigger
 * 
 * @author luohd
 * @date 2025-06-05
 */
public class CrmWorkflowTrigger extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** Flowable 流程 ID */
    @Excel(name = "Flowable 流程 ID")
    private String processDefinitionKey;

    /** 事件类型（SURVEY_SUBMITTED， EMAIL_RECEIVED，WEBHOOK_CALLED，MANUAL_TRIGGERED） */
    @Excel(name = "事件类型", readConverterExp = "S=URVEY_SUBMITTED，,E=MAIL_RECEIVED，WEBHOOK_CALLED，MANUAL_TRIGGERED")
    private String eventType;

    /** 触发源的唯一 ID，例如 surveyId、webhookId */
    @Excel(name = "触发源的唯一 ID，例如 surveyId、webhookId")
    private String triggerSourceId;

    /** 执行表达式 */
    @Excel(name = "执行表达式")
    private String conditionExpression;

    /** 变量 */
    @Excel(name = "变量")
    private String variableMapping;

    /** 租户ID */
    @Excel(name = "租户ID")
    private String tenantId;

    /** 启用 */
    @Excel(name = "启用")
    private Integer isActive;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setProcessDefinitionKey(String processDefinitionKey) 
    {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getProcessDefinitionKey() 
    {
        return processDefinitionKey;
    }
    public void setEventType(String eventType) 
    {
        this.eventType = eventType;
    }

    public String getEventType() 
    {
        return eventType;
    }
    public void setTriggerSourceId(String triggerSourceId) 
    {
        this.triggerSourceId = triggerSourceId;
    }

    public String getTriggerSourceId() 
    {
        return triggerSourceId;
    }
    public void setConditionExpression(String conditionExpression) 
    {
        this.conditionExpression = conditionExpression;
    }

    public String getConditionExpression() 
    {
        return conditionExpression;
    }
    public void setVariableMapping(String variableMapping) 
    {
        this.variableMapping = variableMapping;
    }

    public String getVariableMapping() 
    {
        return variableMapping;
    }
    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }

    public String getTenantId() 
    {
        return tenantId;
    }
    public void setIsActive(Integer isActive) 
    {
        this.isActive = isActive;
    }

    public Integer getIsActive() 
    {
        return isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("processDefinitionKey", getProcessDefinitionKey())
            .append("eventType", getEventType())
            .append("triggerSourceId", getTriggerSourceId())
            .append("conditionExpression", getConditionExpression())
            .append("variableMapping", getVariableMapping())
            .append("tenantId", getTenantId())
            .append("isActive", getIsActive())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
