package com.ruoyi.crm.webchat.realtime;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * Receives Redis Web Chat inbox/widget channel messages and fans out to local SSE emitters.
 */
@Component
public class WebchatInboxRedisSubscriber implements MessageListener
{
    private static final Logger log = LoggerFactory.getLogger(WebchatInboxRedisSubscriber.class);

    @Autowired
    private WebchatInboxSseHub sseHub;

    @Override
    public void onMessage(Message message, byte[] pattern)
    {
        if (message == null || message.getBody() == null)
        {
            return;
        }
        String channel = message.getChannel() != null
                ? new String(message.getChannel(), StandardCharsets.UTF_8) : null;
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        handle(channel, body);
    }

    void handle(String channel, String body)
    {
        if (!StringUtils.hasText(body) || !StringUtils.hasText(channel))
        {
            return;
        }
        try
        {
            JSONObject envelope = JSON.parseObject(body);
            if (envelope == null)
            {
                return;
            }
            String event = envelope.getString("event");
            Object data = envelope.get("data");
            if (!StringUtils.hasText(event) || data == null)
            {
                return;
            }
            if (channel.startsWith(WebchatInboxEventPublisher.WIDGET_CHANNEL_PREFIX))
            {
                String rest = channel.substring(WebchatInboxEventPublisher.WIDGET_CHANNEL_PREFIX.length());
                int colon = rest.lastIndexOf(':');
                if (colon <= 0 || colon >= rest.length() - 1)
                {
                    return;
                }
                String tenantId = rest.substring(0, colon);
                Long conversationId = Long.parseLong(rest.substring(colon + 1));
                if (!sseHub.hasLocalWidgetSubscribers(tenantId, conversationId))
                {
                    return;
                }
                sseHub.broadcastWidget(tenantId, conversationId, event, data);
            }
            else if (channel.startsWith(WebchatInboxEventPublisher.INBOX_CHANNEL_PREFIX))
            {
                String tenantId = channel.substring(WebchatInboxEventPublisher.INBOX_CHANNEL_PREFIX.length());
                if (!sseHub.hasLocalSubscribers(tenantId))
                {
                    return;
                }
                sseHub.broadcast(tenantId, event, data);
            }
        }
        catch (Exception e)
        {
            log.warn("Webchat Redis message handle failed channel={}: {}", channel, e.getMessage());
        }
    }
}
