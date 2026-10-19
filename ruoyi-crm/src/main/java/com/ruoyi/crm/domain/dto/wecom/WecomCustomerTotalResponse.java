package com.ruoyi.crm.domain.dto.wecom;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信全企业客户总数统计响应
 */
public class WecomCustomerTotalResponse
{
    private int totalStaff;
    private int totalUniqueCustomers;
    private int totalRawCustomers;
    private List<WecomStaffCustomerItem> items = new ArrayList<>();

    public int getTotalStaff()
    {
        return totalStaff;
    }

    public void setTotalStaff(int totalStaff)
    {
        this.totalStaff = totalStaff;
    }

    public int getTotalUniqueCustomers()
    {
        return totalUniqueCustomers;
    }

    public void setTotalUniqueCustomers(int totalUniqueCustomers)
    {
        this.totalUniqueCustomers = totalUniqueCustomers;
    }

    public int getTotalRawCustomers()
    {
        return totalRawCustomers;
    }

    public void setTotalRawCustomers(int totalRawCustomers)
    {
        this.totalRawCustomers = totalRawCustomers;
    }

    public List<WecomStaffCustomerItem> getItems()
    {
        return items;
    }

    public void setItems(List<WecomStaffCustomerItem> items)
    {
        this.items = items;
    }
}
