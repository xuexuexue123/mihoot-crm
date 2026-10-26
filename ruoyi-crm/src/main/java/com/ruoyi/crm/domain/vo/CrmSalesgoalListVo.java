package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

import com.ruoyi.crm.domain.CrmSalesgoal;

/**
 * 销售目标列表 VO：目标行 + 只读实际值 / 完成率（可不落库）。
 */
public class CrmSalesgoalListVo extends CrmSalesgoal
{
    private static final long serialVersionUID = 1L;

    /** 本月实际值；无法计算时 null */
    private BigDecimal actualValue;

    /** actual/goal；goalValue&lt;=0 或实际不可用时 null */
    private BigDecimal completionRate;

    public BigDecimal getActualValue()
    {
        return actualValue;
    }

    public void setActualValue(BigDecimal actualValue)
    {
        this.actualValue = actualValue;
    }

    public BigDecimal getCompletionRate()
    {
        return completionRate;
    }

    public void setCompletionRate(BigDecimal completionRate)
    {
        this.completionRate = completionRate;
    }

    public static CrmSalesgoalListVo from(CrmSalesgoal src)
    {
        CrmSalesgoalListVo vo = new CrmSalesgoalListVo();
        if (src == null)
        {
            return vo;
        }
        vo.setGoalId(src.getGoalId());
        vo.setGoalName(src.getGoalName());
        vo.setGoalType(src.getGoalType());
        vo.setKpiCode(src.getKpiCode());
        vo.setPeriodId(src.getPeriodId());
        vo.setPeriodName(src.getPeriodName());
        vo.setPeriodType(src.getPeriodType());
        vo.setMonthId(src.getMonthId());
        vo.setMonthName(src.getMonthName());
        vo.setQuarterId(src.getQuarterId());
        vo.setQuarterName(src.getQuarterName());
        vo.setFiscalId(src.getFiscalId());
        vo.setFiscalName(src.getFiscalName());
        vo.setIsActive(src.getIsActive());
        vo.setGoalValue(src.getGoalValue());
        vo.setOwner(src.getOwner());
        vo.setOwnerId(src.getOwnerId());
        vo.setComment(src.getComment());
        vo.setCreateBy(src.getCreateBy());
        vo.setCreateTime(src.getCreateTime());
        vo.setUpdateBy(src.getUpdateBy());
        vo.setUpdateTime(src.getUpdateTime());
        vo.setUserId(src.getUserId());
        vo.setDeptId(src.getDeptId());
        return vo;
    }
}
