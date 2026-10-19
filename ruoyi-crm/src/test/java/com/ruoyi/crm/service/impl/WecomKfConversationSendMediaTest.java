package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class WecomKfConversationSendMediaTest
{
    @Test
    void resolveMediaIdFromContent_rawAndJson()
    {
        assertEquals("MEDIA1", WecomKfConversationServiceImpl.resolveMediaIdFromContent("MEDIA1"));
        assertEquals("MEDIA2", WecomKfConversationServiceImpl.resolveMediaIdFromContent("{\"media_id\":\"MEDIA2\"}"));
        assertEquals("MEDIA3", WecomKfConversationServiceImpl.resolveMediaIdFromContent("{\"mediaId\":\"MEDIA3\"}"));
        assertNull(WecomKfConversationServiceImpl.resolveMediaIdFromContent("  "));
        assertNull(WecomKfConversationServiceImpl.resolveMediaIdFromContent(null));
    }
}
