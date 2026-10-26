package com.ruoyi.crm.domain.dto.wecom;

/**
 * 企业微信成员客户数明细
 */
public class WecomStaffCustomerItem
{
    private String userid;
    private String name;
    private int customerCnt;
    private boolean hasFollowPermission = true;

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getCustomerCnt()
    {
        return customerCnt;
    }

    public void setCustomerCnt(int customerCnt)
    {
        this.customerCnt = customerCnt;
    }

    public boolean isHasFollowPermission()
    {
        return hasFollowPermission;
    }

    public void setHasFollowPermission(boolean hasFollowPermission)
    {
        this.hasFollowPermission = hasFollowPermission;
    }
}
