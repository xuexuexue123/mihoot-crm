package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目变更对象 sys_re_pmchange
 * 
 * @author luohd
 * @date 2023-05-19
 */
public class SysRePmchange extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String changeId;

    /** 项目Id */
    @Excel(name = "项目Id")
    private String projectId;

    /** 相关任务 */
    @Excel(name = "相关任务")
    private String taskId;

    /** 变更要点 */
    @Excel(name = "变更要点")
    private String changePoint;

    /** 变更理由 */
    @Excel(name = "变更理由")
    private String reason;

    /** 申请人 */
    @Excel(name = "申请人")
    private String applicant;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approver;

    /** 审批结论 */
    @Excel(name = "审批结论")
    private String approvedResult;

    /** 审批结论 */
    @Excel(name = "审批结论")
    private String approvedComment;

    /** 成本影响 */
    @Excel(name = "成本影响")
    private String impactToCost;

    /** 进度影响 */
    @Excel(name = "进度影响")
    private String impactToProgress;

    /** 资源影响 */
    @Excel(name = "资源影响")
    private String impactToResource;

    /** 不变的影响 */
    @Excel(name = "不变的影响")
    private String whatifNoChange;

    /** 变更程度 */
    @Excel(name = "变更程度")
    private String impactLevel;

    public void setChangeId(String changeId) 
    {
        this.changeId = changeId;
    }

    public String getChangeId() 
    {
        return changeId;
    }
    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setChangePoint(String changePoint) 
    {
        this.changePoint = changePoint;
    }

    public String getChangePoint() 
    {
        return changePoint;
    }
    public void setReason(String reason) 
    {
        this.reason = reason;
    }

    public String getReason() 
    {
        return reason;
    }
    public void setApplicant(String applicant) 
    {
        this.applicant = applicant;
    }

    public String getApplicant() 
    {
        return applicant;
    }
    public void setApprover(String approver) 
    {
        this.approver = approver;
    }

    public String getApprover() 
    {
        return approver;
    }
    public void setApprovedResult(String approvedResult) 
    {
        this.approvedResult = approvedResult;
    }

    public String getApprovedResult() 
    {
        return approvedResult;
    }
    public void setApprovedComment(String approvedComment) 
    {
        this.approvedComment = approvedComment;
    }

    public String getApprovedComment() 
    {
        return approvedComment;
    }
    public void setImpactToCost(String impactToCost) 
    {
        this.impactToCost = impactToCost;
    }

    public String getImpactToCost() 
    {
        return impactToCost;
    }
    public void setImpactToProgress(String impactToProgress) 
    {
        this.impactToProgress = impactToProgress;
    }

    public String getImpactToProgress() 
    {
        return impactToProgress;
    }
    public void setImpactToResource(String impactToResource) 
    {
        this.impactToResource = impactToResource;
    }

    public String getImpactToResource() 
    {
        return impactToResource;
    }
    public void setWhatifNoChange(String whatifNoChange) 
    {
        this.whatifNoChange = whatifNoChange;
    }

    public String getWhatifNoChange() 
    {
        return whatifNoChange;
    }
    public void setImpactLevel(String impactLevel) 
    {
        this.impactLevel = impactLevel;
    }

    public String getImpactLevel() 
    {
        return impactLevel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("changeId", getChangeId())
            .append("projectId", getProjectId())
            .append("taskId", getTaskId())
            .append("changePoint", getChangePoint())
            .append("reason", getReason())
            .append("applicant", getApplicant())
            .append("approver", getApprover())
            .append("approvedResult", getApprovedResult())
            .append("approvedComment", getApprovedComment())
            .append("impactToCost", getImpactToCost())
            .append("impactToProgress", getImpactToProgress())
            .append("impactToResource", getImpactToResource())
            .append("whatifNoChange", getWhatifNoChange())
            .append("impactLevel", getImpactLevel())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
