package com.ruoyi.crm.messenger.realtime;

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
 * Receives Redis Messenger inbox channel messages and fans out to local SSE emitters.
 */
@Component
public class MessengerInboxRedisSubscriber implements MessageListener
{
    private static final Logger log = LoggerFactory.getLogger(MessengerInboxRedisSubscriber.class);

    @Autowired
    private MessengerInboxSseHub sseHub;

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
        if (!StringUtils.hasText(body))
        {
            return;
        }
        String tenantId = extractTenantId(channel);
        if (!StringUtils.hasText(tenantId) || !sseHub.hasLocalSubscribers(tenantId))
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
            sseHub.broadcast(tenantId, event, data);
        }
        catch (Exception e)
        {
            log.warn("Messenger inbox Redis message handle failed channel={}: {}", channel, e.getMessage());
        }
    }

    static String extractTenantId(String channel)
    {
        if (!StringUtils.hasText(channel))
        {
            return null;
        }
        String prefix = MessengerInboxEventPublisher.CHANNEL_PREFIX;
        if (channel.startsWith(prefix) && channel.length() > prefix.length())
        {
            return channel.substring(prefix.length());
        }
        return null;
    }
}
