package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 企业微信商品图册 API 响应体。
 * 对应 add_product_album / update_product_album 接口的返回。
 */
public class WecomProductAlbumResponse
{
    private Integer errcode;

    private String errmsg;

    @JsonProperty("product_id")
    private String productId;

    public Integer getErrcode()
    {
        return errcode;
    }

    public void setErrcode(Integer errcode)
    {
        this.errcode = errcode;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public void setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
    }

    @JsonProperty("product_id")
    public String getProductId()
    {
        return productId;
    }

    @JsonProperty("product_id")
    public void setProductId(String productId)
    {
        this.productId = productId;
    }
}
