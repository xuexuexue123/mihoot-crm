package com.ruoyi.crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmInboxChannelSettings;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.mapper.CrmInboxChannelSettingsMapper;

/**
 * Read channel enable flags for API enforcement and inbound hooks.
 */
@Service
public class InboxChannelGateService
{
    @Autowired
    private CrmInboxChannelSettingsMapper settingsMapper;

    public CrmInboxChannelSettings loadSettings(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            return defaultsFor(null);
        }
        CrmInboxChannelSettings row = settingsMapper.selectByTenantId(tenantId.trim());
        if (row == null)
        {
            return defaultsFor(tenantId.trim());
        }
        row.setTenantId(tenantId.trim());
        return row;
    }

    public boolean isEnabled(String tenantId, InboxChannel channel)
    {
        if (channel == null || StringUtils.isBlank(tenantId))
        {
            return true;
        }
        return loadSettings(tenantId).isEnabled(channel);
    }

    public boolean isEnabled(String tenantId, String channelCode)
    {
        return isEnabled(tenantId, InboxChannel.fromCode(channelCode));
    }

    public void evict(String tenantId)
    {
        // v1: no cache; hook for future Caffeine eviction
    }

    public static CrmInboxChannelSettings defaultsFor(String tenantId)
    {
        CrmInboxChannelSettings settings = new CrmInboxChannelSettings();
        settings.setTenantId(tenantId);
        settings.setWhatsappEnabled(true);
        settings.setMessengerEnabled(true);
        settings.setWechatMpEnabled(true);
        settings.setWebchatEnabled(true);
        settings.setWecomKfEnabled(true);
        return settings;
    }
}
