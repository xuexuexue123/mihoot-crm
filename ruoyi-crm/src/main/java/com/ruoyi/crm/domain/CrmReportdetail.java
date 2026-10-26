package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 周报详细对象 crm_reportdetail
 *
 * @author luohd
 * @date 2024-04-16
 */
public class CrmReportdetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报告ID */
    private String detailId;

    /** 报告ID */
    @Excel(name = "报告ID")
    private String reportId;

    /** 星期几 */
    @Excel(name = "星期几")
    private String dayOrder;

    /** COLDCALL */
    @Excel(name = "COLDCALL")
    private Long coldCall;

    /** FOLLOWUP */
    @Excel(name = "FOLLOWUP")
    private Long followupCall;

    /** EMAILSENT */
    @Excel(name = "EMAILSENT")
    private Long emailSent;

    /** MEETINGS */
    @Excel(name = "MEETINGS")
    private Long meetingArranged;

    /** VISITS */
    @Excel(name = "VISITS")
    private Long visitsCompleted;

    /** 业务线索 */
    @Excel(name = "业务线索")
    private Long leadGenerated;

    /** 已收单 */
    @Excel(name = "已收单")
    private Long dealClosed;

    /** 销售额 */
    @Excel(name = "销售额")
    private BigDecimal salesRevenue;

    /** 周目标 */
    @Excel(name = "周目标")
    private BigDecimal targetAmount;

    /** 差值 */
    @Excel(name = "差值")
    private BigDecimal variance;

    /** 说明 */
    @Excel(name = "说明")
    private String notes;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 顺序 */
    @Excel(name = "顺序")
    private Long reportOrder;

    public void setDetailId(String detailId)
    {
        this.detailId = detailId;
    }

    public String getDetailId()
    {
        return detailId;
    }
    public void setReportId(String reportId)
    {
        this.reportId = reportId;
    }

    public String getReportId()
    {
        return reportId;
    }
    public void setDayOrder(String dayOrder)
    {
        this.dayOrder = dayOrder;
    }

    public String getDayOrder()
    {
        return dayOrder;
    }
    public void setColdCall(Long coldCall)
    {
        this.coldCall = coldCall;
    }

    public Long getColdCall()
    {
        return coldCall;
    }
    public void setFollowupCall(Long followupCall)
    {
        this.followupCall = followupCall;
    }

    public Long getFollowupCall()
    {
        return followupCall;
    }
    public void setEmailSent(Long emailSent)
    {
        this.emailSent = emailSent;
    }

    public Long getEmailSent()
    {
        return emailSent;
    }
    public void setMeetingArranged(Long meetingArranged)
    {
        this.meetingArranged = meetingArranged;
    }

    public Long getMeetingArranged()
    {
        return meetingArranged;
    }
    public void setVisitsCompleted(Long visitsCompleted)
    {
        this.visitsCompleted = visitsCompleted;
    }

    public Long getVisitsCompleted()
    {
        return visitsCompleted;
    }
    public void setLeadGenerated(Long leadGenerated)
    {
        this.leadGenerated = leadGenerated;
    }

    public Long getLeadGenerated()
    {
        return leadGenerated;
    }
    public void setDealClosed(Long dealClosed)
    {
        this.dealClosed = dealClosed;
    }

    public Long getDealClosed()
    {
        return dealClosed;
    }
    public void setSalesRevenue(BigDecimal salesRevenue)
    {
        this.salesRevenue = salesRevenue;
    }

    public BigDecimal getSalesRevenue()
    {
        return salesRevenue;
    }
    public void setTargetAmount(BigDecimal targetAmount)
    {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getTargetAmount()
    {
        return targetAmount;
    }
    public void setVariance(BigDecimal variance)
    {
        this.variance = variance;
    }

    public BigDecimal getVariance()
    {
        return variance;
    }
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getNotes()
    {
        return notes;
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
    public void setReportOrder(Long reportOrder)
    {
        this.reportOrder = reportOrder;
    }

    public Long getReportOrder()
    {
        return reportOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("detailId", getDetailId())
                .append("reportId", getReportId())
                .append("dayOrder", getDayOrder())
                .append("coldCall", getColdCall())
                .append("followupCall", getFollowupCall())
                .append("emailSent", getEmailSent())
                .append("meetingArranged", getMeetingArranged())
                .append("visitsCompleted", getVisitsCompleted())
                .append("leadGenerated", getLeadGenerated())
                .append("dealClosed", getDealClosed())
                .append("salesRevenue", getSalesRevenue())
                .append("targetAmount", getTargetAmount())
                .append("variance", getVariance())
                .append("notes", getNotes())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("userId", getUserId())
                .append("deptId", getDeptId())
                .append("reportOrder", getReportOrder())
                .toString();
    }
}
