package com.ruoyi.crm.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmMessengerConfig;
import com.ruoyi.crm.domain.CrmMessengerConversation;
import com.ruoyi.crm.domain.CrmMessengerMessage;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.domain.dto.messenger.MessengerSendResult;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.event.MessengerActivityEventTypes;
import com.ruoyi.crm.mapper.CrmMessengerConfigMapper;
import com.ruoyi.crm.mapper.CrmMessengerConversationMapper;
import com.ruoyi.crm.mapper.CrmMessengerMessageMapper;
import com.ruoyi.crm.messenger.MessengerClient;
import com.ruoyi.crm.messenger.MessengerIdentityResolutionSupport;
import com.ruoyi.crm.messenger.realtime.MessengerInboxEventPublisher;
import com.ruoyi.crm.messenger.realtime.MessengerInboxSsePayloads;
import com.ruoyi.crm.service.ICrmMessengerConversationService;
import com.ruoyi.crm.service.ICrmMessengerMessageService;
import com.ruoyi.crm.service.IInboxAgentChannelService;
import com.ruoyi.crm.service.support.TokenEncryptor;

@Service
public class CrmMessengerMessageServiceImpl implements ICrmMessengerMessageService
{
    private static final Logger log = LoggerFactory.getLogger(CrmMessengerMessageServiceImpl.class);

    private static final long MESSAGING_WINDOW_MS = 86400_000L;
    private static final Set<String> ALLOWED_TAGS = new HashSet<>(Arrays.asList("HUMAN_AGENT"));

    @Autowired
    private CrmMessengerConfigMapper configMapper;

    @Autowired
    private CrmMessengerConversationMapper conversationMapper;

    @Autowired
    private CrmMessengerMessageMapper messageMapper;

    @Autowired
    private MessengerClient messengerClient;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired(required = false)
    private MessengerInboxEventPublisher inboxEventPublisher;

    @Autowired(required = false)
    private ICrmMessengerConversationService conversationService;

    @Override
    public MessengerSendResult sendMessage(String tenantId, String psid, String pageId,
                                           String messageType, String content, String mediaUrl,
                                           String filename, String caption,
                                           String messagingType, String tag, boolean asAi)
    {
        if (!StringUtils.hasText(psid))
        {
            throw new ServiceException("psid is required", 400);
        }
        String type = StringUtils.hasText(messageType) ? messageType.trim().toLowerCase() : "text";
        if (!"text".equals(type) && !"image".equals(type) && !"file".equals(type))
        {
            throw new ServiceException("Unsupported message type: " + type, 400);
        }
        if ("text".equals(type) && !StringUtils.hasText(content))
        {
            throw new ServiceException("content is required", 400);
        }
        if (("image".equals(type) || "file".equals(type)) && !StringUtils.hasText(mediaUrl))
        {
            throw new ServiceException("mediaUrl is required for " + type, 400);
        }

        CrmMessengerConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null || !StringUtils.hasText(config.getAccessTokenEnc()))
        {
            throw new ServiceException("Messenger configuration not found", 400);
        }
        String accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());
        String actualPageId = StringUtils.hasText(pageId) ? pageId : config.getPageId();
        if (!StringUtils.hasText(actualPageId))
        {
            throw new ServiceException("pageId is required", 400);
        }

        CrmMessengerConversation conv = conversationMapper.selectByPsid(tenantId, psid);
        if (!asAi)
        {
            if (conversationService == null)
            {
                throw new ServiceException("Conversation service unavailable", 500);
            }
            if (conv == null)
            {
                throw new ServiceException("会话不存在，请先从公共池领取", HttpStatus.FORBIDDEN);
            }
            conversationService.assertOwnedByCurrentUser(conv);
        }

        String resolvedMessagingType = resolveMessagingType(messagingType, tag, conv);
        String resolvedTag = "MESSAGE_TAG".equals(resolvedMessagingType) ? tag.trim() : null;
        assertMessagingWindowAllows(conv, resolvedMessagingType, resolvedTag);

        MessengerSendResult result;
        if ("text".equals(type))
        {
            result = messengerClient.sendTextMessage(
                    actualPageId, psid, content, resolvedMessagingType, resolvedTag, accessToken);
        }
        else
        {
            String attachmentType = "image".equals(type) ? "image" : "file";
            result = messengerClient.sendAttachmentMessage(
                    actualPageId, psid, attachmentType, mediaUrl, filename, caption,
                    resolvedMessagingType, resolvedTag, accessToken);
        }

        if (!result.isSuccess())
        {
            log.error("Messenger sendMessage failed tenant={} psid={} error={}",
                    tenantId, psid, result.getErrorMessage());
            return result;
        }

        Date now = new Date();
        String preview = previewFor(type, content, filename, caption);
        if (conv == null)
        {
            CrmMessengerConversation created = new CrmMessengerConversation();
            created.setTenantId(tenantId);
            created.setPsid(psid);
            created.setPageId(actualPageId);
            created.setLastMessageAt(now);
            created.setLastMessagePreview(truncate(preview, 200));
            created.setLastOutboundAt(now);
            created.setUnreadCount(0);
            created.setStatus("active");
            created.setCreateTime(now);
            created.setUpdateTime(now);
            conversationMapper.upsertConversation(created);
            conv = conversationMapper.selectByPsid(tenantId, psid);
        }

        if (conv != null)
        {
            CrmMessengerMessage msg = new CrmMessengerMessage();
            msg.setTenantId(tenantId);
            msg.setConversationId(conv.getId());
            msg.setPsid(psid);
            msg.setMessageId(result.getMessageId());
            msg.setMessageType(type);
            msg.setContent(StringUtils.hasText(content) ? content : caption);
            msg.setDirection(1);
            msg.setSendTime(System.currentTimeMillis());
            msg.setStatus("sent");
            msg.setMediaUrl(mediaUrl);
            msg.setFilename(filename);
            if ("image".equals(type))
            {
                msg.setMediaMimeType("image/jpeg");
            }
            else if ("file".equals(type))
            {
                msg.setMediaMimeType("application/octet-stream");
            }
            if (asAi)
            {
                msg.setSource(IInboxAgentChannelService.SOURCE_AI_INBOX_AGENT);
            }
            msg.setCreateTime(now);
            messageMapper.insert(msg);

            CrmMessengerConversation updated = new CrmMessengerConversation();
            updated.setId(conv.getId());
            updated.setLastMessageAt(now);
            updated.setLastMessagePreview(truncate(preview, 200));
            updated.setLastOutboundAt(now);
            conversationMapper.updateLastMessage(updated);

            produceSentActivity(conv, result.getMessageId(), preview);

            if (inboxEventPublisher != null)
            {
                CrmMessengerConversation previewConv = conversationMapper.selectById(conv.getId());
                inboxEventPublisher.publishMessageCreated(tenantId,
                        MessengerInboxSsePayloads.messageCreated(tenantId, msg, previewConv));
            }
        }

        return result;
    }

    private static String resolveMessagingType(String messagingType, String tag, CrmMessengerConversation conv)
    {
        boolean outsideWindow = isOutsideWindow(conv);
        if (outsideWindow)
        {
            if (!"MESSAGE_TAG".equals(messagingType) || !StringUtils.hasText(tag))
            {
                throw new ServiceException("Outside 24h window requires messagingType=MESSAGE_TAG and tag", 400);
            }
            String t = tag.trim();
            if (!ALLOWED_TAGS.contains(t))
            {
                throw new ServiceException("Unsupported message tag: " + t, 400);
            }
            return "MESSAGE_TAG";
        }
        if ("MESSAGE_TAG".equals(messagingType))
        {
            if (!StringUtils.hasText(tag) || !ALLOWED_TAGS.contains(tag.trim()))
            {
                throw new ServiceException("tag must be HUMAN_AGENT when messagingType=MESSAGE_TAG", 400);
            }
            return "MESSAGE_TAG";
        }
        return StringUtils.hasText(messagingType) ? messagingType : "RESPONSE";
    }

    private static boolean isOutsideWindow(CrmMessengerConversation conv)
    {
        if (conv == null || conv.getLastCustomerMessageAt() == null)
        {
            return false;
        }
        long remainingSec = (conv.getLastCustomerMessageAt().getTime() + MESSAGING_WINDOW_MS
                - System.currentTimeMillis()) / 1000;
        return remainingSec <= 0;
    }

    private static void assertMessagingWindowAllows(CrmMessengerConversation conv,
                                                    String messagingType, String tag)
    {
        if (!isOutsideWindow(conv))
        {
            return;
        }
        if ("MESSAGE_TAG".equals(messagingType) && StringUtils.hasText(tag))
        {
            return;
        }
        throw new ServiceException("Messaging window expired (24h)", 400);
    }

    private static String previewFor(String type, String content, String filename, String caption)
    {
        if (StringUtils.hasText(content))
        {
            return content;
        }
        if (StringUtils.hasText(caption))
        {
            return caption;
        }
        if ("image".equals(type))
        {
            return "[image]";
        }
        if ("file".equals(type))
        {
            return StringUtils.hasText(filename) ? "[file] " + filename : "[file]";
        }
        return "";
    }

    private void produceSentActivity(CrmMessengerConversation conv, String messageId, String content)
    {
        if (conv == null)
        {
            return;
        }
        boolean hasContact = StringUtils.hasText(conv.getContactId());
        boolean hasLead = StringUtils.hasText(conv.getLeadId());
        if (!hasContact && !hasLead)
        {
            return;
        }
        try
        {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("messenger");
            dto.setChannelEventId("messenger:sent:" + messageId);
            dto.setEventType(MessengerActivityEventTypes.MESSAGE_SENT);
            dto.setEventName(MessengerActivityEventTypes.MESSAGE_SENT_NAME);
            dto.setEventCategory(MessengerActivityEventTypes.MESSAGE_SENT_CATEGORY);
            dto.setImportance(MessengerActivityEventTypes.MESSAGE_SENT_IMPORTANCE);
            dto.setSource(MessengerIdentityResolutionSupport.SOURCE_MESSENGER_SIDEBAR);
            dto.setCreateBy("messenger-send");
            dto.setStartedDate(new Date());
            dto.setMessageData(truncate(content, 200));
            if (hasContact)
            {
                dto.setSubjectType("Contact");
                dto.setSubjectId(conv.getContactId());
            }
            else
            {
                dto.setSubjectType("Lead");
                dto.setSubjectId(conv.getLeadId());
            }
            JSONObject payload = new JSONObject();
            payload.put("message_id", messageId);
            payload.put("psid", conv.getPsid());
            payload.put("conversation_id", conv.getId());
            dto.setPayload(payload.toJSONString());
            activityEventProducer.produce(dto);
        }
        catch (Exception e)
        {
            log.warn("Failed to produce Messenger send Activity: {}", e.getMessage());
        }
    }

    private static String truncate(String content, int max)
    {
        if (content == null)
        {
            return null;
        }
        return content.length() > max ? content.substring(0, max) : content;
    }
}
