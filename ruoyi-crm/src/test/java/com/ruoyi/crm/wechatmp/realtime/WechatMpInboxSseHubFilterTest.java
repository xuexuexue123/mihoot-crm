package com.ruoyi.crm.wechatmp.realtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class WechatMpInboxSseHubFilterTest
{
    private WechatMpInboxSseHub hub;

    @BeforeEach
    void setUp()
    {
        hub = new WechatMpInboxSseHub();
        hub.startHeartbeat();
    }

    @AfterEach
    void tearDown()
    {
        hub.shutdown();
    }

    @Test
    void broadcast_filtersByVisibleAppIds()
    {
        SseEmitter allowed = hub.register("t1", Arrays.asList("wx-a", "wx-b"));
        SseEmitter denied = hub.register("t1", Collections.singletonList("wx-other"));

        assertTrue(hub.hasLocalSubscribers("t1"));

        Map<String, Object> payload = WechatMpInboxSsePayloads.conversationUpdated("wx-a",
                WechatMpInboxSsePayloads.conversationPartialFrom(1L, "wx-a", 0, null, null, null));

        // Completing denied emitter should not break broadcast to allowed
        AtomicInteger errors = new AtomicInteger();
        denied.onError(ex -> errors.incrementAndGet());

        hub.broadcast("t1", WechatMpInboxSsePayloads.EVENT_CONVERSATION_UPDATED, payload);

        // Both still registered until IO fails; filter is visibility not removal
        assertTrue(hub.hasLocalSubscribers("t1"));
        assertEquals("wx-a", WechatMpInboxSsePayloads.extractAppId(payload));

        hub.remove("t1", allowed);
        hub.remove("t1", denied);
        assertFalse(hub.hasLocalSubscribers("t1"));
    }

    @Test
    void register_sendsInitialPing()
    {
        SseEmitter emitter = hub.register("t2", Collections.singletonList("wx1"));
        assertTrue(hub.hasLocalSubscribers("t2"));
        hub.remove("t2", emitter);
    }
}
