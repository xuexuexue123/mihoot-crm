package com.ruoyi.crm.domain.dto;

import java.util.List;

/**
 * 批量状态变更请求
 */
public class ProductBatchStatusRequest
{
    private List<String> productIds;
    /** publish | disable | reactivate | archive */
    private String action;

    public List<String> getProductIds()
    {
        return productIds;
    }

    public void setProductIds(List<String> productIds)
    {
        this.productIds = productIds;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }
}
