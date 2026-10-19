package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.CrmRecordDomainEvent;
import com.ruoyi.crm.service.IRecordTriggerDispatchService;

/**
 * 四实体 CRM CRUD 挂钩公共逻辑
 */
@Component
public class RecordTriggerCrudHookSupport
{
    @Autowired
    private IRecordTriggerDispatchService recordTriggerDispatchService;

    public void afterCreate(String entityType, String recordId, Object record)
    {
        dispatch(entityType, RecordTriggerEventBuilder.buildCreateEvent(entityType, recordId, record, "ui"));
    }

    public void afterUpdate(String entityType, String recordId, Object record, Object previousRecord)
    {
        dispatch(entityType, RecordTriggerEventBuilder.buildUpdateEvent(
                entityType, recordId, record, previousRecord, "ui"));
    }

    public void afterDelete(String entityType, String recordId, Object record)
    {
        dispatch(entityType, RecordTriggerEventBuilder.buildDeleteEvent(entityType, recordId, record, "ui"));
    }

    private void dispatch(String entityType, CrmRecordDomainEvent event)
    {
        if (event == null || StringUtils.isBlank(entityType))
        {
            return;
        }
        String tenant = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenant))
        {
            return;
        }
        recordTriggerDispatchService.registerAfterCommit(tenant, event);
    }
}
