package com.ruoyi.crm.domain.enums;

import com.ruoyi.common.exception.ServiceException;

/**
 * KPI 目标管理固定枚举（与 docs/salesgoal-kpi-backend-plan.md §3.2 对齐）。
 */
public enum SalesKpiCode
{
    CONTRACT_AMOUNT("contract_amount", "合同金额"),
    WON_CONTRACT_COUNT("won_contract_count", "赢单合同数"),
    WIN_RATE("win_rate", "赢单率"),
    FOLLOWED_CUSTOMERS("followed_customers", "跟进客户数"),
    MET_CUSTOMERS("met_customers", "见面客户数"),
    ASSIGNED_CUSTOMERS("assigned_customers", "分配客户数"),
    FOLLOWUP_COUNT("followup_count", "跟进次数"),
    OPTY_COUNT("opty_count", "商机数"),
    OPTY_AMOUNT("opty_amount", "商机金额");

    private final String code;
    private final String label;

    SalesKpiCode(String code, String label)
    {
        this.code = code;
        this.label = label;
    }

    public String getCode()
    {
        return code;
    }

    public String getLabel()
    {
        return label;
    }

    /**
     * @throws ServiceException 非法 kpiCode
     */
    public static SalesKpiCode fromCode(String code)
    {
        if (code == null || code.isEmpty())
        {
            throw new ServiceException("kpiCode 不能为空");
        }
        for (SalesKpiCode value : values())
        {
            if (value.code.equals(code))
            {
                return value;
            }
        }
        throw new ServiceException("非法 kpiCode: " + code + "，允许值见枚举 SalesKpiCode");
    }

    public static boolean isValid(String code)
    {
        if (code == null || code.isEmpty())
        {
            return false;
        }
        for (SalesKpiCode value : values())
        {
            if (value.code.equals(code))
            {
                return true;
            }
        }
        return false;
    }
}
