package com.ruoyi.crm.domain.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 企微商品图册同步预览（不调用企微，仅展示主档将提交的内容）
 */
public class ProductWecomPreviewVo
{
    private String productId;
    private String productName;
    private String productCode;
    private String description;
    /** 主档标准价（元） */
    private BigDecimal standardPriceYuan;
    /** 企微价格（分） */
    private Integer priceFen;
    private String coverImage;
    /** create | update */
    private String mode;
    private String wecomProductId;
    private String syncStatus;
    private boolean canSync;
    private List<String> blockers = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public BigDecimal getStandardPriceYuan()
    {
        return standardPriceYuan;
    }

    public void setStandardPriceYuan(BigDecimal standardPriceYuan)
    {
        this.standardPriceYuan = standardPriceYuan;
    }

    public Integer getPriceFen()
    {
        return priceFen;
    }

    public void setPriceFen(Integer priceFen)
    {
        this.priceFen = priceFen;
    }

    public String getCoverImage()
    {
        return coverImage;
    }

    public void setCoverImage(String coverImage)
    {
        this.coverImage = coverImage;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getWecomProductId()
    {
        return wecomProductId;
    }

    public void setWecomProductId(String wecomProductId)
    {
        this.wecomProductId = wecomProductId;
    }

    public String getSyncStatus()
    {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus)
    {
        this.syncStatus = syncStatus;
    }

    public boolean isCanSync()
    {
        return canSync;
    }

    public void setCanSync(boolean canSync)
    {
        this.canSync = canSync;
    }

    public List<String> getBlockers()
    {
        return blockers;
    }

    public void setBlockers(List<String> blockers)
    {
        this.blockers = blockers;
    }

    public List<String> getWarnings()
    {
        return warnings;
    }

    public void setWarnings(List<String> warnings)
    {
        this.warnings = warnings;
    }
}
