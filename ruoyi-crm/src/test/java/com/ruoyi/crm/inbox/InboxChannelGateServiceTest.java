package com.ruoyi.crm.inbox;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmInboxChannelSettings;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.mapper.CrmInboxChannelSettingsMapper;
import com.ruoyi.crm.service.InboxChannelGateService;

@ExtendWith(MockitoExtension.class)
class InboxChannelGateServiceTest
{
    @Mock
    private CrmInboxChannelSettingsMapper settingsMapper;

    @InjectMocks
    private InboxChannelGateService gateService;

    @Test
    void isEnabled_defaultsTrueWhenMissingRow()
    {
        when(settingsMapper.selectByTenantId("t1")).thenReturn(null);
        assertTrue(gateService.isEnabled("t1", InboxChannel.WHATSAPP));
    }

    @Test
    void isEnabled_respectsStoredFlags()
    {
        CrmInboxChannelSettings row = InboxChannelGateService.defaultsFor("t1");
        row.setWhatsappEnabled(false);
        when(settingsMapper.selectByTenantId("t1")).thenReturn(row);

        assertFalse(gateService.isEnabled("t1", InboxChannel.WHATSAPP));
        assertTrue(gateService.isEnabled("t1", InboxChannel.MESSENGER));
        assertTrue(gateService.isEnabled("t1", InboxChannel.WECOM_KF));
    }

    @Test
    void isEnabled_respectsWecomKfFlag()
    {
        CrmInboxChannelSettings row = InboxChannelGateService.defaultsFor("t1");
        row.setWecomKfEnabled(false);
        when(settingsMapper.selectByTenantId("t1")).thenReturn(row);

        assertFalse(gateService.isEnabled("t1", InboxChannel.WECOM_KF));
        assertTrue(gateService.isEnabled("t1", InboxChannel.WECHAT_MP));
    }
}
