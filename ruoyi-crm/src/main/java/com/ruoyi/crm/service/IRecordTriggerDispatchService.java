package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.CrmRecordDomainEvent;

/**
 * CRM Record Trigger：CRUD 提交后 dispatch 至 Next.js BFF
 */
public interface IRecordTriggerDispatchService
{
    void dispatchAfterCommit(String tenant, CrmRecordDomainEvent event);

    void registerAfterCommit(String tenant, CrmRecordDomainEvent event);
}
