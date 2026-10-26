package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.EventTriggerDispatchPayload;

/**
 * CRM_EVENT_TRIGGER：事务提交后向 Next BFF dispatch
 */
public interface IEventTriggerDispatchService
{
    void registerAfterCommit(String tenant, EventTriggerDispatchPayload payload);

    void dispatchAfterCommit(String tenant, EventTriggerDispatchPayload payload);
}
