package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 权益变更对象 crm_interest
 * 
 * @author luohd
 * @date 2024-07-09
 */
public class CrmInterest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String interestId;

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

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date changeDate;

    /** 轮次 */
    @Excel(name = "轮次")
    private String turn;

    /** 本轮融资金额 */
    @Excel(name = "本轮融资金额")
    private BigDecimal turnAmount;

    /** 投前估值 */
    @Excel(name = "投前估值")
    private BigDecimal touqianValue;

    /** 投后估值 */
    @Excel(name = "投后估值")
    private BigDecimal touhouValue;

    /** 注册资本 */
    @Excel(name = "注册资本")
    private BigDecimal zhuceZiben;

    /** 持有注册资本 */
    @Excel(name = "持有注册资本")
    private BigDecimal chiyouZiben;

    /** 持股比例 */
    @Excel(name = "持股比例")
    private BigDecimal chiyouPercentage;

    /** 持有价值 */
    @Excel(name = "持有价值")
    private BigDecimal chiyouValue;

    /** 投资金额 */
    @Excel(name = "投资金额")
    private BigDecimal investAmount;

    /** 备注 */
    @Excel(name = "备注")
    private String comment;

    /** 交易类型 */
    @Excel(name = "交易类型")
    private String category;

    /** 交易基金 */
    @Excel(name = "交易基金")
    private String accountName;

    /** 基金ID */
    @Excel(name = "基金ID")
    private String accountId;
    /** 基金投资信息 */
    private List<CrmFundpaid> crmFundpaidList;

    public void setInterestId(String interestId) 
    {
        this.interestId = interestId;
    }

    public String getInterestId() 
    {
        return interestId;
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
    public void setChangeDate(Date changeDate) 
    {
        this.changeDate = changeDate;
    }

    public Date getChangeDate() 
    {
        return changeDate;
    }
    public void setTurn(String turn) 
    {
        this.turn = turn;
    }

    public String getTurn() 
    {
        return turn;
    }
    public void setTurnAmount(BigDecimal turnAmount) 
    {
        this.turnAmount = turnAmount;
    }

    public BigDecimal getTurnAmount() 
    {
        return turnAmount;
    }
    public void setTouqianValue(BigDecimal touqianValue) 
    {
        this.touqianValue = touqianValue;
    }

    public BigDecimal getTouqianValue() 
    {
        return touqianValue;
    }
    public void setTouhouValue(BigDecimal touhouValue) 
    {
        this.touhouValue = touhouValue;
    }

    public BigDecimal getTouhouValue() 
    {
        return touhouValue;
    }
    public void setZhuceZiben(BigDecimal zhuceZiben) 
    {
        this.zhuceZiben = zhuceZiben;
    }

    public BigDecimal getZhuceZiben() 
    {
        return zhuceZiben;
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
    public void setInvestAmount(BigDecimal investAmount) 
    {
        this.investAmount = investAmount;
    }

    public BigDecimal getInvestAmount() 
    {
        return investAmount;
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
    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getAccountName()
    {
        return accountName;
    }
    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getAccountId()
    {
        return accountId;
    }
    public List<CrmFundpaid> getCrmFundpaidList()
    {
        return crmFundpaidList;
    }

    public void setCrmFundpaidList(List<CrmFundpaid> crmFundpaidList)
    {
        this.crmFundpaidList = crmFundpaidList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("interestId", getInterestId())
            .append("orgId", getOrgId())
            .append("fullName", getFullName())
            .append("optyId", getOptyId())
            .append("optyName", getOptyName())
            .append("changeDate", getChangeDate())
            .append("turn", getTurn())
            .append("turnAmount", getTurnAmount())
            .append("touqianValue", getTouqianValue())
            .append("touhouValue", getTouhouValue())
            .append("zhuceZiben", getZhuceZiben())
            .append("chiyouZiben", getChiyouZiben())
            .append("chiyouPercentage", getChiyouPercentage())
            .append("chiyouValue", getChiyouValue())
            .append("investAmount", getInvestAmount())
            .append("comment", getComment())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("crmFundpaidList", getCrmFundpaidList())
            .toString();
    }
}
