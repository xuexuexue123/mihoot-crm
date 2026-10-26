package com.ruoyi.crm.domain.dto;

import java.util.List;
import java.util.Map;

/**
 * CRM Record Trigger 领域事件（dispatch 至 Next.js BFF）
 */
public class CrmRecordDomainEvent
{
    private String entityType;

    private String eventType;

    private String recordId;

    private Map<String, Object> record;

    private Map<String, Object> previousRecord;

    private List<String> changedFields;

    private String occurredAt;

    private String source;

    private String operation;

    public String getEntityType()
    {
        return entityType;
    }

    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getRecordId()
    {
        return recordId;
    }

    public void setRecordId(String recordId)
    {
        this.recordId = recordId;
    }

    public Map<String, Object> getRecord()
    {
        return record;
    }

    public void setRecord(Map<String, Object> record)
    {
        this.record = record;
    }

    public Map<String, Object> getPreviousRecord()
    {
        return previousRecord;
    }

    public void setPreviousRecord(Map<String, Object> previousRecord)
    {
        this.previousRecord = previousRecord;
    }

    public List<String> getChangedFields()
    {
        return changedFields;
    }

    public void setChangedFields(List<String> changedFields)
    {
        this.changedFields = changedFields;
    }

    public String getOccurredAt()
    {
        return occurredAt;
    }

    public void setOccurredAt(String occurredAt)
    {
        this.occurredAt = occurredAt;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }
}
