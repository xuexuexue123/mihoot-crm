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
 * 合同对象 crm_contract
 * 
 * @author luohd
 * @date 2024-02-16
 */
public class CrmContract extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private String contractId;

    /** 名称 */
    @Excel(name = "名称")
    private String contractName;

    /** 类型 */
    @Excel(name = "类型")
    private String contractType;

    /** 编码 */
    @Excel(name = "编码")
    private String contractCode;

    /** 合同状态 */
    @Excel(name = "合同状态")
    private String contractStatus;

    /** 我方身份 */
    @Excel(name = "我方身份")
    private String contractIdentity;

    /** 资金流向 */
    @Excel(name = "资金流向")
    private String contractFundsdirec;

    /** 机会编号 */
    @Excel(name = "机会编号")
    private String optyId;

    /** 机会名 */
    @Excel(name = "机会名")
    private String optyName;

    /** 客户编号 */
    @Excel(name = "客户编号")
    private String accountId;

    /** 客户名 */
    @Excel(name = "客户名")
    private String accountName;

    /** 联系人ID */
    @Excel(name = "联系人ID")
    private String contactId;

    /** 联系人名 */
    @Excel(name = "联系人名")
    private String contactName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 税率 */
    @Excel(name = "税率")
    private BigDecimal taxRatio;

    /** 税额 */
    @Excel(name = "税额")
    private BigDecimal taxAmount;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 签约金额 */
    @Excel(name = "签约金额")
    private BigDecimal signAmount;

    /** 变更金额 */
    @Excel(name = "变更金额")
    private BigDecimal changedAmount;

    /** 执行金额 */
    @Excel(name = "执行金额")
    private BigDecimal executionAmount;

    /** 已收款 */
    @Excel(name = "已收款")
    private BigDecimal receivedAmount;

    /** 应收金额 */
    @Excel(name = "应收金额")
    private BigDecimal receivable;

    /** 未收金额 */
    @Excel(name = "未收金额")
    private BigDecimal pendingReceivable;

    /** 已开票金额 */
    @Excel(name = "已开票金额")
    private BigDecimal invoicedAmount;

    /** 合同成本 */
    @Excel(name = "合同成本")
    private BigDecimal cost;

    /** 关联费用 */
    @Excel(name = "关联费用")
    private BigDecimal expense;

    /** 合同利润 */
    @Excel(name = "合同利润")
    private BigDecimal margin;

    /** 支付期数 */
    @Excel(name = "支付期数")
    private Long paymentTerms;

    /** 是否变更 */
    @Excel(name = "是否变更")
    private String isChange;

    /** 是否作废 */
    @Excel(name = "是否作废")
    private String isDestroy;

    /** 承办人id */
    @Excel(name = "承办人id")
    private String operatorId;

    /** 承办人 */
    @Excel(name = "承办人")
    private String operatorName;

    /** 销售id */
    @Excel(name = "销售id")
    private String ownerId;

    /** 销售 */
    @Excel(name = "销售")
    private String ownerName;

    /** 合同状态 */
    @Excel(name = "合同状态")
    private String status;

    /** 合同年限 */
    @Excel(name = "合同年限")
    private Long duration;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveTime;

    /** 失效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date losteffectiveTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 签订时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签订时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date signTime;


    private Long userId;

    private Long deptId;

    /** 合同文件 */
    @Excel(name = "合同文件")
    private String commentFile;

    /** 收款信息 */
    private List<CrmPaymentterm> crmPaymenttermList;

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
    public void setContractType(String contractType) 
    {
        this.contractType = contractType;
    }

    public String getContractType() 
    {
        return contractType;
    }
    public void setContractCode(String contractCode) 
    {
        this.contractCode = contractCode;
    }

    public String getContractCode() 
    {
        return contractCode;
    }
    public void setContractStatus(String contractStatus) 
    {
        this.contractStatus = contractStatus;
    }

    public String getContractStatus() 
    {
        return contractStatus;
    }
    public void setContractIdentity(String contractIdentity) 
    {
        this.contractIdentity = contractIdentity;
    }

    public String getContractIdentity() 
    {
        return contractIdentity;
    }
    public void setContractFundsdirec(String contractFundsdirec) 
    {
        this.contractFundsdirec = contractFundsdirec;
    }

    public String getContractFundsdirec() 
    {
        return contractFundsdirec;
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
    public void setContactId(String contactId) 
    {
        this.contactId = contactId;
    }

    public String getContactId() 
    {
        return contactId;
    }
    public void setContactName(String contactName) 
    {
        this.contactName = contactName;
    }

    public String getContactName() 
    {
        return contactName;
    }
    public void setContactPhone(String contactPhone) 
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone() 
    {
        return contactPhone;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setTaxRatio(BigDecimal taxRatio) 
    {
        this.taxRatio = taxRatio;
    }

    public BigDecimal getTaxRatio() 
    {
        return taxRatio;
    }
    public void setTaxAmount(BigDecimal taxAmount) 
    {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxAmount() 
    {
        return taxAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }
    public void setSignAmount(BigDecimal signAmount) 
    {
        this.signAmount = signAmount;
    }

    public BigDecimal getSignAmount() 
    {
        return signAmount;
    }
    public void setChangedAmount(BigDecimal changedAmount) 
    {
        this.changedAmount = changedAmount;
    }

    public BigDecimal getChangedAmount() 
    {
        return changedAmount;
    }
    public void setExecutionAmount(BigDecimal executionAmount) 
    {
        this.executionAmount = executionAmount;
    }

    public BigDecimal getExecutionAmount() 
    {
        return executionAmount;
    }
    public void setReceivedAmount(BigDecimal receivedAmount) 
    {
        this.receivedAmount = receivedAmount;
    }

    public BigDecimal getReceivedAmount() 
    {
        return receivedAmount;
    }
    public void setReceivable(BigDecimal receivable) 
    {
        this.receivable = receivable;
    }

    public BigDecimal getReceivable() 
    {
        return receivable;
    }
    public void setPendingReceivable(BigDecimal pendingReceivable) 
    {
        this.pendingReceivable = pendingReceivable;
    }

    public BigDecimal getPendingReceivable() 
    {
        return pendingReceivable;
    }
    public void setInvoicedAmount(BigDecimal invoicedAmount) 
    {
        this.invoicedAmount = invoicedAmount;
    }

    public BigDecimal getInvoicedAmount() 
    {
        return invoicedAmount;
    }
    public void setCost(BigDecimal cost) 
    {
        this.cost = cost;
    }

    public BigDecimal getCost() 
    {
        return cost;
    }
    public void setExpense(BigDecimal expense) 
    {
        this.expense = expense;
    }

    public BigDecimal getExpense() 
    {
        return expense;
    }
    public void setMargin(BigDecimal margin) 
    {
        this.margin = margin;
    }

    public BigDecimal getMargin() 
    {
        return margin;
    }
    public void setPaymentTerms(Long paymentTerms) 
    {
        this.paymentTerms = paymentTerms;
    }

    public Long getPaymentTerms() 
    {
        return paymentTerms;
    }
    public void setIsChange(String isChange) 
    {
        this.isChange = isChange;
    }

    public String getIsChange() 
    {
        return isChange;
    }
    public void setIsDestroy(String isDestroy) 
    {
        this.isDestroy = isDestroy;
    }

    public String getIsDestroy() 
    {
        return isDestroy;
    }
    public void setOperatorId(String operatorId) 
    {
        this.operatorId = operatorId;
    }

    public String getOperatorId() 
    {
        return operatorId;
    }
    public void setOperatorName(String operatorName) 
    {
        this.operatorName = operatorName;
    }

    public String getOperatorName() 
    {
        return operatorName;
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
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setDuration(Long duration) 
    {
        this.duration = duration;
    }

    public Long getDuration() 
    {
        return duration;
    }
    public void setEffectiveTime(Date effectiveTime) 
    {
        this.effectiveTime = effectiveTime;
    }

    public Date getEffectiveTime() 
    {
        return effectiveTime;
    }
    public void setLosteffectiveTime(Date losteffectiveTime) 
    {
        this.losteffectiveTime = losteffectiveTime;
    }

    public Date getLosteffectiveTime() 
    {
        return losteffectiveTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setSignTime(Date signTime) 
    {
        this.signTime = signTime;
    }

    public Date getSignTime() 
    {
        return signTime;
    }

    public List<CrmPaymentterm> getCrmPaymenttermList()
    {
        return crmPaymenttermList;
    }

    public void setCrmPaymenttermList(List<CrmPaymentterm> crmPaymenttermList)
    {
        this.crmPaymenttermList = crmPaymenttermList;
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
            .append("contractId", getContractId())
            .append("contractName", getContractName())
            .append("contractType", getContractType())
            .append("contractCode", getContractCode())
            .append("contractStatus", getContractStatus())
            .append("contractIdentity", getContractIdentity())
            .append("contractFundsdirec", getContractFundsdirec())
            .append("optyId", getOptyId())
            .append("optyName", getOptyName())
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("contactId", getContactId())
            .append("contactName", getContactName())
            .append("contactPhone", getContactPhone())
            .append("amount", getAmount())
            .append("taxRatio", getTaxRatio())
            .append("taxAmount", getTaxAmount())
            .append("totalAmount", getTotalAmount())
            .append("signAmount", getSignAmount())
            .append("changedAmount", getChangedAmount())
            .append("executionAmount", getExecutionAmount())
            .append("receivedAmount", getReceivedAmount())
            .append("receivable", getReceivable())
            .append("pendingReceivable", getPendingReceivable())
            .append("invoicedAmount", getInvoicedAmount())
            .append("cost", getCost())
            .append("expense", getExpense())
            .append("margin", getMargin())
            .append("paymentTerms", getPaymentTerms())
            .append("isChange", getIsChange())
            .append("isDestroy", getIsDestroy())
            .append("operatorId", getOperatorId())
            .append("operatorName", getOperatorName())
            .append("ownerId", getOwnerId())
            .append("ownerName", getOwnerName())
            .append("status", getStatus())
            .append("duration", getDuration())
            .append("effectiveTime", getEffectiveTime())
            .append("losteffectiveTime", getLosteffectiveTime())
            .append("endTime", getEndTime())
            .append("signTime", getSignTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("crmPaymenttermList", getCrmPaymenttermList())
            .toString();
    }
}
