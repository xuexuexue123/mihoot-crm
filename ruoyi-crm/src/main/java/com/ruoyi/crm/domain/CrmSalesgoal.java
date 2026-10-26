package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 销售目标对象 crm_salesgoal
 * 
 * @author luohd
 * @date 2024-03-05
 */
public class CrmSalesgoal extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String goalId;

    /** 名称 */
    @Excel(name = "名称")
    private String goalName;

    /** 任务类型(销售额、线索、机会） */
    @Excel(name = "任务类型(销售额、线索、机会）")
    private String goalType;

    /**
     * KPI 编码（9 固定枚举）；查询以本字段为准。
     * 新数据 goalType=kpi、goalName=kpiCode 作兼容。
     */
    @Excel(name = "KPI编码")
    private String kpiCode;

    /** 周期编号 */
    @Excel(name = "周期编号")
    private String periodId;

    /** 周期名称 */
    @Excel(name = "周期名称")
    private String periodName;

    /** 类型(年、季度、月） */
    @Excel(name = "类型(年、季度、月）")
    private String periodType;

    /** 月度id */
    @Excel(name = "月度id")
    private String monthId;

    /** 月度名称 */
    @Excel(name = "月度名称")
    private String monthName;

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

    /** 销售目标 */
    @Excel(name = "销售目标")
    private BigDecimal goalValue;

    /** 销售名称 */
    @Excel(name = "销售名称")
    private String owner;

    /** 销售ID */
    @Excel(name = "销售ID")
    private String ownerId;

    /** 备注 */
    @Excel(name = "备注")
    private String comment;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    public void setGoalId(String goalId) 
    {
        this.goalId = goalId;
    }

    public String getGoalId() 
    {
        return goalId;
    }
    public void setGoalName(String goalName) 
    {
        this.goalName = goalName;
    }

    public String getGoalName() 
    {
        return goalName;
    }
    public void setGoalType(String goalType) 
    {
        this.goalType = goalType;
    }

    public String getGoalType() 
    {
        return goalType;
    }

    public void setKpiCode(String kpiCode)
    {
        this.kpiCode = kpiCode;
    }

    public String getKpiCode()
    {
        return kpiCode;
    }

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
    public void setMonthId(String monthId) 
    {
        this.monthId = monthId;
    }

    public String getMonthId() 
    {
        return monthId;
    }
    public void setMonthName(String monthName) 
    {
        this.monthName = monthName;
    }

    public String getMonthName() 
    {
        return monthName;
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
    public void setGoalValue(BigDecimal goalValue) 
    {
        this.goalValue = goalValue;
    }

    public BigDecimal getGoalValue() 
    {
        return goalValue;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setOwnerId(String ownerId) 
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId() 
    {
        return ownerId;
    }
    public void setComment(String comment) 
    {
        this.comment = comment;
    }

    public String getComment() 
    {
        return comment;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("goalId", getGoalId())
            .append("goalName", getGoalName())
            .append("goalType", getGoalType())
            .append("kpiCode", getKpiCode())
            .append("periodId", getPeriodId())
            .append("periodName", getPeriodName())
            .append("periodType", getPeriodType())
            .append("monthId", getMonthId())
            .append("monthName", getMonthName())
            .append("quarterId", getQuarterId())
            .append("quarterName", getQuarterName())
            .append("fiscalId", getFiscalId())
            .append("fiscalName", getFiscalName())
            .append("isActive", getIsActive())
            .append("goalValue", getGoalValue())
            .append("owner", getOwner())
            .append("ownerId", getOwnerId())
            .append("comment", getComment())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .toString();
    }
}
