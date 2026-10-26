package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.CrmWhatsappTemplate;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppSendResult;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappConversationMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateMapper;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsAppClient;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxEventPublisher;

@ExtendWith(MockitoExtension.class)
class CrmWhatsappMessageServiceOwnerGateTest
{
    @Mock
    private CrmWhatsappConfigMapper configMapper;

    @Mock
    private CrmWhatsappConversationMapper conversationMapper;

    @Mock
    private CrmWhatsappMessageMapper messageMapper;

    @Mock
    private CrmWhatsappTemplateMapper templateMapper;

    @Mock
    private WhatsAppClient whatsappClient;

    @Mock
    private TokenEncryptor tokenEncryptor;

    @Mock
    private WhatsAppInboxEventPublisher inboxEventPublisher;

    @Mock
    private InboxChannelGateService inboxChannelGateService;

    @InjectMocks
    private CrmWhatsappMessageServiceImpl service;

    @BeforeEach
    void setUp()
    {
        when(inboxChannelGateService.isEnabled(anyString(), eq(InboxChannel.WHATSAPP))).thenReturn(true);

        SysUser user = new SysUser();
        user.setUserId(1001L);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1001L);
        loginUser.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));

        CrmWhatsappConfig config = new CrmWhatsappConfig();
        config.setAccessTokenEnc("enc");
        config.setPhoneNumberId("pnid");
        lenient().when(configMapper.selectByTenantId("t1")).thenReturn(config);
        lenient().when(tokenEncryptor.decrypt("enc")).thenReturn("token");
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void send_rejectsWhenInboxChannelDisabled()
    {
        when(inboxChannelGateService.isEnabled("t1", InboxChannel.WHATSAPP)).thenReturn(false);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.sendMessage("t1", "86138", null, "text", "hi", null, null, null, null));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode().intValue());
        assertEquals("inbox_channel_disabled", ex.getMessage());
        verify(whatsappClient, never()).sendTextMessage(anyString(), anyString(), anyString(), anyString(), anyBoolean());
        verify(conversationMapper, never()).selectByWaId(anyString(), anyString());
    }

    @Test
    void send_rejectsUnclaimedConversation()
    {
        CrmWhatsappConversation conv = new CrmWhatsappConversation();
        conv.setId(1L);
        conv.setUserId(null);
        when(conversationMapper.selectByWaId("t1", "86138")).thenReturn(conv);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.sendMessage("t1", "86138", null, "text", "hi", null, null, null, null));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode().intValue());
        verify(whatsappClient, never()).sendTextMessage(anyString(), anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void send_rejectsNonOwner()
    {
        CrmWhatsappConversation conv = new CrmWhatsappConversation();
        conv.setId(1L);
        conv.setUserId(2002L);
        when(conversationMapper.selectByWaId("t1", "86138")).thenReturn(conv);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.sendMessage("t1", "86138", null, "text", "hi", null, null, null, null));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode().intValue());
    }

    @Test
    void send_allowsOwner()
    {
        CrmWhatsappConversation conv = ownedConv();
        conv.setLastCustomerMessageAt(new Date());
        when(conversationMapper.selectByWaId("t1", "86138")).thenReturn(conv);
        when(whatsappClient.sendTextMessage(eq("pnid"), eq("86138"), eq("hi"), eq("token"), eq(false)))
                .thenReturn(WhatsAppSendResult.success("wamid.1", "86138"));

        WhatsAppSendResult result = service.sendMessage("t1", "86138", null, "text", "hi", null, null, null, null);
        assertEquals(true, result.isSuccess());
        verify(whatsappClient).sendTextMessage(eq("pnid"), eq("86138"), eq("hi"), eq("token"), eq(false));
    }

    @Test
    void send_rejectsFreeFormWhenWindowExpired()
    {
        CrmWhatsappConversation conv = ownedConv();
        conv.setLastCustomerMessageAt(new Date(System.currentTimeMillis() - 90_000_000L));
        when(conversationMapper.selectByWaId("t1", "86138")).thenReturn(conv);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.sendMessage("t1", "86138", null, "text", "hi", null, null, null, null));
        assertEquals(400, ex.getCode().intValue());
        assertTrue(ex.getMessage().contains("会话窗已过期"));
        verify(whatsappClient, never()).sendTextMessage(anyString(), anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void send_allowsTemplateWhenWindowExpired()
    {
        CrmWhatsappConversation conv = ownedConv();
        conv.setLastCustomerMessageAt(new Date(System.currentTimeMillis() - 90_000_000L));
        when(conversationMapper.selectByWaId("t1", "86138")).thenReturn(conv);

        CrmWhatsappTemplate tpl = new CrmWhatsappTemplate();
        tpl.setTemplateName("hello");
        tpl.setLanguage("zh_CN");
        tpl.setStatus("APPROVED");
        when(templateMapper.selectByTemplate("t1", "hello", "zh_CN")).thenReturn(tpl);
        when(whatsappClient.sendTemplateMessage(eq("pnid"), eq("86138"), eq("hello"), eq("zh_CN"),
                isNull(), eq("token")))
                .thenReturn(WhatsAppSendResult.success("wamid.tpl", "86138"));

        Map<String, Object> template = new HashMap<>();
        template.put("name", "hello");
        template.put("language", "zh_CN");

        WhatsAppSendResult result = service.sendMessage(
                "t1", "86138", null, "template", null, null, null, null, template);
        assertTrue(result.isSuccess());
        verify(whatsappClient).sendTemplateMessage(eq("pnid"), eq("86138"), eq("hello"), eq("zh_CN"),
                isNull(), eq("token"));
        verify(messageMapper).insert(any());
    }

    @Test
    void send_rejectsUnapprovedTemplate()
    {
        CrmWhatsappConversation conv = ownedConv();
        when(conversationMapper.selectByWaId("t1", "86138")).thenReturn(conv);

        CrmWhatsappTemplate tpl = new CrmWhatsappTemplate();
        tpl.setStatus("pending");
        when(templateMapper.selectByTemplate("t1", "hello", "zh_CN")).thenReturn(tpl);

        Map<String, Object> template = new HashMap<>();
        template.put("name", "hello");
        template.put("language", "zh_CN");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.sendMessage("t1", "86138", null, "template", null, null, null, null, template));
        assertEquals(400, ex.getCode().intValue());
        verify(whatsappClient, never()).sendTemplateMessage(
                anyString(), anyString(), anyString(), anyString(), any(JSONArray.class), anyString());
    }

    private static CrmWhatsappConversation ownedConv()
    {
        CrmWhatsappConversation conv = new CrmWhatsappConversation();
        conv.setId(1L);
        conv.setUserId(1001L);
        conv.setWaId("86138");
        return conv;
    }
}
