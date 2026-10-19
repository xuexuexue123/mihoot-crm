package com.ruoyi.crm.wecom.realtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class WecomKfInboxSseHubFilterTest
{
    private WecomKfInboxSseHub hub;

    @BeforeEach
    void setUp()
    {
        hub = new WecomKfInboxSseHub();
        hub.startHeartbeat();
    }

    @AfterEach
    void tearDown()
    {
        hub.shutdown();
    }

    @Test
    void broadcast_filtersByVisibleOpenKfids()
    {
        SseEmitter allowed = hub.register("t1", Arrays.asList("wkf-a", "wkf-b"));
        SseEmitter denied = hub.register("t1", Collections.singletonList("wkf-other"));

        assertTrue(hub.hasLocalSubscribers("t1"));

        Map<String, Object> payload = WecomKfInboxSsePayloads.conversationUpdated("wkf-a",
                WecomKfInboxSsePayloads.conversationPartialFrom(1L, "wkf-a", 0, null, null, null));

        hub.broadcast("t1", WecomKfInboxSsePayloads.EVENT_CONVERSATION_UPDATED, payload);

        assertTrue(hub.hasLocalSubscribers("t1"));
        assertEquals("wkf-a", WecomKfInboxSsePayloads.extractOpenKfid(payload));

        hub.remove("t1", allowed);
        hub.remove("t1", denied);
        assertFalse(hub.hasLocalSubscribers("t1"));
    }

    @Test
    void broadcast_emptyVisible_allowsAll()
    {
        SseEmitter emitter = hub.register("t2", Collections.emptyList());
        Map<String, Object> payload = WecomKfInboxSsePayloads.conversationUpdated("any-kf",
                WecomKfInboxSsePayloads.conversationPartialFrom(2L, "any-kf", 0, null, null, null));
        hub.broadcast("t2", WecomKfInboxSsePayloads.EVENT_CONVERSATION_UPDATED, payload);
        assertTrue(hub.hasLocalSubscribers("t2"));
        hub.remove("t2", emitter);
    }

    @Test
    void closeTenant_removesSubscribers()
    {
        hub.register("t3", Collections.singletonList("wkf1"));
        assertTrue(hub.hasLocalSubscribers("t3"));
        hub.closeTenant("t3");
        assertFalse(hub.hasLocalSubscribers("t3"));
    }

    @Test
    void register_sendsInitialPing()
    {
        SseEmitter emitter = hub.register("t4", Collections.singletonList("wkf1"));
        assertTrue(hub.hasLocalSubscribers("t4"));
        hub.remove("t4", emitter);
    }
}
