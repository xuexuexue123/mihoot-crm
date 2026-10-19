package com.ruoyi.crm.mobile.push;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.ruoyi.crm.domain.CrmMobilePushDevice;
import com.ruoyi.crm.mapper.InboxMobilePushBadgeMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InboxMobilePushServiceTest
{
    @Mock
    private MobilePushProperties properties;

    @Mock
    private MobilePushDeviceService deviceService;

    @Mock
    private FcmPushClient fcmPushClient;

    @Mock
    private InboxMobilePushBadgeMapper badgeMapper;

    @InjectMocks
    private InboxMobilePushService pushService;

    @BeforeEach
    void enablePush()
    {
        when(properties.isEnabled()).thenReturn(true);
    }

    private void enableFcmReady()
    {
        when(properties.isReady()).thenReturn(true);
    }

    @Test
    void dispatch_skipsWhenNoOwner()
    {
        InboxMobilePushRequest req = baseRequest();
        req.setOwnerUserId(null);
        pushService.dispatch(req);
        verify(deviceService, never()).listByUser(anyString(), any());
    }

    @Test
    void dispatch_skipsWhenNoDevices()
    {
        when(deviceService.listByUser("t1", 5L)).thenReturn(Collections.emptyList());
        pushService.dispatch(baseRequest());
        verify(fcmPushClient, never()).sendNotification(anyString(), anyString(), anyString(),
                anyMap(), anyInt(), anyString());
    }

    @Test
    void dispatch_sendsToRegisteredDevices()
    {
        enableFcmReady();
        CrmMobilePushDevice device = new CrmMobilePushDevice();
        device.setPushToken("tok1");
        device.setPlatform("android");
        when(deviceService.listByUser("t1", 5L)).thenReturn(Collections.singletonList(device));
        when(badgeMapper.sumMineUnread("t1", 5L)).thenReturn(3);

        pushService.dispatch(baseRequest());

        verify(fcmPushClient).sendNotification(eq("tok1"), anyString(), anyString(), anyMap(), eq(3), eq("android"));
    }

    @Test
    void dispatch_deletesUnregisteredToken()
    {
        enableFcmReady();
        CrmMobilePushDevice device = new CrmMobilePushDevice();
        device.setPushToken("stale");
        device.setPlatform("ios");
        when(deviceService.listByUser("t1", 5L)).thenReturn(Collections.singletonList(device));
        when(badgeMapper.sumMineUnread("t1", 5L)).thenReturn(1);
        when(fcmPushClient.sendNotification(anyString(), anyString(), anyString(), anyMap(), anyInt(), anyString()))
                .thenThrow(new FcmPushClient.FcmUnregisteredTokenException("stale"));

        pushService.dispatch(baseRequest());

        verify(deviceService).deleteStaleToken("t1", "stale");
    }

    @Test
    void dispatch_noOpWhenDisabled()
    {
        when(properties.isEnabled()).thenReturn(false);
        pushService.dispatch(baseRequest());
        verify(deviceService, never()).listByUser(anyString(), any());
    }

    private static InboxMobilePushRequest baseRequest()
    {
        InboxMobilePushRequest req = new InboxMobilePushRequest();
        req.setTenantId("t1");
        req.setChannel(InboxMobilePushRequest.CHANNEL_WHATSAPP);
        req.setConversationId(99L);
        req.setConversationKey("whatsapp:99");
        req.setOwnerUserId(5L);
        req.setMessageType("text");
        req.setMessageContent("hello");
        req.setPeerDisplayName("Alice");
        req.setConversationUnread(1);
        return req;
    }
}
