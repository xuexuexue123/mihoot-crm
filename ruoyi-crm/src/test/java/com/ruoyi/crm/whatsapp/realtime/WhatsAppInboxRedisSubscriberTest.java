package com.ruoyi.crm.whatsapp.realtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSON;

@ExtendWith(MockitoExtension.class)
class WhatsAppInboxRedisSubscriberTest
{
    @Mock
    private WhatsAppInboxSseHub sseHub;

    @InjectMocks
    private WhatsAppInboxRedisSubscriber subscriber;

    @Test
    void extractTenantIdFromChannel()
    {
        assertEquals("acme", WhatsAppInboxRedisSubscriber.extractTenantId("whatsapp:inbox:acme"));
        assertNull(WhatsAppInboxRedisSubscriber.extractTenantId("other"));
        assertNull(WhatsAppInboxRedisSubscriber.extractTenantId("whatsapp:inbox:"));
    }

    @Test
    void handleBroadcastsWhenLocalSubscribersExist()
    {
        when(sseHub.hasLocalSubscribers("t1")).thenReturn(true);
        Map<String, Object> data = new HashMap<>();
        data.put("type", "ping");
        data.put("ts", 1L);
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("event", "ping");
        envelope.put("data", data);

        subscriber.handle("whatsapp:inbox:t1", JSON.toJSONString(envelope));

        verify(sseHub).broadcast(eq("t1"), eq("ping"), any());
    }

    @Test
    void handleSkipsWhenNoLocalSubscribers()
    {
        when(sseHub.hasLocalSubscribers("t1")).thenReturn(false);
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("event", "ping");
        envelope.put("data", new HashMap<>());

        subscriber.handle("whatsapp:inbox:t1", JSON.toJSONString(envelope));

        verify(sseHub, never()).broadcast(any(), any(), any());
    }
}
