package com.ruoyi.crm.domain.dto.wecom;

/**
 * 企业微信商品图册同步请求体。
 * 前端已将价格（元）乘以 100 转为分，后端无需再做单位转换。
 */
public class WecomProductAlbumRequest
{
    /** CRM 产品 ID */
    private String productId;

    /** 商品名称 */
    private String productName;

    /** 商品描述/特色（可选） */
    private String description;

    /** 价格，单位：分 */
    private Integer price;

    /** 图片 URL（可选） */
    private String avatar;

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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getPrice()
    {
        return price;
    }

    public void setPrice(Integer price)
    {
        this.price = price;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }
}
