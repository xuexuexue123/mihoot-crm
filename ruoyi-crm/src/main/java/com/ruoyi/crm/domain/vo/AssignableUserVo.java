package com.ruoyi.crm.domain.vo;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;

/**
 * 可分配用户列表行
 */
public class AssignableUserVo
{
    private Long userId;
    private Long deptId;
    private String userName;
    private String nickName;
    private String snUserid;
    private String status;
    private SysDept dept;
    private List<SysRole> roles;
    private Boolean assignable;
    private String assignBlockReason;
    private String ownerSelectValue;
    private Integer activeLeadCount;

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

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getSnUserid()
    {
        return snUserid;
    }

    public void setSnUserid(String snUserid)
    {
        this.snUserid = snUserid;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public SysDept getDept()
    {
        return dept;
    }

    public void setDept(SysDept dept)
    {
        this.dept = dept;
    }

    public List<SysRole> getRoles()
    {
        return roles;
    }

    public void setRoles(List<SysRole> roles)
    {
        this.roles = roles;
    }

    public Boolean getAssignable()
    {
        return assignable;
    }

    public void setAssignable(Boolean assignable)
    {
        this.assignable = assignable;
    }

    public String getAssignBlockReason()
    {
        return assignBlockReason;
    }

    public void setAssignBlockReason(String assignBlockReason)
    {
        this.assignBlockReason = assignBlockReason;
    }

    public String getOwnerSelectValue()
    {
        return ownerSelectValue;
    }

    public void setOwnerSelectValue(String ownerSelectValue)
    {
        this.ownerSelectValue = ownerSelectValue;
    }

    public Integer getActiveLeadCount()
    {
        return activeLeadCount;
    }

    public void setActiveLeadCount(Integer activeLeadCount)
    {
        this.activeLeadCount = activeLeadCount;
    }
}
