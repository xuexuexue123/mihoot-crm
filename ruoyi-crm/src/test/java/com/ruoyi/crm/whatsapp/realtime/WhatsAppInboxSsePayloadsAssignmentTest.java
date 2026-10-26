package com.ruoyi.crm.whatsapp.realtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmWhatsappConversation;

class WhatsAppInboxSsePayloadsAssignmentTest
{
    @Test
    void toConversationPartial_includesOwnerFields()
    {
        CrmWhatsappConversation conv = new CrmWhatsappConversation();
        conv.setId(5L);
        conv.setUserId(1001L);
        conv.setOwner("Alice");
        Date at = new Date();
        conv.setAssignedAt(at);
        conv.setStatus("active");

        Map<String, Object> partial = WhatsAppInboxSsePayloads.toConversationPartial(conv);
        assertEquals(1001L, partial.get("userId"));
        assertEquals("Alice", partial.get("owner"));
        assertEquals(at, partial.get("assignedAt"));
    }

    @Test
    void conversationPartialFrom_clearsAssignment()
    {
        Map<String, Object> partial = WhatsAppInboxSsePayloads.conversationPartialFrom(
                9L, null, null, null, null, null);
        assertEquals(9L, partial.get("id"));
        assertNull(partial.get("userId"));
        assertNull(partial.get("owner"));
        assertNull(partial.get("assignedAt"));
    }
}
