package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmMessengerConversation;
import com.ruoyi.crm.domain.CrmMessengerMessage;
import com.ruoyi.crm.domain.CrmMessengerWebhookEvent;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.domain.dto.InboxAgentInboundPayload;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.event.MessengerActivityEventTypes;
import com.ruoyi.crm.mapper.CrmMessengerConversationMapper;
import com.ruoyi.crm.mapper.CrmMessengerMessageMapper;
import com.ruoyi.crm.mapper.CrmMessengerWebhookEventMapper;
import com.ruoyi.crm.messenger.MessengerIdentityResolutionSupport;
import com.ruoyi.crm.messenger.realtime.MessengerInboxEventPublisher;
import com.ruoyi.crm.messenger.realtime.MessengerInboxSsePayloads;
import com.ruoyi.crm.service.ICrmMessengerWebhookService;
import com.ruoyi.crm.service.IInboxAgentInboundDispatchService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.mobile.push.InboxMobilePushHook;

@Service
public class CrmMessengerWebhookServiceImpl implements ICrmMessengerWebhookService
{
    private static final Logger log = LoggerFactory.getLogger(CrmMessengerWebhookServiceImpl.class);

    @Autowired
    private CrmMessengerWebhookEventMapper eventMapper;

    @Autowired
    private CrmMessengerConversationMapper conversationMapper;

    @Autowired
    private CrmMessengerMessageMapper messageMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private MessengerIdentityResolutionSupport messengerIdentityResolutionSupport;

    @Autowired
    private MessengerInboxEventPublisher inboxEventPublisher;

    @Autowired
    private IInboxAgentInboundDispatchService inboxAgentInboundDispatchService;

    @Autowired(required = false)
    private InboxMobilePushHook inboxMobilePushHook;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Override
    @Transactional
    public void processWebhook(String tenantId, Map<String, Object> body)
    {
        try
        {
            Object object = body.get("object");
            if (object != null && !"page".equals(String.valueOf(object)))
            {
                log.info("Messenger webhook ignored object={} tenant={}", object, tenantId);
                return;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> entries = (List<Map<String, Object>>) body.get("entry");
            if (entries == null || entries.isEmpty())
            {
                log.warn("Messenger webhook body has no entries for tenant {}", tenantId);
                return;
            }

            for (Map<String, Object> entry : entries)
            {
                String pageId = entry.get("id") != null ? String.valueOf(entry.get("id")) : null;
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> messagingList = (List<Map<String, Object>>) entry.get("messaging");
                if (messagingList == null || messagingList.isEmpty())
                {
                    continue;
                }
                for (Map<String, Object> messaging : messagingList)
                {
                    processMessaging(tenantId, pageId, messaging);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Failed to process Messenger webhook for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Webhook processing failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void processMessaging(String tenantId, String pageId, Map<String, Object> messaging)
    {
        if (messaging.containsKey("message"))
        {
            processMessage(tenantId, pageId, messaging);
        }
        else if (messaging.containsKey("delivery"))
        {
            processDelivery(tenantId, pageId, messaging);
        }
        else if (messaging.containsKey("read"))
        {
            processRead(tenantId, pageId, messaging);
        }
        else if (messaging.containsKey("postback"))
        {
            processPostback(tenantId, pageId, messaging);
        }
        else
        {
            insertIgnoredEvent(tenantId, pageId, messaging, "other");
        }
    }

    @SuppressWarnings("unchecked")
    private void processMessage(String tenantId, String pageId, Map<String, Object> messaging)
    {
        Map<String, Object> message = (Map<String, Object>) messaging.get("message");
        if (message == null)
        {
            return;
        }

        boolean isEcho = Boolean.TRUE.equals(message.get("is_echo"))
                || "true".equalsIgnoreCase(String.valueOf(message.get("is_echo")));
        String mid = metaString(message, "mid", "message_id");
        String text = (String) message.get("text");
        String messageType = resolveInboundMessageType(message);
        String content = text;
        if (!StringUtils.hasText(content) && "postback".equals(messageType))
        {
            content = text;
        }
        if (!StringUtils.hasText(content))
        {
            content = "[" + messageType + "]";
        }

        String psid;
        int direction;
        if (isEcho)
        {
            // Page 发出的 echo：对端为 recipient
            psid = extractId((Map<String, Object>) messaging.get("recipient"));
            direction = 1;
            // 已有同 mid（send API 已落库）则跳过，避免双写
            if (StringUtils.hasText(mid) && messageMapper.selectByMessageId(tenantId, mid) != null)
            {
                log.debug("Skip Messenger echo already stored tenant={} mid={}", tenantId, mid);
                return;
            }
        }
        else
        {
            psid = extractId((Map<String, Object>) messaging.get("sender"));
            direction = 0;
        }

        long sendTime = parseTimestampMillis(messaging.get("timestamp"));
        if (sendTime <= 0)
        {
            sendTime = System.currentTimeMillis();
        }

        if (StringUtils.hasText(mid))
        {
            CrmMessengerWebhookEvent existingEvent = eventMapper.selectByMessageIdAndEventType(
                    tenantId, mid, "message");
            if (existingEvent != null && "processed".equals(existingEvent.getStatus()))
            {
                log.debug("Skip duplicate Messenger message event tenant={} mid={}", tenantId, mid);
                return;
            }
            if (!isEcho && messageMapper.selectByMessageId(tenantId, mid) != null)
            {
                log.debug("Skip duplicate Messenger message row tenant={} mid={}", tenantId, mid);
                return;
            }
        }

        Date now = new Date();
        CrmMessengerWebhookEvent event = new CrmMessengerWebhookEvent();
        event.setTenantId(tenantId);
        event.setPsid(psid);
        event.setPageId(pageId);
        event.setEventType("message");
        event.setMessageId(mid);
        event.setMessageType(messageType);
        event.setContent(content);
        event.setDirection(direction);
        event.setSendTime(sendTime);
        event.setRawJson(JSON.toJSONString(messaging));
        event.setStatus("pending");
        event.setCreateTime(now);
        eventMapper.insert(event);

        try
        {
            CrmMessengerConversation existing = StringUtils.hasText(psid)
                    ? conversationMapper.selectByPsid(tenantId, psid) : null;
            CrmMessengerConversation conv = new CrmMessengerConversation();
            conv.setTenantId(tenantId);
            conv.setPsid(psid);
            conv.setPageId(pageId);
            conv.setLastMessageAt(now);
            conv.setLastMessagePreview(truncate(content, 200));
            if (direction == 0)
            {
                conv.setLastCustomerMessageAt(now);
            }
            else
            {
                conv.setLastOutboundAt(now);
            }
            if (existing != null)
            {
                if (direction == 0)
                {
                    conv.setUnreadCount(existing.getUnreadCount() != null
                            ? existing.getUnreadCount() + 1 : 1);
                }
                else
                {
                    conv.setUnreadCount(existing.getUnreadCount());
                }
                conv.setStatus(StringUtils.hasText(existing.getStatus()) ? existing.getStatus() : "active");
                conv.setCreateTime(existing.getCreateTime() != null ? existing.getCreateTime() : now);
                conv.setProfileName(existing.getProfileName());
            }
            else
            {
                conv.setUnreadCount(direction == 0 ? 1 : 0);
                conv.setStatus("active");
                conv.setCreateTime(now);
            }
            conv.setUpdateTime(now);
            conversationMapper.upsertConversation(conv);

            CrmMessengerConversation saved = conversationMapper.selectByPsid(tenantId, psid);

            CrmMessengerMessage msg = new CrmMessengerMessage();
            msg.setTenantId(tenantId);
            msg.setConversationId(saved != null ? saved.getId() : null);
            msg.setPsid(psid);
            msg.setMessageId(mid);
            msg.setMessageType(messageType);
            msg.setContent(content);
            msg.setDirection(direction);
            msg.setSendTime(sendTime);
            msg.setStatus(direction == 0 ? "delivered" : "sent");
            msg.setCreateTime(now);
            msg.setRawJson(JSON.toJSONString(message));
            attachInboundMedia(msg, message);
            messageMapper.insert(msg);

            if (saved != null)
            {
                resolveAndAttachContact(tenantId, psid, saved);
                event.setCrmContactId(saved.getContactId());
                event.setCrmLeadId(saved.getLeadId());
                if (direction == 0)
                {
                    produceMessageActivity(saved, MessengerActivityEventTypes.MESSAGE_RECEIVED,
                            MessengerActivityEventTypes.MESSAGE_RECEIVED_NAME,
                            MessengerActivityEventTypes.MESSAGE_RECEIVED_IMPORTANCE,
                            MessengerActivityEventTypes.MESSAGE_RECEIVED_CATEGORY,
                            mid, messageType, content, sendTime, "messenger:msg:" + mid);
                }
            }

            CrmMessengerConversation preview = conversationMapper.selectByPsid(tenantId, psid);
            CrmMessengerConversation convForSse = preview != null ? preview : saved;
            if (inboxChannelGateService.isEnabled(tenantId, InboxChannel.MESSENGER))
            {
                inboxEventPublisher.publishMessageCreated(tenantId,
                        MessengerInboxSsePayloads.messageCreated(tenantId, msg, convForSse));

                if (direction == 0 && inboxMobilePushHook != null)
                {
                    inboxMobilePushHook.scheduleMessengerInbound(tenantId, msg, convForSse);
                }

                if (direction == 0 && "text".equals(messageType) && msg.getId() != null
                        && convForSse != null && convForSse.getId() != null)
                {
                    InboxAgentInboundPayload payload = new InboxAgentInboundPayload();
                    payload.setChannel(InboxAgentInboundPayload.CHANNEL_MESSENGER);
                    payload.setConversationId(convForSse.getId());
                    payload.setInboundMessageId(msg.getId());
                    payload.setAccountId(pageId);
                    payload.setPeerId(psid);
                    payload.setMsgType("text");
                    payload.setText(text);
                    payload.setKeywordAutoReplied(false);
                    inboxAgentInboundDispatchService.scheduleDispatch(tenantId, payload);
                }
            }

            eventMapper.updateStatus(event.getId(), "processed", null);
        }
        catch (Exception e)
        {
            log.error("Failed to process Messenger message {} for tenant {}", mid, tenantId, e);
            eventMapper.updateStatus(event.getId(), "failed", truncate(e.getMessage(), 500));
        }
    }

    @SuppressWarnings("unchecked")
    private void processDelivery(String tenantId, String pageId, Map<String, Object> messaging)
    {
        Map<String, Object> delivery = (Map<String, Object>) messaging.get("delivery");
        if (delivery == null)
        {
            return;
        }
        String psid = extractId((Map<String, Object>) messaging.get("sender"));
        if (!StringUtils.hasText(psid))
        {
            psid = extractId((Map<String, Object>) messaging.get("recipient"));
        }
        long sendTime = parseTimestampMillis(messaging.get("timestamp"));
        if (sendTime <= 0)
        {
            sendTime = System.currentTimeMillis();
        }

        List<String> mids = (List<String>) delivery.get("mids");
        Date now = new Date();
        String firstMid = (mids != null && !mids.isEmpty()) ? String.valueOf(mids.get(0)) : null;

        CrmMessengerWebhookEvent event = new CrmMessengerWebhookEvent();
        event.setTenantId(tenantId);
        event.setPsid(psid);
        event.setPageId(pageId);
        event.setEventType("delivery");
        event.setMessageId(firstMid);
        event.setContent("delivered");
        event.setDirection(1);
        event.setSendTime(sendTime);
        event.setRawJson(JSON.toJSONString(messaging));
        event.setStatus("pending");
        event.setCreateTime(now);
        eventMapper.insert(event);

        try
        {
            if (mids != null)
            {
                for (Object midObj : mids)
                {
                    if (midObj == null)
                    {
                        continue;
                    }
                    String mid = String.valueOf(midObj);
                    messageMapper.updateStatusByMessageId(tenantId, mid, "delivered");
                    CrmMessengerMessage existing = messageMapper.selectByMessageId(tenantId, mid);
                    Long conversationId = existing != null ? existing.getConversationId() : null;
                    if (inboxChannelGateService.isEnabled(tenantId, InboxChannel.MESSENGER))
                    {
                        inboxEventPublisher.publishMessageStatus(tenantId,
                                MessengerInboxSsePayloads.messageStatus(tenantId, conversationId, mid,
                                        "delivered", null, null));
                    }
                }
            }

            CrmMessengerConversation conv = StringUtils.hasText(psid)
                    ? conversationMapper.selectByPsid(tenantId, psid) : null;
            if (conv != null)
            {
                resolveAndAttachContact(tenantId, psid, conv);
                event.setCrmContactId(conv.getContactId());
                event.setCrmLeadId(conv.getLeadId());
                produceMessageActivity(conv, MessengerActivityEventTypes.MESSAGE_DELIVERED,
                        MessengerActivityEventTypes.MESSAGE_DELIVERED_NAME,
                        MessengerActivityEventTypes.MESSAGE_DELIVERED_IMPORTANCE,
                        MessengerActivityEventTypes.MESSAGE_DELIVERED_CATEGORY,
                        firstMid, "delivery", "delivered", sendTime,
                        "messenger:delivery:" + firstMid);
            }
            eventMapper.updateStatus(event.getId(), "processed", null);
        }
        catch (Exception e)
        {
            log.error("Failed to process Messenger delivery for tenant {}", tenantId, e);
            eventMapper.updateStatus(event.getId(), "failed", truncate(e.getMessage(), 500));
        }
    }

    @SuppressWarnings("unchecked")
    private void processRead(String tenantId, String pageId, Map<String, Object> messaging)
    {
        Map<String, Object> read = (Map<String, Object>) messaging.get("read");
        if (read == null)
        {
            return;
        }
        // 用户已读 Page 消息：sender=用户 psid
        String psid = extractId((Map<String, Object>) messaging.get("sender"));
        if (!StringUtils.hasText(psid))
        {
            psid = extractId((Map<String, Object>) messaging.get("recipient"));
        }
        long watermark = parseTimestampMillis(read.get("watermark"));
        long sendTime = parseTimestampMillis(messaging.get("timestamp"));
        if (sendTime <= 0)
        {
            sendTime = System.currentTimeMillis();
        }

        Date now = new Date();
        CrmMessengerWebhookEvent event = new CrmMessengerWebhookEvent();
        event.setTenantId(tenantId);
        event.setPsid(psid);
        event.setPageId(pageId);
        event.setEventType("read");
        event.setContent(watermark > 0 ? String.valueOf(watermark) : "read");
        event.setDirection(1);
        event.setSendTime(sendTime);
        event.setRawJson(JSON.toJSONString(messaging));
        event.setStatus("pending");
        event.setCreateTime(now);
        eventMapper.insert(event);

        try
        {
            if (StringUtils.hasText(psid) && watermark > 0)
            {
                // watermark 策略：标记该 psid 下 send_time <= watermark 的出站消息为 read
                messageMapper.updateStatusReadByPsidWatermark(tenantId, psid, watermark);
            }

            CrmMessengerConversation conv = StringUtils.hasText(psid)
                    ? conversationMapper.selectByPsid(tenantId, psid) : null;
            if (conv != null)
            {
                resolveAndAttachContact(tenantId, psid, conv);
                event.setCrmContactId(conv.getContactId());
                event.setCrmLeadId(conv.getLeadId());
                produceMessageActivity(conv, MessengerActivityEventTypes.MESSAGE_READ,
                        MessengerActivityEventTypes.MESSAGE_READ_NAME,
                        MessengerActivityEventTypes.MESSAGE_READ_IMPORTANCE,
                        MessengerActivityEventTypes.MESSAGE_READ_CATEGORY,
                        null, "read", "read", sendTime,
                        "messenger:read:" + psid + ":" + watermark);
                if (inboxChannelGateService.isEnabled(tenantId, InboxChannel.MESSENGER))
                {
                    inboxEventPublisher.publishMessageStatus(tenantId,
                            MessengerInboxSsePayloads.messageStatus(tenantId, conv.getId(),
                                    watermark > 0 ? String.valueOf(watermark) : null,
                                    "read", null, null));
                }
            }
            eventMapper.updateStatus(event.getId(), "processed", null);
        }
        catch (Exception e)
        {
            log.error("Failed to process Messenger read for tenant {}", tenantId, e);
            eventMapper.updateStatus(event.getId(), "failed", truncate(e.getMessage(), 500));
        }
    }

    @SuppressWarnings("unchecked")
    private void processPostback(String tenantId, String pageId, Map<String, Object> messaging)
    {
        Map<String, Object> postback = (Map<String, Object>) messaging.get("postback");
        if (postback == null)
        {
            return;
        }
        String psid = extractId((Map<String, Object>) messaging.get("sender"));
        String payload = (String) postback.get("payload");
        String title = (String) postback.get("title");
        String content = StringUtils.hasText(payload) ? payload : title;
        long sendTime = parseTimestampMillis(messaging.get("timestamp"));
        if (sendTime <= 0)
        {
            sendTime = System.currentTimeMillis();
        }

        Date now = new Date();
        CrmMessengerWebhookEvent event = new CrmMessengerWebhookEvent();
        event.setTenantId(tenantId);
        event.setPsid(psid);
        event.setPageId(pageId);
        event.setEventType("postback");
        event.setMessageType("postback");
        event.setContent(content);
        event.setDirection(0);
        event.setSendTime(sendTime);
        event.setRawJson(JSON.toJSONString(messaging));
        event.setStatus("pending");
        event.setCreateTime(now);
        eventMapper.insert(event);

        try
        {
            if (StringUtils.hasText(psid))
            {
                CrmMessengerConversation existing = conversationMapper.selectByPsid(tenantId, psid);
                CrmMessengerConversation conv = new CrmMessengerConversation();
                conv.setTenantId(tenantId);
                conv.setPsid(psid);
                conv.setPageId(pageId);
                conv.setLastMessageAt(now);
                conv.setLastMessagePreview(truncate(content, 200));
                conv.setLastCustomerMessageAt(now);
                if (existing != null)
                {
                    conv.setUnreadCount(existing.getUnreadCount() != null
                            ? existing.getUnreadCount() + 1 : 1);
                    conv.setStatus(StringUtils.hasText(existing.getStatus()) ? existing.getStatus() : "active");
                    conv.setCreateTime(existing.getCreateTime() != null ? existing.getCreateTime() : now);
                }
                else
                {
                    conv.setUnreadCount(1);
                    conv.setStatus("active");
                    conv.setCreateTime(now);
                }
                conv.setUpdateTime(now);
                conversationMapper.upsertConversation(conv);

                CrmMessengerConversation saved = conversationMapper.selectByPsid(tenantId, psid);
                if (saved != null)
                {
                    CrmMessengerMessage msg = new CrmMessengerMessage();
                    msg.setTenantId(tenantId);
                    msg.setConversationId(saved.getId());
                    msg.setPsid(psid);
                    msg.setMessageType("postback");
                    msg.setContent(content);
                    msg.setDirection(0);
                    msg.setSendTime(sendTime);
                    msg.setStatus("delivered");
                    msg.setCreateTime(now);
                    msg.setRawJson(JSON.toJSONString(postback));
                    messageMapper.insert(msg);

                    resolveAndAttachContact(tenantId, psid, saved);
                    event.setCrmContactId(saved.getContactId());
                    event.setCrmLeadId(saved.getLeadId());

                    CrmMessengerConversation preview = conversationMapper.selectByPsid(tenantId, psid);
                    CrmMessengerConversation convForSse = preview != null ? preview : saved;
                    if (inboxChannelGateService.isEnabled(tenantId, InboxChannel.MESSENGER))
                    {
                        inboxEventPublisher.publishMessageCreated(tenantId,
                                MessengerInboxSsePayloads.messageCreated(tenantId, msg, convForSse));

                        if (inboxMobilePushHook != null)
                        {
                            inboxMobilePushHook.scheduleMessengerInbound(tenantId, msg, convForSse);
                        }
                    }
                }
            }
            eventMapper.updateStatus(event.getId(), "processed", null);
        }
        catch (Exception e)
        {
            log.error("Failed to process Messenger postback for tenant {}", tenantId, e);
            eventMapper.updateStatus(event.getId(), "failed", truncate(e.getMessage(), 500));
        }
    }

    private void insertIgnoredEvent(String tenantId, String pageId, Map<String, Object> messaging,
                                    String eventType)
    {
        Date now = new Date();
        CrmMessengerWebhookEvent event = new CrmMessengerWebhookEvent();
        event.setTenantId(tenantId);
        event.setPageId(pageId);
        event.setEventType(eventType);
        event.setRawJson(JSON.toJSONString(messaging));
        event.setStatus("ignored");
        event.setCreateTime(now);
        eventMapper.insert(event);
    }

    private void resolveAndAttachContact(String tenantId, String psid, CrmMessengerConversation conv)
    {
        if (conv == null || !StringUtils.hasText(psid))
        {
            return;
        }
        if (StringUtils.hasText(conv.getContactId()))
        {
            return;
        }
        try
        {
            String contactId = messengerIdentityResolutionSupport.resolveContactId(psid);
            if (!StringUtils.hasText(contactId))
            {
                return;
            }
            conversationMapper.updateContactId(tenantId, psid, contactId);
            conv.setContactId(contactId);
        }
        catch (Exception e)
        {
            log.warn("Messenger contact resolve failed tenant={} psid={}: {}",
                    tenantId, psid, e.getMessage());
        }
    }

    private void produceMessageActivity(CrmMessengerConversation conv, String eventType,
                                        String eventName, int importance, String category,
                                        String messageId, String msgType, String content,
                                        long sendTimeMillis, String channelEventId)
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
            dto.setChannelEventId(channelEventId);
            dto.setEventType(eventType);
            dto.setEventName(eventName);
            dto.setEventCategory(category);
            dto.setImportance(importance);
            dto.setSource(MessengerIdentityResolutionSupport.SOURCE_MESSENGER_WEBHOOK);
            dto.setCreateBy("messenger-webhook");
            dto.setStartedDate(new Date(sendTimeMillis));
            dto.setMessageData(StringUtils.hasText(content) ? truncate(content, 200) : eventName);

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
            payload.put("msg_type", msgType);
            payload.put("psid", conv.getPsid());
            payload.put("conversation_id", conv.getId());
            if (StringUtils.hasText(content))
            {
                payload.put("content_preview", truncate(content, 200));
            }
            dto.setPayload(payload.toJSONString());

            activityEventProducer.produce(dto);
        }
        catch (Exception e)
        {
            log.warn("Failed to produce Messenger Activity eventType={} messageId={}: {}",
                    eventType, messageId, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static String resolveInboundMessageType(Map<String, Object> message)
    {
        if (message.get("text") != null)
        {
            return "text";
        }
        if (message.get("sticker_id") != null)
        {
            return "sticker";
        }
        List<Map<String, Object>> attachments = (List<Map<String, Object>>) message.get("attachments");
        if (attachments != null && !attachments.isEmpty())
        {
            Object type = attachments.get(0).get("type");
            if (type != null)
            {
                return String.valueOf(type);
            }
            return "attachment";
        }
        if (message.get("quick_reply") != null)
        {
            return "quick_reply";
        }
        return "other";
    }

    @SuppressWarnings("unchecked")
    private static void attachInboundMedia(CrmMessengerMessage msg, Map<String, Object> message)
    {
        List<Map<String, Object>> attachments = (List<Map<String, Object>>) message.get("attachments");
        if (attachments == null || attachments.isEmpty())
        {
            return;
        }
        Map<String, Object> att = attachments.get(0);
        String attType = att.get("type") != null ? String.valueOf(att.get("type")).toLowerCase() : null;
        Map<String, Object> payload = (Map<String, Object>) att.get("payload");
        if (payload != null)
        {
            Object url = payload.get("url");
            if (url != null)
            {
                msg.setMediaUrl(String.valueOf(url));
            }
            Object mime = payload.get("mime_type");
            if (mime == null)
            {
                mime = payload.get("mimeType");
            }
            if (mime != null && StringUtils.hasText(String.valueOf(mime)))
            {
                msg.setMediaMimeType(String.valueOf(mime));
            }
            Object name = payload.get("name");
            if (name == null)
            {
                name = payload.get("filename");
            }
            if (name == null)
            {
                name = payload.get("title");
            }
            if (name != null && StringUtils.hasText(String.valueOf(name)))
            {
                msg.setFilename(String.valueOf(name));
            }
        }
        if (!StringUtils.hasText(msg.getMediaMimeType()))
        {
            msg.setMediaMimeType(inferMimeFromAttachmentType(attType));
        }
    }

    private static String inferMimeFromAttachmentType(String attType)
    {
        if (attType == null)
        {
            return null;
        }
        switch (attType)
        {
            case "image":
            case "sticker":
                return "image/jpeg";
            case "audio":
                return "audio/mpeg";
            case "video":
                return "video/mp4";
            case "file":
                return "application/octet-stream";
            default:
                return null;
        }
    }

    private static String extractId(Map<String, Object> node)
    {
        if (node == null || node.get("id") == null)
        {
            return null;
        }
        return String.valueOf(node.get("id"));
    }

    private static String metaString(Map<String, Object> map, String... keys)
    {
        if (map == null)
        {
            return null;
        }
        for (String key : keys)
        {
            Object v = map.get(key);
            if (v != null && StringUtils.hasText(String.valueOf(v)))
            {
                return String.valueOf(v);
            }
        }
        return null;
    }

    private static long parseTimestampMillis(Object raw)
    {
        if (raw == null)
        {
            return 0L;
        }
        try
        {
            long v = Long.parseLong(String.valueOf(raw));
            // Meta Messenger timestamp 多为毫秒；若像秒级则放大
            if (v > 0 && v < 1_000_000_000_000L)
            {
                return v * 1000L;
            }
            return v;
        }
        catch (NumberFormatException e)
        {
            return 0L;
        }
    }

    private static String truncate(String s, int max)
    {
        if (s == null)
        {
            return null;
        }
        return s.length() > max ? s.substring(0, max) : s;
    }
}
