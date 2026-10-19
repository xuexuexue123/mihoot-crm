package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 收款对象 crm_paymentterm
 * 
 * @author luohd
 * @date 2024-02-16
 */
public class CrmPaymentterm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String termId;

    /** 名称 */
    @Excel(name = "名称")
    private String termName;

    /** 收款日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "收款日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date termDate;

    /** 收款期数 */
    @Excel(name = "收款期数")
    private Long termIndex;

    /** 实收金额 */
    @Excel(name = "实收金额")
    private BigDecimal paidAmount;

    /** 收款金额 */
    @Excel(name = "收款金额")
    private BigDecimal termAmount;

    /** 合同ID */
    @Excel(name = "合同ID")
    private String contractId;

    /** 合同名称 */
    @Excel(name = "合同名称")
    private String contractName;

    /** 客户编号 */
    @Excel(name = "客户编号")
    private String accountId;

    /** 客户名 */
    @Excel(name = "客户名")
    private String accountName;

    /** 销售id */
    @Excel(name = "销售id")
    private String ownerId;

    /** 销售 */
    @Excel(name = "销售")
    private String ownerName;

    /** 是否已付款 */
    @Excel(name = "是否已付款")
    private String isPaid;

    /** 收款人 */
    @Excel(name = "收款人")
    private String operatorName;

    /** 收款日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "收款日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date collectDate;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    private Long userId;

    private Long deptId;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 退款原因 */
    private String refundReason;

    /** 退款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date refundedAt;

    /** 退款操作人 */
    private String refundedBy;

    public void setTermId(String termId) 
    {
        this.termId = termId;
    }

    public String getTermId() 
    {
        return termId;
    }
    public void setTermName(String termName) 
    {
        this.termName = termName;
    }

    public String getTermName() 
    {
        return termName;
    }
    public void setTermDate(Date termDate) 
    {
        this.termDate = termDate;
    }

    public Date getTermDate() 
    {
        return termDate;
    }
    public void setTermIndex(Long termIndex) 
    {
        this.termIndex = termIndex;
    }

    public Long getTermIndex() 
    {
        return termIndex;
    }
    public void setPaidAmount(BigDecimal paidAmount) 
    {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getPaidAmount() 
    {
        return paidAmount;
    }
    public void setTermAmount(BigDecimal termAmount) 
    {
        this.termAmount = termAmount;
    }

    public BigDecimal getTermAmount() 
    {
        return termAmount;
    }
    public void setContractId(String contractId) 
    {
        this.contractId = contractId;
    }

    public String getContractId() 
    {
        return contractId;
    }
    public void setContractName(String contractName) 
    {
        this.contractName = contractName;
    }

    public String getContractName() 
    {
        return contractName;
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
    public void setOwnerId(String ownerId) 
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId() 
    {
        return ownerId;
    }
    public void setOwnerName(String ownerName) 
    {
        this.ownerName = ownerName;
    }

    public String getOwnerName() 
    {
        return ownerName;
    }
    public void setIsPaid(String isPaid) 
    {
        this.isPaid = isPaid;
    }

    public String getIsPaid() 
    {
        return isPaid;
    }
    public void setOperatorName(String operatorName) 
    {
        this.operatorName = operatorName;
    }

    public String getOperatorName() 
    {
        return operatorName;
    }
    public void setCollectDate(Date collectDate) 
    {
        this.collectDate = collectDate;
    }

    public Date getCollectDate() 
    {
        return collectDate;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
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

    public BigDecimal getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason()
    {
        return refundReason;
    }

    public void setRefundReason(String refundReason)
    {
        this.refundReason = refundReason;
    }

    public Date getRefundedAt()
    {
        return refundedAt;
    }

    public void setRefundedAt(Date refundedAt)
    {
        this.refundedAt = refundedAt;
    }

    public String getRefundedBy()
    {
        return refundedBy;
    }

    public void setRefundedBy(String refundedBy)
    {
        this.refundedBy = refundedBy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("termId", getTermId())
            .append("termName", getTermName())
            .append("termDate", getTermDate())
            .append("termIndex", getTermIndex())
            .append("paidAmount", getPaidAmount())
            .append("termAmount", getTermAmount())
            .append("contractId", getContractId())
            .append("contractName", getContractName())
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("ownerId", getOwnerId())
            .append("ownerName", getOwnerName())
            .append("isPaid", getIsPaid())
            .append("operatorName", getOperatorName())
            .append("collectDate", getCollectDate())
            .append("description", getDescription())
            .append("refundAmount", getRefundAmount())
            .append("refundReason", getRefundReason())
            .append("refundedAt", getRefundedAt())
            .append("refundedBy", getRefundedBy())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
