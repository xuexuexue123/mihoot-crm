package com.ruoyi.crm.domain.vo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContextSummaryVo
{
    private String entityType;

    private String entityId;

    private String displayName;

    private Map<String, String> fields = new LinkedHashMap<>();

    private Map<String, Long> relatedCounts = new LinkedHashMap<>();

    private List<RecentActivityItem> recentActivity;

    public static class RecentActivityItem
    {
        private String pulseType;
        private String message;
        private String createTime;

        public String getPulseType()
        {
            return pulseType;
        }

        public void setPulseType(String pulseType)
        {
            this.pulseType = pulseType;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }

        public String getCreateTime()
        {
            return createTime;
        }

        public void setCreateTime(String createTime)
        {
            this.createTime = createTime;
        }
    }

    public String getEntityType()
    {
        return entityType;
    }

    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }

    public String getEntityId()
    {
        return entityId;
    }

    public void setEntityId(String entityId)
    {
        this.entityId = entityId;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public Map<String, String> getFields()
    {
        return fields;
    }

    public void setFields(Map<String, String> fields)
    {
        this.fields = fields;
    }

    public Map<String, Long> getRelatedCounts()
    {
        return relatedCounts;
    }

    public void setRelatedCounts(Map<String, Long> relatedCounts)
    {
        this.relatedCounts = relatedCounts;
    }

    public List<RecentActivityItem> getRecentActivity()
    {
        return recentActivity;
    }

    public void setRecentActivity(List<RecentActivityItem> recentActivity)
    {
        this.recentActivity = recentActivity;
    }
}
