package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报价产品对象 crm_quoteoption
 * 
 * @author luohd
 * @date 2024-02-10
 */
public class CrmQuoteoption extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private String optionId;

    /** 报价单ID */
    @Excel(name = "报价单ID")
    private String quoteId;

    /** 编号 */
    @Excel(name = "编号")
    private String quoteCode;

    /** 名称 */
    @Excel(name = "名称")
    private String quoteName;

    /** 类型 */
    @Excel(name = "类型")
    private String quoteType;

    /** 小计 */
    @Excel(name = "小计")
    private BigDecimal totalAmount;

    /** 产品ID */
    @Excel(name = "产品ID")
    private String productId;

    /** 产品名 */
    @Excel(name = "产品名")
    private String productName;

    /** 产品描述 */
    @Excel(name = "产品描述")
    private String productDesc;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 数量 */
    @Excel(name = "数量")
    private Long quantity;

    /** 折扣 */
    @Excel(name = "折扣")
    private BigDecimal discount;

    /** 销售id */
    @Excel(name = "销售id")
    private String ownerId;

    /** 销售 */
    @Excel(name = "销售")
    private String ownerName;

    public void setOptionId(String optionId) 
    {
        this.optionId = optionId;
    }

    public String getOptionId() 
    {
        return optionId;
    }
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
    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }
    public void setProductId(String productId) 
    {
        this.productId = productId;
    }

    public String getProductId() 
    {
        return productId;
    }
    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }
    public void setProductDesc(String productDesc) 
    {
        this.productDesc = productDesc;
    }

    public String getProductDesc() 
    {
        return productDesc;
    }
    public void setUnitPrice(BigDecimal unitPrice) 
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() 
    {
        return unitPrice;
    }
    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }
    public void setDiscount(BigDecimal discount) 
    {
        this.discount = discount;
    }

    public BigDecimal getDiscount() 
    {
        return discount;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("optionId", getOptionId())
            .append("quoteId", getQuoteId())
            .append("quoteCode", getQuoteCode())
            .append("quoteName", getQuoteName())
            .append("quoteType", getQuoteType())
            .append("totalAmount", getTotalAmount())
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("productDesc", getProductDesc())
            .append("unitPrice", getUnitPrice())
            .append("quantity", getQuantity())
            .append("discount", getDiscount())
            .append("ownerId", getOwnerId())
            .append("ownerName", getOwnerName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
