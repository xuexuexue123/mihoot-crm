package com.ruoyi.crm.inbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmInboxChannelSettings;
import com.ruoyi.crm.domain.dto.InboxChannelSettingsUpdateRequest;
import com.ruoyi.crm.domain.dto.InboxChannelSettingsView;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.mapper.CrmInboxChannelSettingsMapper;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.impl.CrmInboxChannelSettingsServiceImpl;
import com.ruoyi.crm.wecom.realtime.WecomKfInboxSseHub;

@ExtendWith(MockitoExtension.class)
class CrmInboxChannelSettingsServiceTest
{
    @Mock
    private CrmInboxChannelSettingsMapper settingsMapper;

    @Mock
    private InboxChannelGateService gateService;

    @Mock
    private WecomKfInboxSseHub wecomKfInboxSseHub;

    @InjectMocks
    private CrmInboxChannelSettingsServiceImpl service;

    @Test
    void getView_defaultsAllEnabledWhenNoRow()
    {
        CrmInboxChannelSettings defaults = InboxChannelGateService.defaultsFor("t1");
        when(gateService.loadSettings("t1")).thenReturn(defaults);

        InboxChannelSettingsView view = service.getView("t1");

        assertTrue(view.getWhatsapp().isEnabled());
        assertTrue(view.getMessenger().isEnabled());
        assertTrue(view.getWechatMp().isEnabled());
        assertTrue(view.getWebchat().isEnabled());
        assertTrue(view.getWecomKf().isEnabled());
    }

    @Test
    void updatePartial_mergesOnlyProvidedChannels()
    {
        CrmInboxChannelSettings current = InboxChannelGateService.defaultsFor("t1");
        when(gateService.loadSettings("t1")).thenReturn(current);

        InboxChannelSettingsUpdateRequest req = new InboxChannelSettingsUpdateRequest();
        req.setWhatsapp(new InboxChannelSettingsView.ChannelFlag(false));

        InboxChannelSettingsView view = service.updatePartial("t1", req, "admin");

        assertFalse(view.getWhatsapp().isEnabled());
        assertTrue(view.getMessenger().isEnabled());
        verify(settingsMapper).upsert(any(CrmInboxChannelSettings.class));
        verify(gateService).evict("t1");
    }

    @Test
    void updatePartial_rejectsAllDisabled()
    {
        CrmInboxChannelSettings current = InboxChannelGateService.defaultsFor("t1");
        when(gateService.loadSettings("t1")).thenReturn(current);

        InboxChannelSettingsUpdateRequest req = new InboxChannelSettingsUpdateRequest();
        req.setWhatsapp(new InboxChannelSettingsView.ChannelFlag(false));
        req.setMessenger(new InboxChannelSettingsView.ChannelFlag(false));
        req.setWechatMp(new InboxChannelSettingsView.ChannelFlag(false));
        req.setWebchat(new InboxChannelSettingsView.ChannelFlag(false));
        req.setWecomKf(new InboxChannelSettingsView.ChannelFlag(false));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.updatePartial("t1", req, "admin"));
        assertEquals("inbox_channels_all_disabled", ex.getMessage());
        verify(settingsMapper, never()).upsert(any());
    }

    @Test
    void updatePartial_disablingWecomKf_closesSse()
    {
        CrmInboxChannelSettings current = InboxChannelGateService.defaultsFor("t1");
        when(gateService.loadSettings("t1")).thenReturn(current);

        InboxChannelSettingsUpdateRequest req = new InboxChannelSettingsUpdateRequest();
        req.setWecomKf(new InboxChannelSettingsView.ChannelFlag(false));

        InboxChannelSettingsView view = service.updatePartial("t1", req, "admin");

        assertFalse(view.getWecomKf().isEnabled());
        verify(wecomKfInboxSseHub).closeTenant("t1");
    }
}
