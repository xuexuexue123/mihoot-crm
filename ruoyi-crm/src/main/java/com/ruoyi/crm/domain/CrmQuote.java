package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报价单对象 crm_quote
 * 
 * @author luohd
 * @date 2024-02-14
 */
public class CrmQuote extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private String quoteId;

    /** 编号 */
    @Excel(name = "编号")
    private String quoteCode;

    /** 机会ID */
    @Excel(name = "机会ID")
    private String optyId;

    /** 机会编号 */
    @Excel(name = "机会编号")
    private String optyName;

    /** 名称 */
    @Excel(name = "名称")
    private String quoteName;

    /** 类型 */
    @Excel(name = "类型")
    private String quoteType;

    /** 未含税金额 */
    @Excel(name = "未含税金额")
    private BigDecimal amount;

    /** 折扣 */
    @Excel(name = "折扣")
    private BigDecimal discount;

    /** 税率 */
    @Excel(name = "税率")
    private BigDecimal taxRatio;

    /** 税额 */
    @Excel(name = "税额")
    private BigDecimal taxAmount;

    /** 折扣额 */
    @Excel(name = "折扣额")
    private BigDecimal discountAmount;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 客户ID */
    @Excel(name = "客户ID")
    private String accountId;

    /** 客户名 */
    @Excel(name = "客户名")
    private String accountName;

    /** 联系人ID */
    @Excel(name = "联系人ID")
    private String contactId;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 销售id */
    @Excel(name = "销售id")
    private String ownerId;

    /** 销售 */
    @Excel(name = "销售")
    private String ownerName;

    /** 合同月 */
    @Excel(name = "合同月")
    private Long paymentTerms;

    /** 报价日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报价日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date quoteDate;

    /** 失效日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expiredDate;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 审批状态 */
    @Excel(name = "审批状态")
    private String approvalStatus;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 报价产品信息 */
    private List<CrmQuoteoption> crmQuoteoptionList;

    public void setQuoteId(String quoteId) 
    {
        this.quoteId = quoteId;
    }

    public String getQuoteId() 
    {
        return quoteId;
    }
    public void setQuoteCode(String quoteCode) 
    {
        this.quoteCode = quoteCode;
    }

    public String getQuoteCode() 
    {
        return quoteCode;
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
    public void setQuoteName(String quoteName) 
    {
        this.quoteName = quoteName;
    }

    public String getQuoteName() 
    {
        return quoteName;
    }
    public void setQuoteType(String quoteType) 
    {
        this.quoteType = quoteType;
    }

    public String getQuoteType() 
    {
        return quoteType;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setDiscount(BigDecimal discount) 
    {
        this.discount = discount;
    }

    public BigDecimal getDiscount() 
    {
        return discount;
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
    public void setDiscountAmount(BigDecimal discountAmount) 
    {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountAmount() 
    {
        return discountAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
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
    public void setPaymentTerms(Long paymentTerms) 
    {
        this.paymentTerms = paymentTerms;
    }

    public Long getPaymentTerms() 
    {
        return paymentTerms;
    }
    public void setQuoteDate(Date quoteDate) 
    {
        this.quoteDate = quoteDate;
    }

    public Date getQuoteDate() 
    {
        return quoteDate;
    }
    public void setExpiredDate(Date expiredDate) 
    {
        this.expiredDate = expiredDate;
    }

    public Date getExpiredDate() 
    {
        return expiredDate;
    }
    public void setEffectiveTime(Date effectiveTime) 
    {
        this.effectiveTime = effectiveTime;
    }

    public Date getEffectiveTime() 
    {
        return effectiveTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setApprovalStatus(String approvalStatus) 
    {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovalStatus() 
    {
        return approvalStatus;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getRemark()
    {
        return remark;
    }

    @JsonProperty("quoteOptionList")
    public List<CrmQuoteoption> getCrmQuoteoptionList()
    {
        return crmQuoteoptionList;
    }

    @JsonProperty("quoteOptionList")
    public void setCrmQuoteoptionList(List<CrmQuoteoption> crmQuoteoptionList)
    {
        this.crmQuoteoptionList = crmQuoteoptionList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("quoteId", getQuoteId())
            .append("quoteCode", getQuoteCode())
            .append("optyId", getOptyId())
            .append("optyName", getOptyName())
            .append("quoteName", getQuoteName())
            .append("quoteType", getQuoteType())
            .append("amount", getAmount())
            .append("discount", getDiscount())
            .append("taxRatio", getTaxRatio())
            .append("taxAmount", getTaxAmount())
            .append("discountAmount", getDiscountAmount())
            .append("totalAmount", getTotalAmount())
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("contactId", getContactId())
            .append("contactName", getContactName())
            .append("contactPhone", getContactPhone())
            .append("ownerId", getOwnerId())
            .append("ownerName", getOwnerName())
            .append("paymentTerms", getPaymentTerms())
            .append("quoteDate", getQuoteDate())
            .append("expiredDate", getExpiredDate())
            .append("effectiveTime", getEffectiveTime())
            .append("endTime", getEndTime())
            .append("approvalStatus", getApprovalStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("crmQuoteoptionList", getCrmQuoteoptionList())
            .toString();
    }
}
