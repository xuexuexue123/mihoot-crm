package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 销售周期对象 crm_timeperiod
 * 
 * @author luohd
 * @date 2024-02-27
 */
public class CrmTimeperiod extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String periodId;

    /** 名称 */
    @Excel(name = "名称")
    private String periodName;

    /** 类型(年、季度、月） */
    @Excel(name = "类型(年、季度、月）")
    private String periodType;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 季度id */
    @Excel(name = "季度id")
    private String quarterId;

    /** 季度名称 */
    @Excel(name = "季度名称")
    private String quarterName;

    /** 年度id */
    @Excel(name = "年度id")
    private String fiscalId;

    /** 年度名称 */
    @Excel(name = "年度名称")
    private String fiscalName;

    /** 是否当前 */
    @Excel(name = "是否当前")
    private Long isActive;

    /** 备注 */
    @Excel(name = "备注")
    private String comment;

    public void setPeriodId(String periodId) 
    {
        this.periodId = periodId;
    }

    public String getPeriodId() 
    {
        return periodId;
    }
    public void setPeriodName(String periodName) 
    {
        this.periodName = periodName;
    }

    public String getPeriodName() 
    {
        return periodName;
    }
    public void setPeriodType(String periodType) 
    {
        this.periodType = periodType;
    }

    public String getPeriodType() 
    {
        return periodType;
    }
    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }
    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }

    public Date getEndDate() 
    {
        return endDate;
    }
    public void setQuarterId(String quarterId) 
    {
        this.quarterId = quarterId;
    }

    public String getQuarterId() 
    {
        return quarterId;
    }
    public void setQuarterName(String quarterName) 
    {
        this.quarterName = quarterName;
    }

    public String getQuarterName() 
    {
        return quarterName;
    }
    public void setFiscalId(String fiscalId) 
    {
        this.fiscalId = fiscalId;
    }

    public String getFiscalId() 
    {
        return fiscalId;
    }
    public void setFiscalName(String fiscalName) 
    {
        this.fiscalName = fiscalName;
    }

    public String getFiscalName() 
    {
        return fiscalName;
    }
    public void setIsActive(Long isActive) 
    {
        this.isActive = isActive;
    }

    public Long getIsActive() 
    {
        return isActive;
    }
    public void setComment(String comment) 
    {
        this.comment = comment;
    }

    public String getComment() 
    {
        return comment;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("periodId", getPeriodId())
            .append("periodName", getPeriodName())
            .append("periodType", getPeriodType())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("quarterId", getQuarterId())
            .append("quarterName", getQuarterName())
            .append("fiscalId", getFiscalId())
            .append("fiscalName", getFiscalName())
            .append("isActive", getIsActive())
            .append("comment", getComment())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
