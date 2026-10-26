package com.ruoyi.crm.wechatmp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpQrcodeMapper;

@ExtendWith(MockitoExtension.class)
class EventWechatMpAppIdResolverTest
{
    private static final String EVENT_ID = "evt-1";

    @Mock
    private CrmEventMapper crmEventMapper;

    @Mock
    private ICrmWechatMpConfigService wechatMpConfigService;

    @Mock
    private CrmWechatMpQrcodeMapper wechatMpQrcodeMapper;

    @InjectMocks
    private EventWechatMpAppIdResolver resolver;

    @Test
    void resolvesConfiguredConnectedAppId()
    {
        CrmEvent event = eventWithAppId("wx-bound");
        when(wechatMpConfigService.isConnected("wx-bound")).thenReturn(true);

        assertEquals("wx-bound", resolver.resolve(event));
    }

    @Test
    void resolvesSingleConnectedTenantWhenFieldEmpty()
    {
        CrmEvent event = eventWithAppId(null);
        when(wechatMpConfigService.listConnectedAppIds()).thenReturn(Collections.singletonList("wx-only"));

        assertEquals("wx-only", resolver.resolve(event));
    }

    @Test
    void resolvesSingleQrcodeAppIdFallback()
    {
        CrmEvent event = eventWithAppId(null);
        when(wechatMpConfigService.listConnectedAppIds()).thenReturn(Arrays.asList("wx-a", "wx-b"));
        when(wechatMpQrcodeMapper.selectDistinctAppIdsByEventId(EVENT_ID))
                .thenReturn(Collections.singletonList("wx-qr"));

        assertEquals("wx-qr", resolver.resolve(event));
    }

    @Test
    void returnsNullForMultiAccountWithoutBindingOrQrcode()
    {
        CrmEvent event = eventWithAppId(null);
        when(crmEventMapper.selectCrmEventByEventId(EVENT_ID)).thenReturn(event);
        when(wechatMpConfigService.listConnectedAppIds()).thenReturn(Arrays.asList("wx-a", "wx-b"));
        when(wechatMpQrcodeMapper.selectDistinctAppIdsByEventId(EVENT_ID))
                .thenReturn(Collections.emptyList());

        assertNull(resolver.resolve(EVENT_ID));
    }

    @Test
    void ignoresDisconnectedEventFieldAndFallsBack()
    {
        CrmEvent event = eventWithAppId("wx-stale");
        when(wechatMpConfigService.isConnected("wx-stale")).thenReturn(false);
        when(wechatMpConfigService.listConnectedAppIds()).thenReturn(Collections.singletonList("wx-only"));

        assertEquals("wx-only", resolver.resolve(event));
    }

    private static CrmEvent eventWithAppId(String appId)
    {
        CrmEvent event = new CrmEvent();
        event.setEventId(EVENT_ID);
        event.setWechatMpAppId(appId);
        return event;
    }
}
