package com.ruoyi.crm.mobile.push;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.ruoyi.crm.domain.CrmMessengerConversation;
import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.CrmWebchatMessage;
import com.ruoyi.crm.domain.CrmMessengerMessage;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 与 Inbox SSE {@code message.created} 同钩子的移动推送入口。
 */
@Component
public class InboxMobilePushHook
{
    private static final Logger log = LoggerFactory.getLogger(InboxMobilePushHook.class);

    @Autowired
    @Qualifier("mobilePushTaskExecutor")
    private Executor mobilePushTaskExecutor;

    @Autowired
    private InboxMobilePushService pushService;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    public void schedule(InboxMobilePushRequest request)
    {
        if (request == null || !StringUtils.hasText(request.getTenantId()))
        {
            return;
        }
        if (!inboxChannelGateService.isEnabled(request.getTenantId(), request.getChannel()))
        {
            return;
        }
        Runnable task = () -> runWithTenant(request);
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    submit(task);
                }
            });
        }
        else
        {
            submit(task);
        }
    }

    public void scheduleWhatsappInbound(String tenantId, CrmWhatsappMessage msg,
            CrmWhatsappConversation conv)
    {
        if (msg == null || conv == null || conv.getUserId() == null)
        {
            return;
        }
        InboxMobilePushRequest req = new InboxMobilePushRequest();
        req.setTenantId(tenantId);
        req.setChannel(InboxMobilePushRequest.CHANNEL_WHATSAPP);
        req.setConversationId(conv.getId());
        req.setConversationKey(InboxMobilePushRequest.CHANNEL_WHATSAPP + ":" + conv.getId());
        req.setOwnerUserId(conv.getUserId());
        req.setMessageType(msg.getMessageType());
        req.setMessageContent(msg.getContent());
        req.setPeerDisplayName(firstNonBlank(conv.getPhoneNumber(), conv.getWaId()));
        req.setConversationUnread(conv.getUnreadCount());
        schedule(req);
    }

    public void scheduleMessengerInbound(String tenantId, CrmMessengerMessage msg,
            CrmMessengerConversation conv)
    {
        if (msg == null || conv == null || conv.getUserId() == null || msg.getDirection() == null
                || msg.getDirection() != 0)
        {
            return;
        }
        InboxMobilePushRequest req = new InboxMobilePushRequest();
        req.setTenantId(tenantId);
        req.setChannel(InboxMobilePushRequest.CHANNEL_MESSENGER);
        req.setConversationId(conv.getId());
        req.setConversationKey(InboxMobilePushRequest.CHANNEL_MESSENGER + ":" + conv.getId());
        req.setOwnerUserId(conv.getUserId());
        req.setMessageType(msg.getMessageType());
        req.setMessageContent(msg.getContent());
        req.setPeerDisplayName(conv.getProfileName());
        req.setConversationUnread(conv.getUnreadCount());
        schedule(req);
    }

    public void scheduleWebchatInbound(String tenantId, CrmWebchatMessage msg,
            CrmWebchatConversation conv)
    {
        if (msg == null || conv == null || conv.getUserId() == null
                || msg.getDirection() == null || msg.getDirection() != 0)
        {
            return;
        }
        InboxMobilePushRequest req = new InboxMobilePushRequest();
        req.setTenantId(tenantId);
        req.setChannel(InboxMobilePushRequest.CHANNEL_WEBCHAT);
        req.setConversationId(conv.getId());
        req.setConversationKey(InboxMobilePushRequest.CHANNEL_WEBCHAT + ":" + conv.getId());
        req.setOwnerUserId(conv.getUserId());
        req.setMessageType(msg.getMessageType());
        req.setMessageContent(msg.getContent());
        req.setPeerDisplayName(firstNonBlank(conv.getProfileName(), conv.getVisitorId()));
        req.setConversationUnread(conv.getUnreadCount());
        schedule(req);
    }

    public void scheduleWechatMpInbound(String tenantId, CrmWechatMpMessage msg,
            CrmWechatMpConversation conv)
    {
        if (msg == null || conv == null || conv.getUserId() == null
                || !"inbound".equals(msg.getDirection()))
        {
            return;
        }
        InboxMobilePushRequest req = new InboxMobilePushRequest();
        req.setTenantId(tenantId);
        req.setChannel(InboxMobilePushRequest.CHANNEL_WECHAT_MP);
        req.setConversationId(conv.getId());
        if (conv.getId() != null)
        {
            req.setConversationKey(InboxMobilePushRequest.CHANNEL_WECHAT_MP + ":" + conv.getId());
        }
        else
        {
            req.setConversationKey(InboxMobilePushRequest.CHANNEL_WECHAT_MP + ":"
                    + msg.getAppId() + ":" + msg.getOpenId());
        }
        req.setOwnerUserId(conv.getUserId());
        req.setMessageType(msg.getMsgType());
        req.setMessageContent(msg.getContent());
        req.setPeerDisplayName(firstNonBlank(conv.getNickname(), conv.getContactName()));
        req.setConversationUnread(conv.getUnreadCount());
        schedule(req);
    }

    private void submit(Runnable task)
    {
        try
        {
            mobilePushTaskExecutor.execute(task);
        }
        catch (Exception e)
        {
            log.warn("Mobile push task rejected: {}", e.getMessage());
        }
    }

    private void runWithTenant(InboxMobilePushRequest request)
    {
        String tenantKey = request.getTenantId();
        try
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenantKey);
            pushService.dispatch(request);
        }
        catch (Exception e)
        {
            log.warn("Mobile push hook failed tenant={}: {}", tenantKey, e.getMessage());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private static String firstNonBlank(String... values)
    {
        if (values == null)
        {
            return null;
        }
        for (String v : values)
        {
            if (StringUtils.hasText(v))
            {
                return v;
            }
        }
        return null;
    }
}
