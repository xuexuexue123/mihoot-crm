package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 投资人对象 crm_fundinvestor
 * 
 * @author luohd
 * @date 2024-06-18
 */
public class CrmFundinvestor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String investorId;

    /** 类型 */
    @Excel(name = "类型")
    private String relateType;

    /** 关联人ID */
    @Excel(name = "关联人ID")
    private String relateId;

    /** 联系人名 */
    @Excel(name = "联系人名")
    private String relateName;

    /** 基金ID */
    @Excel(name = "基金ID")
    private String accountId;

    /** 基金名称 */
    @Excel(name = "基金名称")
    private String accountName;

    /** 认缴额 */
    @Excel(name = "认缴额")
    private BigDecimal claimAmount;

    /** 认缴比例 */
    @Excel(name = "认缴比例")
    private BigDecimal claimPercentage;

    /** 认缴日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "认缴日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date claimDate;

    /** 实缴额 */
    @Excel(name = "实缴额")
    private BigDecimal investAmount;

    /** 实缴管理费 */
    @Excel(name = "实缴管理费")
    private BigDecimal paidMgrfee;

    /** 费用分摊 */
    @Excel(name = "费用分摊")
    private BigDecimal sharedFee;

    /** 累计分配金额 */
    @Excel(name = "累计分配金额")
    private BigDecimal accumulatedAmount;

    /** 备注文件 */
    @Excel(name = "备注文件")
    private String commentFile;

    public void setInvestorId(String investorId) 
    {
        this.investorId = investorId;
    }

    public String getInvestorId() 
    {
        return investorId;
    }
    public void setRelateType(String relateType) 
    {
        this.relateType = relateType;
    }

    public String getRelateType() 
    {
        return relateType;
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
    public void setClaimAmount(BigDecimal claimAmount) 
    {
        this.claimAmount = claimAmount;
    }

    public BigDecimal getClaimAmount() 
    {
        return claimAmount;
    }
    public void setClaimPercentage(BigDecimal claimPercentage) 
    {
        this.claimPercentage = claimPercentage;
    }

    public BigDecimal getClaimPercentage() 
    {
        return claimPercentage;
    }
    public void setClaimDate(Date claimDate) 
    {
        this.claimDate = claimDate;
    }

    public Date getClaimDate() 
    {
        return claimDate;
    }
    public void setInvestAmount(BigDecimal investAmount) 
    {
        this.investAmount = investAmount;
    }

    public BigDecimal getInvestAmount() 
    {
        return investAmount;
    }
    public void setPaidMgrfee(BigDecimal paidMgrfee) 
    {
        this.paidMgrfee = paidMgrfee;
    }

    public BigDecimal getPaidMgrfee() 
    {
        return paidMgrfee;
    }
    public void setSharedFee(BigDecimal sharedFee) 
    {
        this.sharedFee = sharedFee;
    }

    public BigDecimal getSharedFee() 
    {
        return sharedFee;
    }
    public void setAccumulatedAmount(BigDecimal accumulatedAmount) 
    {
        this.accumulatedAmount = accumulatedAmount;
    }

    public BigDecimal getAccumulatedAmount() 
    {
        return accumulatedAmount;
    }
    public void setCommentFile(String commentFile) 
    {
        this.commentFile = commentFile;
    }

    public String getCommentFile() 
    {
        return commentFile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("investorId", getInvestorId())
            .append("relateType", getRelateType())
            .append("relateId", getRelateId())
            .append("relateName", getRelateName())
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("claimAmount", getClaimAmount())
            .append("claimPercentage", getClaimPercentage())
            .append("claimDate", getClaimDate())
            .append("investAmount", getInvestAmount())
            .append("paidMgrfee", getPaidMgrfee())
            .append("sharedFee", getSharedFee())
            .append("accumulatedAmount", getAccumulatedAmount())
            .append("commentFile", getCommentFile())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
