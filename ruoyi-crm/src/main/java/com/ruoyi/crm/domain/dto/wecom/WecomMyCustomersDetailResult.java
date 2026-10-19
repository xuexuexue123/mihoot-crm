package com.ruoyi.crm.domain.dto.wecom;

import java.util.ArrayList;
import java.util.List;

/**
 * POST /crm/wecom/my-customers/details 响应 data
 */
public class WecomMyCustomersDetailResult
{
    private List<WecomMyCustomersDetailItem> customers = new ArrayList<>();

    public List<WecomMyCustomersDetailItem> getCustomers()
    {
        return customers;
    }

    public void setCustomers(List<WecomMyCustomersDetailItem> customers)
    {
        this.customers = customers;
    }
}
