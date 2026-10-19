package com.ruoyi.crm.domain.vo;

/**
 * 批量操作单条结果
 */
public class ProductBatchItemResultVo
{
    private String productId;
    private boolean success;
    private String message;

    public ProductBatchItemResultVo()
    {
    }

    public ProductBatchItemResultVo(String productId, boolean success, String message)
    {
        this.productId = productId;
        this.success = success;
        this.message = message;
    }

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
