package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 回访规则 enrollment 审计 VO
 */
public class FollowupRuleEnrollmentVo
{
    private String enrollmentId;
    private String ruleId;
    private String triggerType;
    private String subjectType;
    private String subjectId;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextRunAt;

    private String taskId;
    private String skipReason;
    private String cancelReason;
    private Integer retryCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getEnrollmentId()
    {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId)
    {
        this.enrollmentId = enrollmentId;
    }

    public String getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }

    public String getTriggerType()
    {
        return triggerType;
    }

    public void setTriggerType(String triggerType)
    {
        this.triggerType = triggerType;
    }

    public String getSubjectType()
    {
        return subjectType;
    }

    public void setSubjectType(String subjectType)
    {
        this.subjectType = subjectType;
    }

    public String getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(String subjectId)
    {
        this.subjectId = subjectId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getNextRunAt()
    {
        return nextRunAt;
    }

    public void setNextRunAt(Date nextRunAt)
    {
        this.nextRunAt = nextRunAt;
    }

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getSkipReason()
    {
        return skipReason;
    }

    public void setSkipReason(String skipReason)
    {
        this.skipReason = skipReason;
    }

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    public Integer getRetryCount()
    {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount)
    {
        this.retryCount = retryCount;
    }

    public Date getFiredTime()
    {
        return firedTime;
    }

    public void setFiredTime(Date firedTime)
    {
        this.firedTime = firedTime;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
