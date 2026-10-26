package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 任务对象 crm_task
 * 
 * @author luohd
 * @date 2024-01-21
 */
public class CrmTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String taskId;

    /** 活动编号 */
//    @Excel(name = "活动编号")
    private String activityId;

    /** 名称 */
    @Excel(name = "名称")
    private String taskSubject;

    /** 类型 */
//    @Excel(name = "类型")
    private String taskType;

    /** 类别 */
//    @Excel(name = "类别")
    private String category;

    /** 问题组 */
//    @Excel(name = "任务组")
    private String groupName;

    /** 指派人 */
    @Excel(name = "负责人")
    private String assignedTo;

    /** 状态 */
    @Excel(name = "状态")
    private String statusDetail;

    /** 复核人 */
//    @Excel(name = "复核人")
    private String reviewers;

    /** 复核状态 */
//    @Excel(name = "复核状态")
    private String reviewStatus;

    /** 指派人ID */
//    @Excel(name = "指派人ID")
    private String assignedToid;

    /** 优先级 */
    @Excel(name = "优先级")
    private String priority;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;


    /** 时间周期结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    /** 过期日 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "过期日", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dueDate;

    /** 完成日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完成日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date completedDate;

    /** 结果 */
    @Excel(name = "结果")
    private String outcome;

    /** 目标类型 */
//    @Excel(name = "目标类型")
    private String relatedTotype;

    /** 关联目标 */
    @Excel(name = "关联目标")
    private String relatedTo;

    /** 目标ID */
//    @Excel(name = "目标ID")
    private String relatedToid;

    /** 备注 */
    @Excel(name = "备注")
    private String comments;

    private Long userId;

    private Long deptId;
    /** 阶段名 */
//    @Excel(name = "阶段名")
    private String stageName;

    /** 顺序 */
//    @Excel(name = "顺序")
    private Long taskOrder;
    /** 结论	 */
//    @Excel(name = "结论	")
    private String conclusion;

    /** 跟踪人 */
//    @Excel(name = "跟踪人")
    private String followers;

    /** 关联文件 */
//    @Excel(name = "关联文件")
    private String files;


    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setActivityId(String activityId) 
    {
        this.activityId = activityId;
    }

    public String getActivityId() 
    {
        return activityId;
    }
    public void setTaskSubject(String taskSubject) 
    {
        this.taskSubject = taskSubject;
    }

    public String getTaskSubject() 
    {
        return taskSubject;
    }
    public void setTaskType(String taskType) 
    {
        this.taskType = taskType;
    }

    public String getTaskType() 
    {
        return taskType;
    }
    public void setDueDate(Date dueDate) 
    {
        this.dueDate = dueDate;
    }

    public Date getDueDate() 
    {
        return dueDate;
    }
    public void setAssignedTo(String assignedTo) 
    {
        this.assignedTo = assignedTo;
    }

    public String getAssignedTo() 
    {
        return assignedTo;
    }
    public void setAssignedToid(String assignedToid) 
    {
        this.assignedToid = assignedToid;
    }

    public String getAssignedToid() 
    {
        return assignedToid;
    }
    public void setPriority(String priority) 
    {
        this.priority = priority;
    }

    public String getPriority() 
    {
        return priority;
    }
    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStatusDetail(String statusDetail)
    {
        this.statusDetail = statusDetail;
    }

    public String getStatusDetail() 
    {
        return statusDetail;
    }
    public void setCompletedDate(Date completedDate) 
    {
        this.completedDate = completedDate;
    }

    public Date getCompletedDate() 
    {
        return completedDate;
    }
    public void setOutcome(String outcome) 
    {
        this.outcome = outcome;
    }

    public String getOutcome() 
    {
        return outcome;
    }
    public void setRelatedTotype(String relatedTotype) 
    {
        this.relatedTotype = relatedTotype;
    }

    public String getRelatedTotype() 
    {
        return relatedTotype;
    }
    public void setRelatedTo(String relatedTo) 
    {
        this.relatedTo = relatedTo;
    }

    public String getRelatedTo() 
    {
        return relatedTo;
    }
    public void setRelatedToid(String relatedToid) 
    {
        this.relatedToid = relatedToid;
    }

    public String getRelatedToid() 
    {
        return relatedToid;
    }
    public void setComments(String comments) 
    {
        this.comments = comments;
    }

    public String getComments() 
    {
        return comments;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
    public void setStageName(String stageName)
    {
        this.stageName = stageName;
    }

    public String getStageName()
    {
        return stageName;
    }
    public void setTaskOrder(Long taskOrder)
    {
        this.taskOrder = taskOrder;
    }

    public Long getTaskOrder()
    {
        return taskOrder;
    }
    public void setConclusion(String conclusion)
    {
        this.conclusion = conclusion;
    }

    public String getConclusion()
    {
        return conclusion;
    }

    public void setReviewers(String reviewers)
    {
        this.reviewers = reviewers;
    }

    public String getReviewers()
    {
        return reviewers;
    }
    public void setReviewStatus(String reviewStatus)
    {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewStatus()
    {
        return reviewStatus;
    }
    public void setFollowers(String followers)
    {
        this.followers = followers;
    }

    public String getFollowers()
    {
        return followers;
    }
    public void setFiles(String files)
    {
        this.files = files;
    }

    public String getFiles()
    {
        return files;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getGroupName()
    {
        return groupName;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("activityId", getActivityId())
            .append("taskSubject", getTaskSubject())
            .append("taskType", getTaskType())
            .append("dueDate", getDueDate())
            .append("assignedTo", getAssignedTo())
            .append("assignedToid", getAssignedToid())
            .append("priority", getPriority())
            .append("startDate", getStartDate())
            .append("statusDetail", getStatusDetail())
            .append("completedDate", getCompletedDate())
            .append("outcome", getOutcome())
            .append("relatedTotype", getRelatedTotype())
            .append("relatedTo", getRelatedTo())
            .append("relatedToid", getRelatedToid())
            .append("comments", getComments())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("stageName", getStageName())
            .append("taskOrder", getTaskOrder())
            .append("conclusion", getConclusion())
            .append("reviewers", getReviewers())
            .append("reviewStatus", getReviewStatus())
            .append("followers", getFollowers())
            .append("files", getFiles())
            .append("category", getCategory())
            .toString();
    }
}
