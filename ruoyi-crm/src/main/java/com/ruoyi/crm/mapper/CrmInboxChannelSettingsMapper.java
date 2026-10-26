package com.ruoyi.crm.mapper;

import com.ruoyi.crm.domain.CrmInboxChannelSettings;

/**
 * Unified Inbox channel settings Mapper.
 */
public interface CrmInboxChannelSettingsMapper
{
    CrmInboxChannelSettings selectByTenantId(String tenantId);

    int upsert(CrmInboxChannelSettings settings);
}
