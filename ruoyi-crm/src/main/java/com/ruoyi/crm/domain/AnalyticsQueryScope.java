package com.ruoyi.crm.domain;

import java.util.List;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * MyBatis scope object for analytics aggregate queries (carries DataScope + filters).
 */
public class AnalyticsQueryScope extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String filterOwnerId;

    private String filterOrgId;

    private String filterStage;

    private String filterAccountId;

    private String dateFrom;

    private String dateTo;

    private String dateField;

    private List<String> excludeStages;

    public String getFilterOwnerId()
    {
        return filterOwnerId;
    }

    public void setFilterOwnerId(String filterOwnerId)
    {
        this.filterOwnerId = filterOwnerId;
    }

    public String getFilterOrgId()
    {
        return filterOrgId;
    }

    public void setFilterOrgId(String filterOrgId)
    {
        this.filterOrgId = filterOrgId;
    }

    public String getFilterStage()
    {
        return filterStage;
    }

    public void setFilterStage(String filterStage)
    {
        this.filterStage = filterStage;
    }

    public String getFilterAccountId()
    {
        return filterAccountId;
    }

    public void setFilterAccountId(String filterAccountId)
    {
        this.filterAccountId = filterAccountId;
    }

    public String getDateFrom()
    {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom)
    {
        this.dateFrom = dateFrom;
    }

    public String getDateTo()
    {
        return dateTo;
    }

    public void setDateTo(String dateTo)
    {
        this.dateTo = dateTo;
    }

    public String getDateField()
    {
        return dateField;
    }

    public void setDateField(String dateField)
    {
        this.dateField = dateField;
    }

    public List<String> getExcludeStages()
    {
        return excludeStages;
    }

    public void setExcludeStages(List<String> excludeStages)
    {
        this.excludeStages = excludeStages;
    }
}
