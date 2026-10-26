package com.ruoyi.crm.domain.dto;

import java.util.List;

/**
 * POST /crm/record-trigger/match 请求体
 */
public class RecordTriggerMatchRequest
{
    private String tenant;

    private String entityType;

    private String operation;

    private String recordId;

    private List<String> changedFields;

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public String getEntityType()
    {
        return entityType;
    }

    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }

    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public String getRecordId()
    {
        return recordId;
    }

    public void setRecordId(String recordId)
    {
        this.recordId = recordId;
    }

    public List<String> getChangedFields()
    {
        return changedFields;
    }

    public void setChangedFields(List<String> changedFields)
    {
        this.changedFields = changedFields;
    }
}
