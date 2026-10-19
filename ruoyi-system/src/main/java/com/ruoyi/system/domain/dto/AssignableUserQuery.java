package com.ruoyi.system.domain.dto;

import java.util.List;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 可分配用户列表查询
 */
public class AssignableUserQuery extends BaseEntity
{
    private Integer pageNum = 1;
    private Integer pageSize = 50;
    private String purpose = "lead_assign";
    private Boolean wecomBound;
    private String roleKeys;
    private String status = "0";
    private Long deptId;
    private String orgId;
    private String scope = "tenant";
    private String keyword;
    private String snUserid;
    private Long userId;
    private Boolean includeWorkload = false;
    /** 解析后的角色 key 列表，供 MyBatis IN 使用 */
    private List<String> roleKeyList;
    private Long scopeUserId;
    /** 由 service 注入：scope=dept/dept_and_child 时的基准部门 */
    private Long scopeDeptId;

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getPurpose()
    {
        return purpose;
    }

    public void setPurpose(String purpose)
    {
        this.purpose = purpose;
    }

    public Boolean getWecomBound()
    {
        return wecomBound;
    }

    public void setWecomBound(Boolean wecomBound)
    {
        this.wecomBound = wecomBound;
    }

    public String getRoleKeys()
    {
        return roleKeys;
    }

    public void setRoleKeys(String roleKeys)
    {
        this.roleKeys = roleKeys;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public String getSnUserid()
    {
        return snUserid;
    }

    public void setSnUserid(String snUserid)
    {
        this.snUserid = snUserid;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Boolean getIncludeWorkload()
    {
        return includeWorkload;
    }

    public void setIncludeWorkload(Boolean includeWorkload)
    {
        this.includeWorkload = includeWorkload;
    }

    public List<String> getRoleKeyList()
    {
        return roleKeyList;
    }

    public void setRoleKeyList(List<String> roleKeyList)
    {
        this.roleKeyList = roleKeyList;
    }

    public Long getScopeUserId()
    {
        return scopeUserId;
    }

    public void setScopeUserId(Long scopeUserId)
    {
        this.scopeUserId = scopeUserId;
    }

    public Long getScopeDeptId()
    {
        return scopeDeptId;
    }

    public void setScopeDeptId(Long scopeDeptId)
    {
        this.scopeDeptId = scopeDeptId;
    }
}
