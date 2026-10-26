package com.ruoyi.crm.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.domain.CrmMessengerConversation;
import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.dto.messenger.MessengerSendResult;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppSendResult;
import com.ruoyi.crm.mapper.CrmMessengerConversationMapper;
import com.ruoyi.crm.mapper.CrmWebchatConversationMapper;
import com.ruoyi.crm.mapper.CrmWecomKfConversationMapper;
import com.ruoyi.crm.mapper.CrmWhatsappConversationMapper;
import com.ruoyi.crm.service.ICrmMessengerConversationService;
import com.ruoyi.crm.service.ICrmMessengerMessageService;
import com.ruoyi.crm.service.ICrmWebchatConversationService;
import com.ruoyi.crm.service.ICrmWebchatMessageService;
import com.ruoyi.crm.service.ICrmWhatsappConversationService;
import com.ruoyi.crm.service.ICrmWhatsappMessageService;
import com.ruoyi.crm.service.IInboxAgentChannelService;
import com.ruoyi.crm.service.IWecomKfConversationService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageReplyRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConversationMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpConversationService;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessageService;

@Service
public class InboxAgentChannelServiceImpl implements IInboxAgentChannelService
{
    @Value("${inbox-agent.bot-user-id:9001}")
    private Long botUserId;

    @Value("${inbox-agent.bot-owner-name:Inbox AI}")
    private String botOwnerName;

    @Autowired
    private CrmWhatsappConversationMapper whatsappConversationMapper;

    @Autowired
    private ICrmWhatsappConversationService whatsappConversationService;

    @Autowired
    private ICrmWhatsappMessageService whatsappMessageService;

    @Autowired
    private CrmWechatMpConversationMapper wechatMpConversationMapper;

    @Autowired
    private IWechatMpConversationService wechatMpConversationService;

    @Autowired
    private IWechatMpMessageService wechatMpMessageService;

    @Autowired
    private CrmMessengerConversationMapper messengerConversationMapper;

    @Autowired
    private ICrmMessengerConversationService messengerConversationService;

    @Autowired
    private ICrmMessengerMessageService messengerMessageService;

    @Autowired
    private CrmWebchatConversationMapper webchatConversationMapper;

    @Autowired
    private ICrmWebchatConversationService webchatConversationService;

    @Autowired
    private ICrmWebchatMessageService webchatMessageService;

    @Autowired
    private CrmWecomKfConversationMapper wecomKfConversationMapper;

    @Autowired
    private IWecomKfConversationService wecomKfConversationService;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> sendWhatsAppText(String tenantId, Long conversationId, String text)
    {
        assertChannelEnabled(tenantId, InboxChannel.WHATSAPP);
        if (!StringUtils.hasText(tenantId) || conversationId == null)
        {
            throw new ServiceException("tenantId and conversationId are required", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(text))
        {
            throw new ServiceException("text is required", HttpStatus.BAD_REQUEST);
        }
        CrmWhatsappConversation conv = whatsappConversationMapper.selectById(conversationId);
        if (conv == null || !tenantId.equals(conv.getTenantId()))
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        assertHostedForAi(conv.getHostedByAi());
        whatsappConversationService.claimForAiBot(conversationId, tenantId, botUserId, botOwnerName);

        WhatsAppSendResult result = whatsappMessageService.sendMessageAsAi(
                tenantId, conv.getWaId(), conv.getPhoneNumberId(), "text", text.trim(),
                null, null, null, null);

        Map<String, Object> data = new HashMap<>();
        data.put("ok", result != null && result.isSuccess());
        data.put("messageId", result != null ? result.getMessageId() : null);
        data.put("error", result != null ? result.getErrorMessage() : null);
        data.put("source", SOURCE_AI_INBOX_AGENT);
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> replyWechatMpText(Long conversationId, String text)
    {
        if (conversationId == null)
        {
            throw new ServiceException("conversationId is required", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(text))
        {
            throw new ServiceException("text is required", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpConversation conv = wechatMpConversationMapper.selectById(conversationId);
        if (conv == null)
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        assertChannelEnabled(TenantDataSourceSupport.currentTenantKey(), InboxChannel.WECHAT_MP);
        assertHostedForAi(conv.getHostedByAi());
        wechatMpConversationService.claimForAiBot(conversationId, botUserId, botOwnerName);

        WechatMpMessageReplyRequest req = new WechatMpMessageReplyRequest();
        req.setAppId(conv.getAppId());
        req.setOpenId(conv.getOpenId());
        req.setMsgType("text");
        req.setContent(text.trim());
        wechatMpMessageService.replyAsAi(req);

        Map<String, Object> data = new HashMap<>();
        data.put("ok", true);
        data.put("source", SOURCE_AI_INBOX_AGENT);
        data.put("appId", conv.getAppId());
        data.put("openId", conv.getOpenId());
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> sendMessengerText(String tenantId, Long conversationId, String text)
    {
        assertChannelEnabled(tenantId, InboxChannel.MESSENGER);
        if (!StringUtils.hasText(tenantId) || conversationId == null)
        {
            throw new ServiceException("tenantId and conversationId are required", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(text))
        {
            throw new ServiceException("text is required", HttpStatus.BAD_REQUEST);
        }
        CrmMessengerConversation conv = messengerConversationMapper.selectById(conversationId);
        if (conv == null || !tenantId.equals(conv.getTenantId()))
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        assertHostedForAi(conv.getHostedByAi());
        messengerConversationService.claimForAiBot(conversationId, tenantId, botUserId, botOwnerName);

        MessengerSendResult result = messengerMessageService.sendMessage(
                tenantId, conv.getPsid(), conv.getPageId(), "text", text.trim(),
                null, null, null, null, null, true);

        Map<String, Object> data = new HashMap<>();
        data.put("ok", result != null && result.isSuccess());
        data.put("messageId", result != null ? result.getMessageId() : null);
        data.put("error", result != null ? result.getErrorMessage() : null);
        data.put("source", SOURCE_AI_INBOX_AGENT);
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> sendWebchatText(String tenantId, Long conversationId, String text)
    {
        assertChannelEnabled(tenantId, InboxChannel.WEBCHAT);
        if (!StringUtils.hasText(tenantId) || conversationId == null)
        {
            throw new ServiceException("tenantId and conversationId are required", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(text))
        {
            throw new ServiceException("text is required", HttpStatus.BAD_REQUEST);
        }
        CrmWebchatConversation conv = webchatConversationMapper.selectById(conversationId);
        if (conv == null || !tenantId.equals(conv.getTenantId()))
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        assertHostedForAi(conv.getHostedByAi());
        webchatConversationService.claimForAiBot(conversationId, tenantId, botUserId, botOwnerName);

        Map<String, Object> result = webchatMessageService.sendText(
                tenantId, conversationId, text.trim(), null, true);

        Map<String, Object> data = new HashMap<>();
        data.put("ok", result != null && result.get("id") != null);
        data.put("messageId", result != null ? result.get("id") : null);
        data.put("source", SOURCE_AI_INBOX_AGENT);
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> sendWecomKfText(String tenantId, Long conversationId, String text)
    {
        assertChannelEnabled(tenantId, InboxChannel.WECOM_KF);
        if (!StringUtils.hasText(tenantId) || conversationId == null)
        {
            throw new ServiceException("tenantId and conversationId are required", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(text))
        {
            throw new ServiceException("text is required", HttpStatus.BAD_REQUEST);
        }
        CrmWecomKfConversation conv = wecomKfConversationMapper.selectById(conversationId);
        if (conv == null)
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        assertHostedForAi(conv.getHostedByAi());
        wecomKfConversationService.claimForAiBot(conversationId, botUserId, botOwnerName);

        Map<String, Object> result = wecomKfConversationService.sendTextAsAi(conversationId, text.trim());

        Map<String, Object> data = new HashMap<>();
        data.put("ok", result != null && result.get("msgid") != null);
        data.put("messageId", result != null ? result.get("msgid") : null);
        data.put("source", SOURCE_AI_INBOX_AGENT);
        return data;
    }

    private void assertChannelEnabled(String tenantId, InboxChannel channel)
    {
        if (!inboxChannelGateService.isEnabled(tenantId, channel))
        {
            throw new ServiceException("inbox_channel_disabled", HttpStatus.FORBIDDEN);
        }
    }

    private static void assertHostedForAi(Boolean hostedByAi)
    {
        if (Boolean.FALSE.equals(hostedByAi))
        {
            throw new ServiceException("Conversation taken over by human", HttpStatus.CONFLICT);
        }
    }
}
