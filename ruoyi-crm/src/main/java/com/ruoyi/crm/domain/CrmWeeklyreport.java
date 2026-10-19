package com.ruoyi.crm.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 周报对象 crm_weeklyreport
 * 
 * @author luohd
 * @date 2024-04-16
 */
public class CrmWeeklyreport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报告ID */
    private String reportId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String nickName;

    /** 是否审核 */
    @Excel(name = "是否审核")
    private Long reviewed;

    /** 是否审批 */
    @Excel(name = "是否审批")
    private Long approved;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approver;

    /** 审批人ID */
    @Excel(name = "审批人ID")
    private String approverId;

    /** 报告内容 */
    @Excel(name = "报告内容")
    private String reportContent;

    /** 报告批复 */
    @Excel(name = "报告批复")
    private String managerComment;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 周报详细信息 */
    private List<CrmReportdetail> crmReportdetailList;

    public void setReportId(String reportId) 
    {
        this.reportId = reportId;
    }

    public String getReportId() 
    {
        return reportId;
    }
    public void setNickName(String nickName) 
    {
        this.nickName = nickName;
    }

    public String getNickName() 
    {
        return nickName;
    }
    public void setReviewed(Long reviewed) 
    {
        this.reviewed = reviewed;
    }

    public Long getReviewed() 
    {
        return reviewed;
    }
    public void setApproved(Long approved) 
    {
        this.approved = approved;
    }

    public Long getApproved() 
    {
        return approved;
    }
    public void setApprover(String approver) 
    {
        this.approver = approver;
    }

    public String getApprover() 
    {
        return approver;
    }
    public void setApproverId(String approverId) 
    {
        this.approverId = approverId;
    }

    public String getApproverId() 
    {
        return approverId;
    }
    public void setReportContent(String reportContent) 
    {
        this.reportContent = reportContent;
    }

    public String getReportContent() 
    {
        return reportContent;
    }
    public void setManagerComment(String managerComment) 
    {
        this.managerComment = managerComment;
    }

    public String getManagerComment() 
    {
        return managerComment;
    }
    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    public List<CrmReportdetail> getCrmReportdetailList()
    {
        return crmReportdetailList;
    }

    public void setCrmReportdetailList(List<CrmReportdetail> crmReportdetailList)
    {
        this.crmReportdetailList = crmReportdetailList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("reportId", getReportId())
            .append("nickName", getNickName())
            .append("reviewed", getReviewed())
            .append("approved", getApproved())
            .append("approver", getApprover())
            .append("approverId", getApproverId())
            .append("reportContent", getReportContent())
            .append("managerComment", getManagerComment())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("crmReportdetailList", getCrmReportdetailList())
            .toString();
    }
}
