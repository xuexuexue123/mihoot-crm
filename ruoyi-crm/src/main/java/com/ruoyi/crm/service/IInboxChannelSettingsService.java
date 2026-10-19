package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.InboxChannelSettingsUpdateRequest;
import com.ruoyi.crm.domain.dto.InboxChannelSettingsView;

/**
 * Unified Inbox channel settings (tenant-scoped enable switches).
 */
public interface IInboxChannelSettingsService
{
    InboxChannelSettingsView getView(String tenantId);

    InboxChannelSettingsView updatePartial(String tenantId, InboxChannelSettingsUpdateRequest request,
            String operator);
}
