package com.ruoyi.crm.domain.dto.wecom;

/**
 * 已同步到企微商品图册的产品映射项。
 */
public class WecomSyncedProductItem
{
    /** CRM 产品 ID */
    private String productId;

    /** 企业微信商品图册 product_id */
    private String wecomProductId;

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public String getWecomProductId()
    {
        return wecomProductId;
    }

    public void setWecomProductId(String wecomProductId)
    {
        this.wecomProductId = wecomProductId;
    }
}
