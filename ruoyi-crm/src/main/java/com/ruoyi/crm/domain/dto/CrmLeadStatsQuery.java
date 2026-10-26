package com.ruoyi.crm.domain.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class CrmLeadStatsQuery extends BaseEntity
{
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date from;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date to;

    private String orgId;
    private Long deptId;
    private String leadType = "business";
    private String groupBy = "stage";
    private Boolean includeConverted = false;
    private Integer limit = 20;
    private String granularity = "day";

    public Date getFrom()
    {
        return from;
    }

    public void setFrom(Date from)
    {
        this.from = from;
    }

    public Date getTo()
    {
        return to;
    }

    public void setTo(Date to)
    {
        this.to = to;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getLeadType()
    {
        return leadType;
    }

    public void setLeadType(String leadType)
    {
        this.leadType = leadType;
    }

    public String getGroupBy()
    {
        return groupBy;
    }

    public void setGroupBy(String groupBy)
    {
        this.groupBy = groupBy;
    }

    public Boolean getIncludeConverted()
    {
        return includeConverted;
    }

    public void setIncludeConverted(Boolean includeConverted)
    {
        this.includeConverted = includeConverted;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }

    public String getGranularity()
    {
        return granularity;
    }

    public void setGranularity(String granularity)
    {
        this.granularity = granularity;
    }
}
