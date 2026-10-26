package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;

/**
 * 在售产品目录项（报价选品）
 */
public class ProductSellableVo
{
    private String productId;
    /** 一期等于 productId，预留 SKU */
    private String sellableItemId;
    private String productCode;
    private String productName;
    private String categoryId;
    private String categoryName;
    private String unit;
    private String spec;
    private BigDecimal standardPrice;
    private String shortDescription;
    private String coverImage;
    private boolean hasVariants = false;

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public String getSellableItemId()
    {
        return sellableItemId;
    }

    public void setSellableItemId(String sellableItemId)
    {
        this.sellableItemId = sellableItemId;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(String categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getSpec()
    {
        return spec;
    }

    public void setSpec(String spec)
    {
        this.spec = spec;
    }

    public BigDecimal getStandardPrice()
    {
        return standardPrice;
    }

    public void setStandardPrice(BigDecimal standardPrice)
    {
        this.standardPrice = standardPrice;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getCoverImage()
    {
        return coverImage;
    }

    public void setCoverImage(String coverImage)
    {
        this.coverImage = coverImage;
    }

    public boolean isHasVariants()
    {
        return hasVariants;
    }

    public void setHasVariants(boolean hasVariants)
    {
        this.hasVariants = hasVariants;
    }
}
