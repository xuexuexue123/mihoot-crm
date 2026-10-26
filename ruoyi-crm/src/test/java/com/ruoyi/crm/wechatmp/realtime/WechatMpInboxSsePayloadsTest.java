package com.ruoyi.crm.wechatmp.realtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;

class WechatMpInboxSsePayloadsTest
{
    @Test
    void messageCreated_requiresAppIdAndConversationId()
    {
        CrmWechatMpMessage msg = new CrmWechatMpMessage();
        msg.setId(9L);
        msg.setAppId("wx1");
        msg.setOpenId("oid");
        msg.setDirection("inbound");
        msg.setMsgType("text");
        msg.setContent("hi");
        msg.setCreateTime(new Date());

        CrmWechatMpConversation conv = new CrmWechatMpConversation();
        conv.setId(42L);
        conv.setAppId("wx1");
        conv.setOpenId("oid");
        conv.setUnreadCount(1);

        Map<String, Object> data = WechatMpInboxSsePayloads.messageCreated(msg, conv);
        assertEquals("message.created", data.get("type"));
        assertEquals("wx1", data.get("appId"));
        assertEquals(42L, data.get("conversationId"));
        assertNotNull(data.get("ts"));
        assertNotNull(data.get("message"));
        assertNotNull(data.get("conversation"));
    }

    @Test
    void conversationUpdated_partial()
    {
        Map<String, Object> partial = WechatMpInboxSsePayloads.conversationPartialFrom(
                7L, "wx2", 0, 1001L, "Alice", new Date());
        Map<String, Object> data = WechatMpInboxSsePayloads.conversationUpdated("wx2", partial);
        assertEquals("conversation.updated", data.get("type"));
        assertEquals("wx2", data.get("appId"));
        assertEquals(7L, data.get("conversationId"));
        @SuppressWarnings("unchecked")
        Map<String, Object> c = (Map<String, Object>) data.get("conversation");
        assertEquals("1001", c.get("userId"));
        assertEquals(0, c.get("unreadCount"));
    }

    @Test
    void extractAppId_fromNested()
    {
        Map<String, Object> data = WechatMpInboxSsePayloads.conversationUpdated("wx9",
                WechatMpInboxSsePayloads.conversationPartialFrom(1L, "wx9", null, null, null, null));
        assertEquals("wx9", WechatMpInboxSsePayloads.extractAppId(data));
        assertNull(WechatMpInboxSsePayloads.extractAppId(Collections.emptyMap()));
    }

    @Test
    void ping_hasType()
    {
        Map<String, Object> ping = WechatMpInboxSsePayloads.ping();
        assertEquals("ping", ping.get("type"));
        assertTrue(ping.containsKey("ts"));
    }
}
