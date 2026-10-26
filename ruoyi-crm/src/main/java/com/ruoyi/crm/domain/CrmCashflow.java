package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资金流水对象 crm_cashflow
 * 
 * @author luohd
 * @date 2024-06-19
 */
public class CrmCashflow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String cashflowId;

    /** 类型 */
    @Excel(name = "类型")
    private String cashflowType;

    /** 交易主体ID */
    @Excel(name = "交易主体ID")
    private String relateId;

    /** 交易主体 */
    @Excel(name = "交易主体")
    private String relateName;

    /** 对方银行账号 */
    @Excel(name = "对方银行账号")
    private String relateBackaccount;

    /** 交易日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交易日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date transactionDate;

    /** 交易金额 */
    @Excel(name = "交易金额")
    private BigDecimal amount;

    /** 备注 */
    @Excel(name = "备注")
    private String comment;

    /** 类别 */
    @Excel(name = "类别")
    private String category;

    /** 基金ID */
//    @Excel(name = "基金ID")
    private String accountId;

    public void setCashflowId(String cashflowId) 
    {
        this.cashflowId = cashflowId;
    }

    public String getCashflowId() 
    {
        return cashflowId;
    }
    public void setCashflowType(String cashflowType) 
    {
        this.cashflowType = cashflowType;
    }

    public String getCashflowType() 
    {
        return cashflowType;
    }
    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setRelateName(String relateName) 
    {
        this.relateName = relateName;
    }

    public String getRelateName() 
    {
        return relateName;
    }
    public void setRelateBackaccount(String relateBackaccount) 
    {
        this.relateBackaccount = relateBackaccount;
    }

    public String getRelateBackaccount() 
    {
        return relateBackaccount;
    }
    public void setTransactionDate(Date transactionDate) 
    {
        this.transactionDate = transactionDate;
    }

    public Date getTransactionDate() 
    {
        return transactionDate;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setComment(String comment) 
    {
        this.comment = comment;
    }

    public String getComment() 
    {
        return comment;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getAccountId()
    {
        return accountId;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("cashflowId", getCashflowId())
            .append("cashflowType", getCashflowType())
            .append("relateId", getRelateId())
            .append("relateName", getRelateName())
            .append("relateBackaccount", getRelateBackaccount())
            .append("transactionDate", getTransactionDate())
            .append("amount", getAmount())
            .append("comment", getComment())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("category", getCategory())
            .toString();
    }
}
