package com.ruoyi.crm.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 家庭账户 LTV 聚合（账户维度）。
 */
public class CrmFamilyLtvVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 合同侧金额合计（sign_amount / total_amount / amount 优先非空） */
    private BigDecimal contractTotal;

    /** 赢单商机金额合计（stage 在字典 crm_opty_stage_closed_won 中） */
    private BigDecimal wonTreatmentTotal;

    /** 合同已收合计（可选） */
    private BigDecimal receivedTotal;

    /** 币种展示 */
    private String currency;

    /** 该账户下联系人数量 */
    private Integer memberCount;

    public BigDecimal getContractTotal()
    {
        return contractTotal;
    }

    public void setContractTotal(BigDecimal contractTotal)
    {
        this.contractTotal = contractTotal;
    }

    public BigDecimal getWonTreatmentTotal()
    {
        return wonTreatmentTotal;
    }

    public void setWonTreatmentTotal(BigDecimal wonTreatmentTotal)
    {
        this.wonTreatmentTotal = wonTreatmentTotal;
    }

    public BigDecimal getReceivedTotal()
    {
        return receivedTotal;
    }

    public void setReceivedTotal(BigDecimal receivedTotal)
    {
        this.receivedTotal = receivedTotal;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public Integer getMemberCount()
    {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount)
    {
        this.memberCount = memberCount;
    }
}
