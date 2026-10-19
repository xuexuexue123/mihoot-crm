package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 基金投资对象 crm_fundpaid
 * 
 * @author luohd
 * @date 2024-07-09
 */
public class CrmFundpaid extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String paidId;

    /** 公司编号 */
    @Excel(name = "公司编号")
    private String orgId;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String fullName;

    /** 项目编号 */
    @Excel(name = "项目编号")
    private String optyId;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String optyName;

    /** 权益编号 */
    @Excel(name = "权益编号")
    private String interestId;

    /** 基金编号 */
    @Excel(name = "基金编号")
    private String accountId;

    /** 基金名称 */
    @Excel(name = "基金名称")
    private String accountName;

    /** 投资类别 */
    @Excel(name = "投资类别")
    private String category;

    /** 投资轮次 */
    @Excel(name = "投资轮次")
    private String turn;

    /** 投资金额 */
    @Excel(name = "投资金额")
    private BigDecimal investAmount;

    /** 持有注册资本 */
    @Excel(name = "持有注册资本")
    private BigDecimal chiyouZiben;

    /** 持股比例 */
    @Excel(name = "持股比例")
    private BigDecimal chiyouPercentage;

    /** 持有价值 */
    @Excel(name = "持有价值")
    private BigDecimal chiyouValue;

    /** 签约日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签约日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date paidDate;

    /** 备注 */
    @Excel(name = "备注")
    private String comment;

    public void setPaidId(String paidId) 
    {
        this.paidId = paidId;
    }

    public String getPaidId() 
    {
        return paidId;
    }
    public void setOrgId(String orgId) 
    {
        this.orgId = orgId;
    }

    public String getOrgId() 
    {
        return orgId;
    }
    public void setFullName(String fullName) 
    {
        this.fullName = fullName;
    }

    public String getFullName() 
    {
        return fullName;
    }
    public void setOptyId(String optyId) 
    {
        this.optyId = optyId;
    }

    public String getOptyId() 
    {
        return optyId;
    }
    public void setOptyName(String optyName) 
    {
        this.optyName = optyName;
    }

    public String getOptyName() 
    {
        return optyName;
    }
    public void setInterestId(String interestId) 
    {
        this.interestId = interestId;
    }

    public String getInterestId() 
    {
        return interestId;
    }
    public void setAccountId(String accountId) 
    {
        this.accountId = accountId;
    }

    public String getAccountId() 
    {
        return accountId;
    }
    public void setAccountName(String accountName) 
    {
        this.accountName = accountName;
    }

    public String getAccountName() 
    {
        return accountName;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setTurn(String turn) 
    {
        this.turn = turn;
    }

    public String getTurn() 
    {
        return turn;
    }
    public void setInvestAmount(BigDecimal investAmount) 
    {
        this.investAmount = investAmount;
    }

    public BigDecimal getInvestAmount() 
    {
        return investAmount;
    }
    public void setChiyouZiben(BigDecimal chiyouZiben) 
    {
        this.chiyouZiben = chiyouZiben;
    }

    public BigDecimal getChiyouZiben() 
    {
        return chiyouZiben;
    }
    public void setChiyouPercentage(BigDecimal chiyouPercentage) 
    {
        this.chiyouPercentage = chiyouPercentage;
    }

    public BigDecimal getChiyouPercentage() 
    {
        return chiyouPercentage;
    }
    public void setChiyouValue(BigDecimal chiyouValue) 
    {
        this.chiyouValue = chiyouValue;
    }

    public BigDecimal getChiyouValue() 
    {
        return chiyouValue;
    }
    public void setPaidDate(Date paidDate) 
    {
        this.paidDate = paidDate;
    }

    public Date getPaidDate() 
    {
        return paidDate;
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
            .append("paidId", getPaidId())
            .append("orgId", getOrgId())
            .append("fullName", getFullName())
            .append("optyId", getOptyId())
            .append("optyName", getOptyName())
            .append("interestId", getInterestId())
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("category", getCategory())
            .append("turn", getTurn())
            .append("investAmount", getInvestAmount())
            .append("chiyouZiben", getChiyouZiben())
            .append("chiyouPercentage", getChiyouPercentage())
            .append("chiyouValue", getChiyouValue())
            .append("paidDate", getPaidDate())
            .append("comment", getComment())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
