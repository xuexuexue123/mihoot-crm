package com.ruoyi.crm.domain.dto;

/**
 * Lead 归属写入补丁（assignable resolve / assign / claim 共用）
 */
public class LeadAssignPatch
{
    private Long userId;
    private Long deptId;
    private String owner;
    private String wecomFollowUserid;
    private String status;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getWecomFollowUserid()
    {
        return wecomFollowUserid;
    }

    public void setWecomFollowUserid(String wecomFollowUserid)
    {
        this.wecomFollowUserid = wecomFollowUserid;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
