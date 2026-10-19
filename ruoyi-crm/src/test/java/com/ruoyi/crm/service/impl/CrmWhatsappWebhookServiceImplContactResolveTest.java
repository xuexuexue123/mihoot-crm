package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmWhatsappConversationMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.mapper.CrmWhatsappWebhookEventMapper;
import com.ruoyi.crm.whatsapp.WhatsappIdentityResolutionSupport;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxEventPublisher;

@ExtendWith(MockitoExtension.class)
class CrmWhatsappWebhookServiceImplContactResolveTest
{
    @Mock
    private CrmWhatsappWebhookEventMapper eventMapper;

    @Mock
    private CrmWhatsappConversationMapper conversationMapper;

    @Mock
    private CrmWhatsappMessageMapper messageMapper;

    @Mock
    private ActivityEventProducer activityEventProducer;

    @Mock
    private WhatsappIdentityResolutionSupport whatsappIdentityResolutionSupport;

    @Mock
    private WhatsAppInboxEventPublisher inboxEventPublisher;

    @InjectMocks
    private CrmWhatsappWebhookServiceImpl service;

    @Test
    void processWebhook_resolvesContactAndProducesActivity()
    {
        String tenant = "t1";
        String waId = "8613800138001";
        String messageId = "wamid.1";

        when(eventMapper.selectByMessageIdAndEventType(tenant, messageId, "messages")).thenReturn(null);
        when(messageMapper.selectByMessageId(tenant, messageId)).thenReturn(null);
        when(conversationMapper.selectByWaId(tenant, waId)).thenAnswer(inv -> {
            CrmWhatsappConversation c = new CrmWhatsappConversation();
            c.setId(10L);
            c.setTenantId(tenant);
            c.setWaId(waId);
            c.setUnreadCount(0);
            c.setStatus("active");
            return c;
        });
        when(whatsappIdentityResolutionSupport.resolveContactId(waId)).thenReturn("C100");

        service.processWebhook(tenant, inboundTextBody(waId, messageId, "hello"));

        verify(conversationMapper).updateContactId(tenant, waId, "C100");
        ArgumentCaptor<ActivityEventDTO> captor = ArgumentCaptor.forClass(ActivityEventDTO.class);
        verify(activityEventProducer).produce(captor.capture());
        assertEquals("Contact", captor.getValue().getSubjectType());
        assertEquals("C100", captor.getValue().getSubjectId());
    }

    @Test
    void processWebhook_skipsActivityWhenUnresolved()
    {
        String tenant = "t1";
        String waId = "99900001111";
        String messageId = "wamid.2";

        when(eventMapper.selectByMessageIdAndEventType(tenant, messageId, "messages")).thenReturn(null);
        when(messageMapper.selectByMessageId(tenant, messageId)).thenReturn(null);
        when(conversationMapper.selectByWaId(tenant, waId)).thenAnswer(inv -> {
            CrmWhatsappConversation c = new CrmWhatsappConversation();
            c.setId(11L);
            c.setTenantId(tenant);
            c.setWaId(waId);
            c.setUnreadCount(0);
            c.setStatus("active");
            return c;
        });
        when(whatsappIdentityResolutionSupport.resolveContactId(waId)).thenReturn(null);

        service.processWebhook(tenant, inboundTextBody(waId, messageId, "hi"));

        verify(conversationMapper, never()).updateContactId(anyString(), anyString(), anyString());
        verify(activityEventProducer, never()).produce(any());
        verify(messageMapper).insert(any(CrmWhatsappMessage.class));
        verify(eventMapper).updateStatus(isNull(), eq("processed"), isNull());
    }

    @Test
    void processWebhook_doesNotOverwriteExistingContactId()
    {
        String tenant = "t1";
        String waId = "8613800138001";
        String messageId = "wamid.3";

        when(eventMapper.selectByMessageIdAndEventType(tenant, messageId, "messages")).thenReturn(null);
        when(messageMapper.selectByMessageId(tenant, messageId)).thenReturn(null);
        when(conversationMapper.selectByWaId(tenant, waId)).thenAnswer(inv -> {
            CrmWhatsappConversation c = new CrmWhatsappConversation();
            c.setId(12L);
            c.setTenantId(tenant);
            c.setWaId(waId);
            c.setContactId("C_EXISTING");
            c.setUnreadCount(1);
            c.setStatus("active");
            return c;
        });

        service.processWebhook(tenant, inboundTextBody(waId, messageId, "again"));

        verify(whatsappIdentityResolutionSupport, never()).resolveContactId(anyString());
        verify(conversationMapper, never()).updateContactId(anyString(), anyString(), anyString());
        ArgumentCaptor<ActivityEventDTO> captor = ArgumentCaptor.forClass(ActivityEventDTO.class);
        verify(activityEventProducer).produce(captor.capture());
        assertEquals("C_EXISTING", captor.getValue().getSubjectId());
    }

    private static Map<String, Object> inboundTextBody(String waId, String messageId, String text)
    {
        Map<String, Object> message = new HashMap<>();
        message.put("id", messageId);
        message.put("from", waId);
        message.put("type", "text");
        message.put("timestamp", "1700000000");
        Map<String, Object> textBody = new HashMap<>();
        textBody.put("body", text);
        message.put("text", textBody);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("phone_number_id", "pnid");
        metadata.put("display_phone_number", "15550001111");

        Map<String, Object> value = new HashMap<>();
        value.put("metadata", metadata);
        value.put("messages", Collections.singletonList(message));

        Map<String, Object> change = new HashMap<>();
        change.put("field", "messages");
        change.put("value", value);

        Map<String, Object> entry = new HashMap<>();
        entry.put("changes", Collections.singletonList(change));

        Map<String, Object> body = new HashMap<>();
        body.put("entry", Collections.singletonList(entry));
        return body;
    }
}
