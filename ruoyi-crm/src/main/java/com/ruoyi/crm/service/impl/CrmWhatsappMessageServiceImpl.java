package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.domain.CrmWhatsappTemplate;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppSendResult;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappConversationMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateMapper;
import com.ruoyi.crm.service.ICrmWhatsappMessageService;
import com.ruoyi.crm.service.IInboxAgentChannelService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsAppClient;
import com.ruoyi.crm.whatsapp.WhatsAppTemplateStatuses;
import com.ruoyi.crm.whatsapp.WhatsappTemplateMessageStore;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxEventPublisher;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxSsePayloads;

@Service
public class CrmWhatsappMessageServiceImpl implements ICrmWhatsappMessageService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappMessageServiceImpl.class);

    private static final long MESSAGING_WINDOW_MS = 86400_000L;

    @Autowired
    private CrmWhatsappConfigMapper configMapper;

    @Autowired
    private CrmWhatsappConversationMapper conversationMapper;

    @Autowired
    private CrmWhatsappMessageMapper messageMapper;

    @Autowired
    private CrmWhatsappTemplateMapper templateMapper;

    @Autowired
    private WhatsAppClient whatsappClient;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private WhatsAppInboxEventPublisher inboxEventPublisher;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Override
    public WhatsAppSendResult sendMessage(String tenantId, String waId, String phoneNumberId,
                                          String messageType, String content, String mediaId,
                                          String mediaUrl, String caption, Map<String, Object> template)
    {
        return sendMessageInternal(tenantId, waId, phoneNumberId, messageType, content, mediaId,
                mediaUrl, caption, template, false);
    }

    @Override
    public WhatsAppSendResult sendMessageAsAi(String tenantId, String waId, String phoneNumberId,
                                              String messageType, String content, String mediaId,
                                              String mediaUrl, String caption, Map<String, Object> template)
    {
        return sendMessageInternal(tenantId, waId, phoneNumberId, messageType, content, mediaId,
                mediaUrl, caption, template, true);
    }

    private WhatsAppSendResult sendMessageInternal(String tenantId, String waId, String phoneNumberId,
                                          String messageType, String content, String mediaId,
                                          String mediaUrl, String caption, Map<String, Object> template,
                                          boolean asAiAgent)
    {
        if (!inboxChannelGateService.isEnabled(tenantId, InboxChannel.WHATSAPP))
        {
            throw new ServiceException("inbox_channel_disabled", HttpStatus.FORBIDDEN);
        }
        if (waId == null || waId.isEmpty())
        {
            throw new ServiceException("waId is required", 400);
        }
        if (messageType == null || messageType.isEmpty())
        {
            throw new ServiceException("messageType is required", 400);
        }

        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null || config.getAccessTokenEnc() == null)
        {
            throw new ServiceException("WhatsApp configuration not found", 400);
        }
        String accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());
        String actualPhoneNumberId = phoneNumberId != null ? phoneNumberId : config.getPhoneNumberId();

        CrmWhatsappConversation owned = conversationMapper.selectByWaId(tenantId, waId);
        if (!asAiAgent)
        {
            assertOwnedForSend(owned);
        }
        else if (owned == null)
        {
            throw new ServiceException("会话不存在", HttpStatus.NOT_FOUND);
        }
        assertMessagingWindowAllows(owned, messageType);

        log.info("WhatsApp sendMessage tenant={} waId={} phoneNumberId={} messageType={} "
                        + "hasMediaId={} hasMediaUrl={} hasCaption={} hasTemplate={}",
                tenantId, waId, actualPhoneNumberId, messageType,
                mediaId != null && !mediaId.isEmpty(),
                mediaUrl != null && !mediaUrl.isEmpty(),
                caption != null && !caption.isEmpty(),
                template != null);

        if (("image".equals(messageType) || "document".equals(messageType))
                && (mediaId == null || mediaId.isEmpty())
                && (mediaUrl == null || mediaUrl.isEmpty()))
        {
            throw new ServiceException(
                    "image/document requires mediaId or mediaUrl (Meta needs id or link)", 400);
        }

        String tplName = null;
        String tplLang = null;
        JSONArray tplSendComponents = null;
        CrmWhatsappTemplate cachedTemplate = null;
        WhatsAppSendResult result;
        switch (messageType)
        {
            case "text":
                result = whatsappClient.sendTextMessage(actualPhoneNumberId, waId, content, accessToken, false);
                break;
            case "image":
                result = whatsappClient.sendImageMessage(
                        actualPhoneNumberId, waId, mediaId, mediaUrl, caption, accessToken);
                break;
            case "document":
                result = whatsappClient.sendDocumentMessage(
                        actualPhoneNumberId, waId, mediaId, mediaUrl, null, caption, accessToken);
                break;
            case "template":
            {
                if (template == null)
                {
                    throw new ServiceException("template is required for template message type", 400);
                }
                tplName = (String) template.get("name");
                tplLang = (String) template.get("language");
                if (tplName == null || tplName.isEmpty() || tplLang == null || tplLang.isEmpty())
                {
                    throw new ServiceException("template.name and template.language are required", 400);
                }
                cachedTemplate = templateMapper.selectByTemplate(tenantId, tplName, tplLang);
                if (cachedTemplate == null || !WhatsAppTemplateStatuses.isApproved(cachedTemplate.getStatus()))
                {
                    throw new ServiceException("Template not found or not approved: " + tplName, 400);
                }
                Object comps = template.get("components");
                if (comps != null)
                {
                    tplSendComponents = com.alibaba.fastjson2.JSON.parseArray(
                            com.alibaba.fastjson2.JSON.toJSONString(comps));
                }
                log.info("WhatsApp sendMessage template branch name={} lang={} components={}",
                        tplName, tplLang, tplSendComponents != null ? tplSendComponents.toJSONString() : null);
                result = whatsappClient.sendTemplateMessage(actualPhoneNumberId, waId, tplName, tplLang,
                        tplSendComponents, accessToken);
                break;
            }
            default:
                throw new ServiceException("Unsupported message type: " + messageType, 400);
        }

        if (!result.isSuccess())
        {
            log.error("WhatsApp sendMessage failed tenant={} waId={} messageType={} error={}",
                    tenantId, waId, messageType, result.getErrorMessage());
        }

        if (result.isSuccess())
        {
            // 写入消息记录
            CrmWhatsappConversation conv = conversationMapper.selectByWaId(tenantId, waId);
            if (conv != null)
            {
                String storedContent;
                String storedRawJson = null;
                String preview;
                if ("template".equals(messageType))
                {
                    WhatsappTemplateMessageStore.StoredTemplate stored = WhatsappTemplateMessageStore.build(
                            content,
                            tplName,
                            tplLang,
                            tplSendComponents,
                            cachedTemplate != null ? cachedTemplate.getComponents() : null);
                    storedContent = stored.content;
                    storedRawJson = stored.rawJson;
                    preview = stored.preview;
                }
                else
                {
                    storedContent = resolveStoredContent(messageType, content, caption, tplName);
                    preview = truncatePreview(storedContent);
                }

                CrmWhatsappMessage msg = new CrmWhatsappMessage();
                msg.setTenantId(tenantId);
                msg.setConversationId(conv.getId());
                msg.setWaId(waId);
                msg.setMessageId(result.getMessageId());
                msg.setMessageType(messageType);
                msg.setContent(storedContent);
                msg.setRawJson(storedRawJson);
                msg.setDirection(1);
                msg.setSendTime(System.currentTimeMillis());
                msg.setStatus("sent");
                if (mediaId != null && !mediaId.isEmpty())
                {
                    msg.setMediaId(mediaId);
                }
                if (mediaUrl != null && !mediaUrl.isEmpty())
                {
                    msg.setMediaUrl(mediaUrl);
                }
                msg.setCreateTime(new Date());
                if (asAiAgent)
                {
                    msg.setSource(IInboxAgentChannelService.SOURCE_AI_INBOX_AGENT);
                }
                messageMapper.insert(msg);

                // 更新会话
                CrmWhatsappConversation updated = new CrmWhatsappConversation();
                updated.setId(conv.getId());
                updated.setWaId(waId);
                updated.setLastMessageAt(new Date());
                updated.setLastMessagePreview(preview);
                updated.setLastOutboundAt(new Date());
                conversationMapper.updateLastMessage(updated);

                CrmWhatsappConversation previewConv = conversationMapper.selectByWaId(tenantId, waId);
                if (previewConv == null)
                {
                    previewConv = conv;
                    previewConv.setLastMessageAt(updated.getLastMessageAt());
                    previewConv.setLastMessagePreview(updated.getLastMessagePreview());
                    previewConv.setLastOutboundAt(updated.getLastOutboundAt());
                }
                inboxEventPublisher.publishMessageCreated(tenantId,
                        WhatsAppInboxSsePayloads.messageCreated(tenantId, msg, previewConv));
            }
        }

        return result;
    }

    /**
     * 自由文本/媒体受 24h 客服窗限制；模板消息可窗外发送。
     * lastCustomerMessageAt 为空时与前端一致，不视为已过期。
     */
    private static void assertMessagingWindowAllows(CrmWhatsappConversation conv, String messageType)
    {
        if ("template".equals(messageType))
        {
            return;
        }
        Date lastCustomerMessageAt = conv.getLastCustomerMessageAt();
        if (lastCustomerMessageAt == null)
        {
            return;
        }
        long remainingSec = (lastCustomerMessageAt.getTime() + MESSAGING_WINDOW_MS - System.currentTimeMillis()) / 1000;
        if (remainingSec <= 0)
        {
            throw new ServiceException("会话窗已过期，请发送模板消息", 400);
        }
    }

    private static String resolveStoredContent(String messageType, String content, String caption, String tplName)
    {
        if (content != null && !content.isEmpty())
        {
            return content;
        }
        return caption;
    }

    private static String truncatePreview(String content)
    {
        if (content == null)
        {
            return null;
        }
        return content.length() > 200 ? content.substring(0, 200) : content;
    }

    private static void assertOwnedForSend(CrmWhatsappConversation conv)
    {
        if (conv == null)
        {
            throw new ServiceException("会话不存在，请先从公共池领取", HttpStatus.FORBIDDEN);
        }
        Long currentUserId = SecurityUtils.getUserId();
        if (conv.getUserId() == null || !conv.getUserId().equals(currentUserId))
        {
            throw new ServiceException("仅会话负责人可发送消息", HttpStatus.FORBIDDEN);
        }
    }
}
