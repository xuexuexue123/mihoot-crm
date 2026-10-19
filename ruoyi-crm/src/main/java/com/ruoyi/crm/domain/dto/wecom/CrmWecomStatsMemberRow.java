package com.ruoyi.crm.domain.dto.wecom;

/**
 * 企微统计历史成员名册行（含已删除成员）
 */
public class CrmWecomStatsMemberRow
{
    private String userid;
    private String name;
    private Boolean active;

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

    public Boolean getActive()
    {
        return active;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }
}
