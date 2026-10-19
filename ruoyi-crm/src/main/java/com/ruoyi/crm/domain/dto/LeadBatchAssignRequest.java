package com.ruoyi.crm.domain.dto;

import java.util.List;

/**
 * 批量指派请求
 */
public class LeadBatchAssignRequest
{
    private List<String> leadIds;
    private Long userId;
    private String snUserid;

    public List<String> getLeadIds()
    {
        return leadIds;
    }

    public void setLeadIds(List<String> leadIds)
    {
        this.leadIds = leadIds;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getSnUserid()
    {
        return snUserid;
    }

    public void setSnUserid(String snUserid)
    {
        this.snUserid = snUserid;
    }
}
