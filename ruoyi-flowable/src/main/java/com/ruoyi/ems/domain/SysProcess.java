package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 流程定义对象 sys_re_process
 * 
 * @author ruoyi
 * @date 2022-12-13
 */
public class SysProcess extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流程编码 */
    private String processId;

    /** 排序 */
    @Excel(name = "排序")
    private Long processSort;

    /** 标签 */
    @Excel(name = "标签")
    private String processLabel;

    /** 流程名称 */
    @Excel(name = "流程名称")
    private String processName;

    /** 流程类型 */
    @Excel(name = "流程类型")
    private String processType;

    /** case_type_id */
    @Excel(name = "case_type_id")
    private String caseTypeId;

    /** stage_id */
    @Excel(name = "stage_id")
    private String stageId;

    /** 跳过流程 */
    @Excel(name = "跳过流程")
    private String skipProcess;

    /** 跳过条件 */
    @Excel(name = "跳过条件")
    private String skipCondition;

    /** 流程部署的名称 */
    @Excel(name = "流程部署的名称")
    private String deploymentName;

    /** 流程部署的key */
    @Excel(name = "流程部署的key")
    private String deploymentKey;

    /** 部署的ID */
    @Excel(name = "部署的ID")
    private String deploymentId;

    /** 定义ID */
    @Excel(name = "定义ID")
    private String procDefId;

    /** 启动表单 */
    @Excel(name = "启动表单")
    private String formKey;

    /** 流程前操作 */
    @Excel(name = "流程前操作")
    private String beforeActivity;

    /** 流程前数据 */
    @Excel(name = "流程前数据")
    private String beforeDatatransform;

    /** 流程后操作 */
    @Excel(name = "流程后操作")
    private String afterActivity;

    /** 流程后数据 */
    @Excel(name = "流程后数据")
    private String afterDatatransform;

    /** ReactFlow JSON */
    @Excel(name = "ReactFlow JSON")
    private String reactflowJson;

    /** BPMN 2.0 XML */
    @Excel(name = "BPMN 2.0 XML")
    private String bpmnXml;

    /** 流程状态 */
    @Excel(name = "流程状态")
    private String status;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setProcessSort(Long processSort)
    {
        this.processSort = processSort;
    }

    public Long getProcessSort() 
    {
        return processSort;
    }
    public void setProcessLabel(String processLabel) 
    {
        this.processLabel = processLabel;
    }

    public String getProcessLabel() 
    {
        return processLabel;
    }
    public void setProcessName(String processName) 
    {
        this.processName = processName;
    }

    public String getProcessName() 
    {
        return processName;
    }
    public void setProcessType(String processType) 
    {
        this.processType = processType;
    }

    public String getProcessType() 
    {
        return processType;
    }
    public void setCaseTypeId(String caseTypeId) 
    {
        this.caseTypeId = caseTypeId;
    }

    public String getCaseTypeId() 
    {
        return caseTypeId;
    }
    public void setStageId(String stageId) 
    {
        this.stageId = stageId;
    }

    public String getStageId() 
    {
        return stageId;
    }
    public void setSkipProcess(String skipProcess) 
    {
        this.skipProcess = skipProcess;
    }

    public String getSkipProcess() 
    {
        return skipProcess;
    }
    public void setSkipCondition(String skipCondition) 
    {
        this.skipCondition = skipCondition;
    }

    public String getSkipCondition() 
    {
        return skipCondition;
    }
    public void setDeploymentName(String deploymentName) 
    {
        this.deploymentName = deploymentName;
    }

    public String getDeploymentName() 
    {
        return deploymentName;
    }
    public void setDeploymentKey(String deploymentKey) 
    {
        this.deploymentKey = deploymentKey;
    }

    public String getDeploymentKey() 
    {
        return deploymentKey;
    }
    public void setDeploymentId(String deploymentId) 
    {
        this.deploymentId = deploymentId;
    }

    public String getDeploymentId() 
    {
        return deploymentId;
    }
    public void setProcDefId(String procDefId) 
    {
        this.procDefId = procDefId;
    }

    public String getProcDefId() 
    {
        return procDefId;
    }
    public void setFormKey(String formKey) 
    {
        this.formKey = formKey;
    }

    public String getFormKey() 
    {
        return formKey;
    }

    public String getBeforeActivity() {
        return beforeActivity;
    }

    public void setBeforeActivity(String beforeActivity) {
        this.beforeActivity = beforeActivity;
    }

    public String getBeforeDatatransform() {
        return beforeDatatransform;
    }

    public void setBeforeDatatransform(String beforeDatatransform) {
        this.beforeDatatransform = beforeDatatransform;
    }

    public String getAfterActivity() {
        return afterActivity;
    }

    public void setAfterActivity(String afterActivity) {
        this.afterActivity = afterActivity;
    }

    public String getAfterDatatransform() {
        return afterDatatransform;
    }

    public void setAfterDatatransform(String afterDatatransform) {
        this.afterDatatransform = afterDatatransform;
    }
    public void setReactflowJson(String reactflowJson)
    {
        this.reactflowJson = reactflowJson;
    }

    public String getReactflowJson()
    {
        return reactflowJson;
    }
    public void setBpmnXml(String bpmnXml)
    {
        this.bpmnXml = bpmnXml;
    }

    public String getBpmnXml()
    {
        return bpmnXml;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("processId", getProcessId())
            .append("processSort", getProcessSort())
            .append("processLabel", getProcessLabel())
            .append("processName", getProcessName())
            .append("processType", getProcessType())
            .append("caseTypeId", getCaseTypeId())
            .append("stageId", getStageId())
            .append("skipProcess", getSkipProcess())
            .append("skipCondition", getSkipCondition())
            .append("deploymentName", getDeploymentName())
            .append("deploymentKey", getDeploymentKey())
            .append("deploymentId", getDeploymentId())
            .append("procDefId", getProcDefId())
            .append("formKey", getFormKey())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("beforeActivity", getBeforeActivity())
            .append("beforeDatatransform", getBeforeDatatransform())
            .append("afterActivity", getAfterActivity())
            .append("afterDataTransform", getAfterDatatransform())
            .toString();
    }
}
