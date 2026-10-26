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
import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.domain.CrmWhatsappWebhookEvent;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.domain.dto.InboxAgentInboundPayload;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.event.WhatsappActivityEventTypes;
import com.ruoyi.crm.mapper.CrmWhatsappConversationMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateSendItemMapper;
import com.ruoyi.crm.mapper.CrmWhatsappWebhookEventMapper;
import com.ruoyi.crm.service.ICrmWhatsappWebhookService;
import com.ruoyi.crm.service.IInboxAgentInboundDispatchService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.mobile.push.InboxMobilePushHook;
import com.ruoyi.crm.whatsapp.WhatsAppTemplateStatuses;
import com.ruoyi.crm.whatsapp.WhatsappIdentityResolutionSupport;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxEventPublisher;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxSsePayloads;

@Service
public class CrmWhatsappWebhookServiceImpl implements ICrmWhatsappWebhookService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappWebhookServiceImpl.class);

    @Autowired
    private CrmWhatsappWebhookEventMapper eventMapper;

    @Autowired
    private CrmWhatsappConversationMapper conversationMapper;

    @Autowired
    private CrmWhatsappMessageMapper messageMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private WhatsappIdentityResolutionSupport whatsappIdentityResolutionSupport;

    @Autowired
    private WhatsAppInboxEventPublisher inboxEventPublisher;

    @Autowired
    private CrmWhatsappInboundMediaPersistService inboundMediaPersistService;

    @Autowired
    private IInboxAgentInboundDispatchService inboxAgentInboundDispatchService;

    @Autowired
    private CrmWhatsappTemplateMapper templateMapper;

    @Autowired
    private CrmWhatsappTemplateSendItemMapper sendItemMapper;

    @Autowired(required = false)
    private InboxMobilePushHook inboxMobilePushHook;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Autowired(required = false)
    private com.ruoyi.crm.service.support.ContactServiceSummaryHooks contactServiceSummaryHooks;

    @Override
    @Transactional
    public void processWebhook(String tenantId, Map<String, Object> body)
    {
        try
        {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> entries = (List<Map<String, Object>>) body.get("entry");
            if (entries == null || entries.isEmpty())
            {
                log.warn("Webhook body has no entries for tenant {}", tenantId);
                return;
            }

            for (Map<String, Object> entry : entries)
            {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");
                if (changes == null)
                {
                    continue;
                }

                for (Map<String, Object> change : changes)
                {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> value = (Map<String, Object>) change.get("value");
                    if (value == null)
                    {
                        continue;
                    }

                    String field = (String) change.get("field");
                    processValue(tenantId, value, field);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Failed to process webhook for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Webhook processing failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void processValue(String tenantId, Map<String, Object> value, String field)
    {
        if ("message_template_status_update".equals(field))
        {
            processTemplateStatusUpdate(tenantId, value);
            return;
        }

        Map<String, Object> metadata = (Map<String, Object>) value.get("metadata");
        String phoneNumberId = metaString(metadata, "phone_number_id", "phoneNumberId");
        String displayPhoneNumber = metaString(metadata, "display_phone_number", "displayPhoneNumber");

        List<Map<String, Object>> contacts = (List<Map<String, Object>>) value.get("contacts");
        String waId = null;
        String profileName = null;
        if (contacts != null && !contacts.isEmpty())
        {
            Map<String, Object> contact = contacts.get(0);
            waId = metaString(contact, "wa_id", "waId");
            Map<String, Object> profile = (Map<String, Object>) contact.get("profile");
            if (profile != null)
            {
                profileName = (String) profile.get("name");
            }
        }

        List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
        if (messages != null)
        {
            for (Map<String, Object> message : messages)
            {
                processMessage(tenantId, message, waId, phoneNumberId,
                        displayPhoneNumber, profileName, field, value);
            }
        }

        List<Map<String, Object>> statuses = (List<Map<String, Object>>) value.get("statuses");
        if (statuses != null)
        {
            for (Map<String, Object> status : statuses)
            {
                processStatus(tenantId, status, waId, phoneNumberId, displayPhoneNumber, value);
            }
        }
    }

    /**
     * Meta template lifecycle：APPROVED / REJECTED / PAUSED / DISABLED / REINSTATED 等。
     */
    @SuppressWarnings("unchecked")
    private void processTemplateStatusUpdate(String tenantId, Map<String, Object> value)
    {
        String event = metaString(value, "event", "Event");
        String templateName = metaString(value, "message_template_name", "messageTemplateName");
        String language = metaString(value, "message_template_language", "messageTemplateLanguage");
        String reason = metaString(value, "reason", "Reason");
        if (!StringUtils.hasText(templateName) || !StringUtils.hasText(event))
        {
            log.warn("Ignore template status update missing name/event tenant={} value={}",
                    tenantId, JSON.toJSONString(value));
            return;
        }

        String normalized = mapTemplateLifecycleEvent(event);
        if (normalized == null)
        {
            log.info("Ignore unknown template lifecycle event={} tenant={} template={}",
                    event, tenantId, templateName);
            return;
        }

        if ("rejected".equals(normalized) && !StringUtils.hasText(reason))
        {
            Map<String, Object> rejectionInfo = (Map<String, Object>) value.get("rejection_info");
            if (rejectionInfo != null)
            {
                reason = metaString(rejectionInfo, "reason", "Reason");
            }
        }

        int updated = templateMapper.updateStatusByNameLanguage(
                tenantId, templateName, language, normalized, reason);
        log.info("Template status update tenant={} template={} lang={} event={} -> {} rows={}",
                tenantId, templateName, language, event, normalized, updated);
    }

    /**
     * Meta event → 本地 crm_whatsapp_template.status（小写）。
     * REINSTATED 视为可用（approved）。
     */
    private static String mapTemplateLifecycleEvent(String event)
    {
        if (event == null)
        {
            return null;
        }
        String e = event.trim().toUpperCase();
        switch (e)
        {
            case "APPROVED":
            case "REINSTATED":
                return "approved";
            case "REJECTED":
                return "rejected";
            case "PAUSED":
                return "paused";
            case "DISABLED":
                return "disabled";
            case "PENDING":
            case "IN_APPEAL":
                return "pending";
            case "FLAGGED":
                return "flagged";
            case "DELETED":
            case "ARCHIVED":
                return WhatsAppTemplateStatuses.normalize(e);
            default:
                return WhatsAppTemplateStatuses.normalize(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void processMessage(String tenantId, Map<String, Object> message, String waId,
                                String phoneNumberId, String displayPhoneNumber,
                                String profileName, String field, Map<String, Object> value)
    {
        String messageId = (String) message.get("id");
        String from = (String) message.get("from");
        if (from != null)
        {
            waId = from;
        }
        String type = (String) message.get("type");
        long sendTime = parseTimestampMillis(message.get("timestamp"));
        if (sendTime <= 0)
        {
            sendTime = System.currentTimeMillis();
        }

        String content = null;
        Map<String, Object> typeBody = type != null ? (Map<String, Object>) message.get(type) : null;
        if (typeBody != null)
        {
            if ("text".equals(type))
            {
                content = (String) typeBody.get("body");
            }
            else if ("interactive".equals(type))
            {
                Map<String, Object> interactiveType = (Map<String, Object>) typeBody.get("button_reply");
                if (interactiveType == null)
                {
                    interactiveType = (Map<String, Object>) typeBody.get("list_reply");
                }
                if (interactiveType != null)
                {
                    content = (String) interactiveType.get("title");
                }
            }
            else if ("button".equals(type))
            {
                content = (String) typeBody.get("text");
            }
            else
            {
                content = (String) typeBody.get("caption");
                // 文档无 caption 时用文件名作预览文案
                if (!StringUtils.hasText(content) && "document".equals(type))
                {
                    content = (String) typeBody.get("filename");
                }
            }
        }

        int direction = 0;
        Date now = new Date();
        String eventType = field != null ? field : "messages";

        // 幂等：同一 message_id 的 messages 事件不重复处理
        if (StringUtils.hasText(messageId))
        {
            CrmWhatsappWebhookEvent existingEvent = eventMapper.selectByMessageIdAndEventType(
                    tenantId, messageId, eventType);
            if (existingEvent != null && "processed".equals(existingEvent.getStatus()))
            {
                log.debug("Skip duplicate WhatsApp message event tenant={} messageId={}", tenantId, messageId);
                return;
            }
            CrmWhatsappMessage existingMsg = messageMapper.selectByMessageId(tenantId, messageId);
            if (existingMsg != null)
            {
                log.debug("Skip duplicate WhatsApp message row tenant={} messageId={}", tenantId, messageId);
                return;
            }
        }

        CrmWhatsappWebhookEvent event = new CrmWhatsappWebhookEvent();
        event.setTenantId(tenantId);
        event.setWaId(waId);
        event.setPhoneNumber(displayPhoneNumber);
        event.setPhoneNumberId(phoneNumberId);
        event.setEventType(eventType);
        event.setMessageId(messageId);
        event.setMessageType(type);
        event.setContent(content);
        event.setDirection(direction);
        event.setSendTime(sendTime);
        event.setRawJson(JSON.toJSONString(value));
        event.setStatus("pending");
        event.setCreateTime(now);
        eventMapper.insert(event);

        try
        {
            CrmWhatsappConversation existing = conversationMapper.selectByWaId(tenantId, waId);
            CrmWhatsappConversation conv = new CrmWhatsappConversation();
            conv.setTenantId(tenantId);
            conv.setWaId(waId);
            conv.setPhoneNumber(displayPhoneNumber);
            conv.setPhoneNumberId(phoneNumberId);
            conv.setLastMessageAt(now);
            conv.setLastMessagePreview(content != null && content.length() > 200
                    ? content.substring(0, 200) : content);
            conv.setLastCustomerMessageAt(now);
            if (existing != null)
            {
                conv.setUnreadCount(existing.getUnreadCount() != null
                        ? existing.getUnreadCount() + 1 : 1);
                // PG 在 ON CONFLICT 前仍校验 INSERT 行 NOT NULL；status 必须有值
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

            CrmWhatsappConversation saved = conversationMapper.selectByWaId(tenantId, waId);

            CrmWhatsappMessage msg = new CrmWhatsappMessage();
            msg.setTenantId(tenantId);
            msg.setConversationId(saved != null ? saved.getId() : null);
            msg.setWaId(waId);
            msg.setMessageId(messageId);
            msg.setMessageType(type);
            msg.setContent(content);
            msg.setDirection(direction);
            msg.setSendTime(sendTime);
            msg.setStatus("delivered");
            msg.setCreateTime(now);
            msg.setRawJson(JSON.toJSONString(message));

            if (typeBody != null)
            {
                String mediaId = (String) typeBody.get("id");
                if (mediaId != null)
                {
                    msg.setMediaId(mediaId);
                }
                String mimeType = metaString(typeBody, "mime_type", "mimeType");
                if (mimeType != null)
                {
                    msg.setMediaMimeType(mimeType);
                }
                // document / 部分媒体类型带 filename
                String filename = (String) typeBody.get("filename");
                if (StringUtils.hasText(filename))
                {
                    msg.setFilename(filename.trim());
                }
            }

            messageMapper.insert(msg);

            if (StringUtils.hasText(msg.getMediaId())
                    && CrmWhatsappInboundMediaPersistService.isPersistableMediaType(type)
                    && msg.getId() != null)
            {
                inboundMediaPersistService.schedulePersistAfterCommit(
                        tenantId, msg.getId(), msg.getMediaId(),
                        msg.getFilename(), msg.getMediaMimeType());
            }

            if (saved != null)
            {
                resolveAndAttachContact(tenantId, waId, saved);
                if (contactServiceSummaryHooks != null && StringUtils.hasText(saved.getContactId()))
                {
                    contactServiceSummaryHooks.onMessageForContact(tenantId, saved.getContactId());
                }
                event.setCrmContactId(saved.getContactId());
                event.setCrmLeadId(saved.getLeadId());
                produceMessageActivity(saved, WhatsappActivityEventTypes.MESSAGE_RECEIVED,
                        WhatsappActivityEventTypes.MESSAGE_RECEIVED_NAME,
                        WhatsappActivityEventTypes.MESSAGE_RECEIVED_IMPORTANCE,
                        WhatsappActivityEventTypes.MESSAGE_RECEIVED_CATEGORY,
                        messageId, type, content, sendTime, "whatsapp:msg:" + messageId);

                // Re-read conversation for accurate unread/preview after upsert
                CrmWhatsappConversation preview = conversationMapper.selectByWaId(tenantId, waId);
                CrmWhatsappConversation convForSse = preview != null ? preview : saved;
                if (inboxChannelGateService.isEnabled(tenantId, InboxChannel.WHATSAPP))
                {
                    inboxEventPublisher.publishMessageCreated(tenantId,
                            WhatsAppInboxSsePayloads.messageCreated(tenantId, msg, convForSse));

                    if (inboxMobilePushHook != null)
                    {
                        inboxMobilePushHook.scheduleWhatsappInbound(tenantId, msg, convForSse);
                    }

                    if ("text".equals(type) && msg.getId() != null && convForSse != null
                            && convForSse.getId() != null)
                    {
                        InboxAgentInboundPayload payload = new InboxAgentInboundPayload();
                        payload.setChannel(InboxAgentInboundPayload.CHANNEL_WHATSAPP);
                        payload.setConversationId(convForSse.getId());
                        payload.setInboundMessageId(msg.getId());
                        payload.setAccountId(phoneNumberId);
                        payload.setPeerId(waId);
                        payload.setMsgType("text");
                        payload.setText(content);
                        payload.setKeywordAutoReplied(false);
                        inboxAgentInboundDispatchService.scheduleDispatch(tenantId, payload);
                    }
                }
            }

            eventMapper.updateStatus(event.getId(), "processed", null);
        }
        catch (Exception e)
        {
            log.error("Failed to process webhook message {} for tenant {}", messageId, tenantId, e);
            eventMapper.updateStatus(event.getId(), "failed",
                    e.getMessage() != null && e.getMessage().length() > 500
                            ? e.getMessage().substring(0, 500) : e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void processStatus(String tenantId, Map<String, Object> status, String waId,
                               String phoneNumberId, String displayPhoneNumber,
                               Map<String, Object> value)
    {
        String statusValue = (String) status.get("status");
        String messageId = (String) status.get("id");
        String recipientId = metaString(status, "recipient_id", "recipientId");
        if (recipientId != null)
        {
            waId = recipientId;
        }
        long sendTime = parseTimestampMillis(status.get("timestamp"));
        if (sendTime <= 0)
        {
            sendTime = System.currentTimeMillis();
        }

        Date now = new Date();
        String eventType = "message_status";

        // 同一 message 同一投递状态不重复写 event
        if (StringUtils.hasText(messageId) && StringUtils.hasText(statusValue))
        {
            CrmWhatsappWebhookEvent existing = eventMapper.selectByMessageIdAndEventType(
                    tenantId, messageId, eventType);
            if (existing != null && statusValue.equals(existing.getContent())
                    && "processed".equals(existing.getStatus()))
            {
                log.debug("Skip duplicate WhatsApp status event tenant={} messageId={} status={}",
                        tenantId, messageId, statusValue);
                // 仍尝试覆盖 message.status（幂等）
                messageMapper.updateStatusByMessageId(tenantId, messageId, statusValue);
                String itemStatus = normalizeDeliveryStatusForItem(statusValue);
                if (itemStatus != null)
                {
                    sendItemMapper.updateStatusByMessageId(tenantId, messageId, itemStatus);
                }
                publishStatusEvent(tenantId, messageId, statusValue, null);
                return;
            }
        }

        CrmWhatsappWebhookEvent event = new CrmWhatsappWebhookEvent();
        event.setTenantId(tenantId);
        event.setWaId(waId);
        event.setPhoneNumber(displayPhoneNumber);
        event.setPhoneNumberId(phoneNumberId);
        event.setEventType(eventType);
        event.setMessageId(messageId);
        event.setContent(statusValue);
        event.setDirection(1);
        event.setSendTime(sendTime);
        event.setRawJson(JSON.toJSONString(value));
        event.setStatus("pending");
        event.setCreateTime(now);
        eventMapper.insert(event);

        try
        {
            if (StringUtils.hasText(messageId) && StringUtils.hasText(statusValue))
            {
                messageMapper.updateStatusByMessageId(tenantId, messageId, statusValue);
                // 营销群发明细回写 delivered / read / failed
                String itemStatus = normalizeDeliveryStatusForItem(statusValue);
                if (itemStatus != null)
                {
                    sendItemMapper.updateStatusByMessageId(tenantId, messageId, itemStatus);
                }
            }

            CrmWhatsappConversation conv = StringUtils.hasText(waId)
                    ? conversationMapper.selectByWaId(tenantId, waId) : null;
            if (conv == null && StringUtils.hasText(messageId))
            {
                CrmWhatsappMessage msg = messageMapper.selectByMessageId(tenantId, messageId);
                if (msg != null && StringUtils.hasText(msg.getWaId()))
                {
                    waId = msg.getWaId();
                    conv = conversationMapper.selectByWaId(tenantId, waId);
                }
            }

            if (conv != null)
            {
                resolveAndAttachContact(tenantId, conv.getWaId() != null ? conv.getWaId() : waId, conv);
                event.setCrmContactId(conv.getContactId());
                event.setCrmLeadId(conv.getLeadId());
            }

            String activityType = mapStatusToActivityType(statusValue);
            String activityName = mapStatusToActivityName(statusValue);
            int importance = mapStatusToImportance(statusValue);
            if (activityType != null && conv != null)
            {
                produceMessageActivity(conv, activityType, activityName, importance,
                        "engagement", messageId, statusValue, statusValue, sendTime,
                        "whatsapp:status:" + messageId + ":" + statusValue);
            }

            eventMapper.updateStatus(event.getId(), "processed", null);
            publishStatusEvent(tenantId, messageId, statusValue, conv);
        }
        catch (Exception e)
        {
            log.error("Failed to process webhook status {} for tenant {}", messageId, tenantId, e);
            eventMapper.updateStatus(event.getId(), "failed",
                    e.getMessage() != null && e.getMessage().length() > 500
                            ? e.getMessage().substring(0, 500) : e.getMessage());
        }
    }

    private void publishStatusEvent(String tenantId, String messageId, String statusValue,
            CrmWhatsappConversation convHint)
    {
        if (!StringUtils.hasText(messageId) || !StringUtils.hasText(statusValue))
        {
            return;
        }
        Long conversationId = convHint != null ? convHint.getId() : null;
        if (conversationId == null)
        {
            CrmWhatsappMessage existing = messageMapper.selectByMessageId(tenantId, messageId);
            if (existing != null)
            {
                conversationId = existing.getConversationId();
            }
        }
        String errorCode = null;
        String errorMessage = null;
        if (inboxChannelGateService.isEnabled(tenantId, InboxChannel.WHATSAPP))
        {
            inboxEventPublisher.publishMessageStatus(tenantId,
                    WhatsAppInboxSsePayloads.messageStatus(tenantId, conversationId, messageId,
                            statusValue, errorCode, errorMessage));
        }
    }

    /**
     * 会话尚无 contactId 时按 wa_id / 手机号解析并回写；已有则不覆盖。
     * 解析失败不抛出，不影响消息落库。
     */
    private void resolveAndAttachContact(String tenantId, String waId, CrmWhatsappConversation conv)
    {
        if (conv == null || !StringUtils.hasText(waId))
        {
            return;
        }
        if (StringUtils.hasText(conv.getContactId()))
        {
            return;
        }
        try
        {
            String contactId = whatsappIdentityResolutionSupport.resolveContactId(waId);
            if (!StringUtils.hasText(contactId))
            {
                return;
            }
            conversationMapper.updateContactId(tenantId, waId, contactId);
            conv.setContactId(contactId);
        }
        catch (Exception e)
        {
            log.warn("WhatsApp contact resolve failed tenant={} waId={}: {}",
                    tenantId, waId, e.getMessage());
        }
    }

    private void produceMessageActivity(CrmWhatsappConversation conv, String eventType,
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
            dto.setChannel("whatsapp");
            dto.setChannelEventId(channelEventId);
            dto.setEventType(eventType);
            dto.setEventName(eventName);
            dto.setEventCategory(category);
            dto.setImportance(importance);
            dto.setSource("whatsapp_webhook");
            dto.setCreateBy("whatsapp-webhook");
            dto.setStartedDate(new Date(sendTimeMillis));
            if (StringUtils.hasText(content))
            {
                String preview = content.length() > 200 ? content.substring(0, 200) : content;
                dto.setMessageData(preview);
            }
            else
            {
                dto.setMessageData(eventName);
            }

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
            payload.put("wa_id", conv.getWaId());
            payload.put("conversation_id", conv.getId());
            if (StringUtils.hasText(content))
            {
                payload.put("content_preview",
                        content.length() > 200 ? content.substring(0, 200) : content);
            }
            dto.setPayload(payload.toJSONString());

            activityEventProducer.produce(dto);
        }
        catch (Exception e)
        {
            log.warn("Failed to produce WhatsApp Activity eventType={} messageId={}: {}",
                    eventType, messageId, e.getMessage());
        }
    }

    /** Meta delivery status → send_item.status；未知状态不回写 */
    private static String normalizeDeliveryStatusForItem(String status)
    {
        if (status == null)
        {
            return null;
        }
        switch (status.trim().toLowerCase())
        {
            case "sent":
            case "delivered":
            case "read":
            case "failed":
                return status.trim().toLowerCase();
            default:
                return null;
        }
    }

    private static String mapStatusToActivityType(String status)
    {
        if (status == null)
        {
            return null;
        }
        switch (status)
        {
            case "sent":
                return WhatsappActivityEventTypes.MESSAGE_SENT;
            case "delivered":
                return WhatsappActivityEventTypes.MESSAGE_DELIVERED;
            case "read":
                return WhatsappActivityEventTypes.MESSAGE_READ;
            case "failed":
                return WhatsappActivityEventTypes.MESSAGE_FAILED;
            default:
                return null;
        }
    }

    private static String mapStatusToActivityName(String status)
    {
        if (status == null)
        {
            return null;
        }
        switch (status)
        {
            case "sent":
                return WhatsappActivityEventTypes.MESSAGE_SENT_NAME;
            case "delivered":
                return WhatsappActivityEventTypes.MESSAGE_DELIVERED_NAME;
            case "read":
                return WhatsappActivityEventTypes.MESSAGE_READ_NAME;
            case "failed":
                return WhatsappActivityEventTypes.MESSAGE_FAILED_NAME;
            default:
                return null;
        }
    }

    private static int mapStatusToImportance(String status)
    {
        if (status == null)
        {
            return 10;
        }
        switch (status)
        {
            case "sent":
                return WhatsappActivityEventTypes.MESSAGE_SENT_IMPORTANCE;
            case "delivered":
                return WhatsappActivityEventTypes.MESSAGE_DELIVERED_IMPORTANCE;
            case "read":
                return WhatsappActivityEventTypes.MESSAGE_READ_IMPORTANCE;
            case "failed":
                return WhatsappActivityEventTypes.MESSAGE_FAILED_IMPORTANCE;
            default:
                return 10;
        }
    }

    private static String metaString(Map<String, Object> map, String snake, String camel)
    {
        if (map == null)
        {
            return null;
        }
        Object v = map.get(snake);
        if (v == null)
        {
            v = map.get(camel);
        }
        return v != null ? String.valueOf(v) : null;
    }

    private static long parseTimestampMillis(Object tsObj)
    {
        if (tsObj == null)
        {
            return 0L;
        }
        long seconds = 0L;
        if (tsObj instanceof Number)
        {
            seconds = ((Number) tsObj).longValue();
        }
        else if (tsObj instanceof String)
        {
            String s = ((String) tsObj).trim();
            if (s.isEmpty())
            {
                return 0L;
            }
            try
            {
                seconds = Long.parseLong(s);
            }
            catch (NumberFormatException e)
            {
                return 0L;
            }
        }
        // Meta 通常为秒；若已是毫秒级则不乘
        if (seconds > 1_000_000_000_000L)
        {
            return seconds;
        }
        return seconds * 1000L;
    }
}
