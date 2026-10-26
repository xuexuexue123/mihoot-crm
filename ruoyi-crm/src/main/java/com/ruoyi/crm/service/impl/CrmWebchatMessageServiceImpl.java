package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.CrmWebchatMessage;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.domain.dto.InboxAgentInboundPayload;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmWebchatConversationMapper;
import com.ruoyi.crm.mapper.CrmWebchatMessageMapper;
import com.ruoyi.crm.service.ICrmWebchatConversationService;
import com.ruoyi.crm.service.ICrmWebchatMessageService;
import com.ruoyi.crm.service.IInboxAgentChannelService;
import com.ruoyi.crm.service.IInboxAgentInboundDispatchService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.mobile.push.InboxMobilePushHook;
import com.ruoyi.crm.webchat.WebchatActivityEventTypes;
import com.ruoyi.crm.webchat.WebchatIdentityResolutionSupport;
import com.ruoyi.crm.webchat.realtime.WebchatInboxEventPublisher;

@Service
public class CrmWebchatMessageServiceImpl implements ICrmWebchatMessageService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWebchatMessageServiceImpl.class);
    private static final int MAX_CONTENT_LEN = 4000;

    @Autowired
    private CrmWebchatConversationMapper conversationMapper;

    @Autowired
    private CrmWebchatMessageMapper messageMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired(required = false)
    private WebchatInboxEventPublisher inboxEventPublisher;

    @Autowired(required = false)
    private ICrmWebchatConversationService conversationService;

    @Autowired(required = false)
    private IInboxAgentInboundDispatchService inboxAgentInboundDispatchService;

    @Autowired(required = false)
    private InboxMobilePushHook inboxMobilePushHook;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> sendText(String tenantId, Long conversationId, String content,
                                        String clientMessageId, boolean asAi)
    {
        if (!StringUtils.hasText(content))
        {
            throw new ServiceException("content is required", HttpStatus.BAD_REQUEST);
        }
        String text = content.trim();
        if (text.length() > MAX_CONTENT_LEN)
        {
            throw new ServiceException("content too long", HttpStatus.BAD_REQUEST);
        }
        if (conversationId == null)
        {
            throw new ServiceException("conversationId is required", HttpStatus.BAD_REQUEST);
        }

        CrmWebchatConversation conv = conversationMapper.selectById(conversationId);
        if (conv == null || !tenantId.equals(conv.getTenantId()))
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        if (!asAi)
        {
            if (conversationService == null)
            {
                throw new ServiceException("Conversation service unavailable", 500);
            }
            conversationService.assertOwnedByCurrentUser(conv);
        }

        if (StringUtils.hasText(clientMessageId))
        {
            CrmWebchatMessage existing = messageMapper.selectByClientMessageId(
                    tenantId, conversationId, clientMessageId.trim());
            if (existing != null)
            {
                return toSendResult(existing);
            }
        }

        Date now = new Date();
        CrmWebchatMessage msg = new CrmWebchatMessage();
        msg.setTenantId(tenantId);
        msg.setConversationId(conversationId);
        msg.setVisitorId(conv.getVisitorId());
        msg.setClientMessageId(StringUtils.hasText(clientMessageId) ? clientMessageId.trim() : null);
        msg.setMessageType("text");
        msg.setContent(text);
        msg.setDirection(1);
        msg.setSenderType(asAi ? "ai" : "agent");
        msg.setStatus("sent");
        if (asAi)
        {
            msg.setSource(IInboxAgentChannelService.SOURCE_AI_INBOX_AGENT);
        }
        else
        {
            try
            {
                msg.setAgentUserId(String.valueOf(SecurityUtils.getUserId()));
            }
            catch (Exception ignored)
            {
            }
        }
        msg.setCreateTime(now);
        messageMapper.insert(msg);

        if (StringUtils.hasText(clientMessageId) && msg.getId() == null)
        {
            CrmWebchatMessage raced = messageMapper.selectByClientMessageId(
                    tenantId, conversationId, clientMessageId.trim());
            if (raced != null)
            {
                return toSendResult(raced);
            }
        }

        CrmWebchatConversation updated = new CrmWebchatConversation();
        updated.setId(conv.getId());
        updated.setLastMessageAt(now);
        updated.setLastMessagePreview(truncate(text, 200));
        updated.setLastOutboundAt(now);
        conversationMapper.updateLastMessage(updated);

        produceOutboundActivity(conv, msg, asAi);

        CrmWebchatConversation preview = conversationMapper.selectById(conv.getId());
        if (inboxEventPublisher != null)
        {
            inboxEventPublisher.publishMessageCreated(tenantId, msg, preview);
        }
        return toSendResult(msg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmWebchatMessage insertInbound(String tenantId, Long conversationId, String visitorId,
                                           String content, String messageType, String clientMessageId)
    {
        if (!StringUtils.hasText(content))
        {
            throw new ServiceException("content is required", HttpStatus.BAD_REQUEST);
        }
        String text = stripControls(content.trim());
        if (text.length() > MAX_CONTENT_LEN)
        {
            throw new ServiceException("content too long", HttpStatus.BAD_REQUEST);
        }
        CrmWebchatConversation conv = conversationMapper.selectById(conversationId);
        if (conv == null || !tenantId.equals(conv.getTenantId()))
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        if (!visitorId.equals(conv.getVisitorId()))
        {
            throw new ServiceException("conversation mismatch", HttpStatus.FORBIDDEN);
        }
        if (!"active".equals(conv.getStatus()))
        {
            throw new ServiceException("conversation is closed", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.hasText(clientMessageId))
        {
            CrmWebchatMessage existing = messageMapper.selectByClientMessageId(
                    tenantId, conversationId, clientMessageId.trim());
            if (existing != null)
            {
                return existing;
            }
        }

        Date now = new Date();
        String type = StringUtils.hasText(messageType) ? messageType.trim().toLowerCase() : "text";
        CrmWebchatMessage msg = new CrmWebchatMessage();
        msg.setTenantId(tenantId);
        msg.setConversationId(conversationId);
        msg.setVisitorId(visitorId);
        msg.setClientMessageId(StringUtils.hasText(clientMessageId) ? clientMessageId.trim() : null);
        msg.setMessageType(type);
        msg.setContent(text);
        msg.setDirection(0);
        msg.setSenderType("visitor");
        msg.setStatus("sent");
        msg.setCreateTime(now);
        messageMapper.insert(msg);

        if (StringUtils.hasText(clientMessageId) && msg.getId() == null)
        {
            CrmWebchatMessage raced = messageMapper.selectByClientMessageId(
                    tenantId, conversationId, clientMessageId.trim());
            if (raced != null)
            {
                return raced;
            }
        }

        int unread = conv.getUnreadCount() != null ? conv.getUnreadCount() + 1 : 1;
        CrmWebchatConversation updated = new CrmWebchatConversation();
        updated.setId(conv.getId());
        updated.setLastMessageAt(now);
        updated.setLastMessagePreview(truncate(text, 200));
        updated.setLastCustomerMessageAt(now);
        updated.setUnreadCount(unread);
        conversationMapper.updateLastMessage(updated);

        produceInboundActivity(conv, msg);

        CrmWebchatConversation preview = conversationMapper.selectById(conv.getId());
        if (inboxChannelGateService.isEnabled(tenantId, InboxChannel.WEBCHAT))
        {
            if (inboxEventPublisher != null)
            {
                inboxEventPublisher.publishMessageCreated(tenantId, msg, preview);
            }

            if (inboxMobilePushHook != null)
            {
                inboxMobilePushHook.scheduleWebchatInbound(tenantId, msg, preview);
            }

            if ("text".equals(type) && msg.getId() != null && inboxAgentInboundDispatchService != null)
            {
                InboxAgentInboundPayload payload = new InboxAgentInboundPayload();
                payload.setChannel(InboxAgentInboundPayload.CHANNEL_WEBCHAT);
                payload.setConversationId(conversationId);
                payload.setInboundMessageId(msg.getId());
                payload.setAccountId(null);
                payload.setPeerId(visitorId);
                payload.setMsgType("text");
                payload.setText(text);
                payload.setKeywordAutoReplied(false);
                inboxAgentInboundDispatchService.scheduleDispatch(tenantId, payload);
            }
        }
        return msg;
    }

    private void produceOutboundActivity(CrmWebchatConversation conv, CrmWebchatMessage msg, boolean asAi)
    {
        if (conv == null || (!StringUtils.hasText(conv.getContactId()) && !StringUtils.hasText(conv.getLeadId())))
        {
            return;
        }
        try
        {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("webchat");
            dto.setChannelEventId("webchat:sent:" + (msg.getId() != null ? msg.getId() : System.currentTimeMillis()));
            if (asAi)
            {
                dto.setEventType(WebchatActivityEventTypes.AI_REPLIED);
                dto.setEventName(WebchatActivityEventTypes.AI_REPLIED_NAME);
                dto.setEventCategory(WebchatActivityEventTypes.AI_REPLIED_CATEGORY);
                dto.setImportance(WebchatActivityEventTypes.AI_REPLIED_IMPORTANCE);
            }
            else
            {
                dto.setEventType(WebchatActivityEventTypes.MESSAGE_SENT);
                dto.setEventName(WebchatActivityEventTypes.MESSAGE_SENT_NAME);
                dto.setEventCategory(WebchatActivityEventTypes.MESSAGE_SENT_CATEGORY);
                dto.setImportance(WebchatActivityEventTypes.MESSAGE_SENT_IMPORTANCE);
            }
            dto.setSource(asAi ? IInboxAgentChannelService.SOURCE_AI_INBOX_AGENT
                    : WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_INBOX);
            dto.setCreateBy("webchat-send");
            dto.setStartedDate(new Date());
            dto.setMessageData(truncate(msg.getContent(), 200));
            fillSubject(dto, conv);
            JSONObject payload = new JSONObject();
            payload.put("message_id", msg.getId());
            payload.put("visitor_id", conv.getVisitorId());
            payload.put("conversation_id", conv.getId());
            payload.put("sender_type", msg.getSenderType());
            dto.setPayload(payload.toJSONString());
            activityEventProducer.produce(dto);
        }
        catch (Exception e)
        {
            log.warn("Failed to produce Webchat send Activity: {}", e.getMessage());
        }
    }

    private void produceInboundActivity(CrmWebchatConversation conv, CrmWebchatMessage msg)
    {
        if (conv == null || (!StringUtils.hasText(conv.getContactId()) && !StringUtils.hasText(conv.getLeadId())))
        {
            return;
        }
        try
        {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("webchat");
            dto.setChannelEventId("webchat:msg:" + (msg.getId() != null ? msg.getId() : System.currentTimeMillis()));
            dto.setEventType(WebchatActivityEventTypes.MESSAGE_RECEIVED);
            dto.setEventName(WebchatActivityEventTypes.MESSAGE_RECEIVED_NAME);
            dto.setEventCategory(WebchatActivityEventTypes.MESSAGE_RECEIVED_CATEGORY);
            dto.setImportance(WebchatActivityEventTypes.MESSAGE_RECEIVED_IMPORTANCE);
            dto.setSource(WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_WIDGET);
            dto.setCreateBy("webchat-widget");
            dto.setStartedDate(new Date());
            dto.setMessageData(truncate(msg.getContent(), 200));
            fillSubject(dto, conv);
            JSONObject payload = new JSONObject();
            payload.put("message_id", msg.getId());
            payload.put("visitor_id", conv.getVisitorId());
            payload.put("conversation_id", conv.getId());
            payload.put("sender_type", "visitor");
            dto.setPayload(payload.toJSONString());
            activityEventProducer.produce(dto);
        }
        catch (Exception e)
        {
            log.warn("Failed to produce Webchat receive Activity: {}", e.getMessage());
        }
    }

    private static void fillSubject(ActivityEventDTO dto, CrmWebchatConversation conv)
    {
        if (StringUtils.hasText(conv.getContactId()))
        {
            dto.setSubjectType("Contact");
            dto.setSubjectId(conv.getContactId());
        }
        else
        {
            dto.setSubjectType("Lead");
            dto.setSubjectId(conv.getLeadId());
        }
    }

    private static Map<String, Object> toSendResult(CrmWebchatMessage msg)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("id", msg.getId());
        data.put("clientMessageId", msg.getClientMessageId());
        data.put("content", msg.getContent());
        data.put("direction", msg.getDirection());
        data.put("senderType", msg.getSenderType());
        data.put("status", msg.getStatus());
        data.put("source", msg.getSource());
        data.put("createdAt", msg.getCreateTime());
        data.put("conversationId", msg.getConversationId());
        return data;
    }

    private static String truncate(String content, int max)
    {
        if (content == null)
        {
            return null;
        }
        return content.length() > max ? content.substring(0, max) : content;
    }

    private static String stripControls(String s)
    {
        if (s == null)
        {
            return null;
        }
        return s.replaceAll("[\\p{Cntrl}&&[^\n\t]]", "");
    }
}
