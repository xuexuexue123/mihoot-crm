package com.ruoyi.crm.domain.dto.identity;

import java.util.List;

/**
 * POST /crm/identity/resolve-batch 请求体
 */
public class IdentityResolveBatchRequest
{
    private List<IdentityResolveBatchItem> items;

    public List<IdentityResolveBatchItem> getItems()
    {
        return items;
    }

    public void setItems(List<IdentityResolveBatchItem> items)
    {
        this.items = items;
    }
}
