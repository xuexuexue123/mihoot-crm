package com.ruoyi.crm.mapper;

/**
 * 快照表中去重成员 userid + 显示名
 */
public class CrmWecomStatsMemberNameRow
{
    private String userid;
    private String name;

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
}
