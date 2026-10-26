package com.ruoyi.crm.wecom.realtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.CrmWecomKfMessage;

class WecomKfInboxSsePayloadsTest
{
    @Test
    void messageCreated_requiresOpenKfidAndConversationId()
    {
        CrmWecomKfMessage msg = new CrmWecomKfMessage();
        msg.setId(9L);
        msg.setOpenKfid("wkf1");
        msg.setExternalUserid("ext1");
        msg.setDirection(1);
        msg.setMsgtype("text");
        msg.setContent("hi");
        msg.setMsgid("m1");
        msg.setSendTime(1730000000L);
        msg.setCreatedAt(new Date());

        CrmWecomKfConversation conv = new CrmWecomKfConversation();
        conv.setId(42L);
        conv.setOpenKfid("wkf1");
        conv.setExternalUserid("ext1");
        conv.setUnreadCount(1);

        Map<String, Object> data = WecomKfInboxSsePayloads.messageCreated(msg, conv);
        assertEquals("message.created", data.get("type"));
        assertEquals("wkf1", data.get("openKfid"));
        assertEquals(42L, data.get("conversationId"));
        assertNotNull(data.get("ts"));
        assertNotNull(data.get("message"));
        assertNotNull(data.get("conversation"));
    }

    @Test
    void conversationUpdated_partial()
    {
        Map<String, Object> partial = WecomKfInboxSsePayloads.conversationPartialFrom(
                7L, "wkf2", 0, 1001L, "Alice", new Date());
        Map<String, Object> data = WecomKfInboxSsePayloads.conversationUpdated("wkf2", partial);
        assertEquals("conversation.updated", data.get("type"));
        assertEquals("wkf2", data.get("openKfid"));
        assertEquals(7L, data.get("conversationId"));
        @SuppressWarnings("unchecked")
        Map<String, Object> c = (Map<String, Object>) data.get("conversation");
        assertEquals("1001", c.get("userId"));
        assertEquals(0, c.get("unreadCount"));
    }

    @Test
    void extractOpenKfid_fromNested()
    {
        Map<String, Object> data = WecomKfInboxSsePayloads.conversationUpdated("wkf9",
                WecomKfInboxSsePayloads.conversationPartialFrom(1L, "wkf9", null, null, null, null));
        assertEquals("wkf9", WecomKfInboxSsePayloads.extractOpenKfid(data));
        assertNull(WecomKfInboxSsePayloads.extractOpenKfid(Collections.emptyMap()));
    }

    @Test
    void ping_hasType()
    {
        Map<String, Object> ping = WecomKfInboxSsePayloads.ping();
        assertEquals("ping", ping.get("type"));
        assertTrue(ping.containsKey("ts"));
    }

    @Test
    void messageRecord_includesMediaUrl()
    {
        CrmWecomKfMessage msg = new CrmWecomKfMessage();
        msg.setOpenKfid("wkf1");
        msg.setMediaId("MEDIA1");
        msg.setMsgtype("image");
        Map<String, Object> m = WecomKfInboxSsePayloads.toMessageRecord(msg, 3L);
        assertEquals("/crm/wecom/kf/messages/media?openKfid=wkf1&mediaId=MEDIA1", m.get("mediaUrl"));
    }

    @Test
    void messageRecord_aiSourceMarksSenderType()
    {
        CrmWecomKfMessage msg = new CrmWecomKfMessage();
        msg.setOpenKfid("wkf1");
        msg.setMsgtype("text");
        msg.setContent("auto");
        msg.setSource("ai_inbox_agent");
        Map<String, Object> m = WecomKfInboxSsePayloads.toMessageRecord(msg, 3L);
        assertEquals("ai_inbox_agent", m.get("source"));
        assertEquals("ai", m.get("senderType"));
    }

    @Test
    void conversationPartial_includesHostedByAi()
    {
        CrmWecomKfConversation conv = new CrmWecomKfConversation();
        conv.setId(5L);
        conv.setOpenKfid("wkf1");
        conv.setHostedByAi(Boolean.FALSE);
        Map<String, Object> c = WecomKfInboxSsePayloads.toConversationPartial(conv);
        assertEquals(Boolean.FALSE, c.get("hostedByAi"));

        Map<String, Object> partial = WecomKfInboxSsePayloads.conversationPartialFrom(
                5L, "wkf1", 0, 1L, "Bot", null, Boolean.TRUE);
        assertEquals(Boolean.TRUE, partial.get("hostedByAi"));
    }
}
