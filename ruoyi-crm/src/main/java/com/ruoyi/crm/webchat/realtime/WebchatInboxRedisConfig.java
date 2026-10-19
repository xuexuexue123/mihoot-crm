package com.ruoyi.crm.webchat.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis pub/sub for Web Chat inbox + widget SSE multi-instance fanout.
 */
@Configuration
public class WebchatInboxRedisConfig
{
    private static final Logger log = LoggerFactory.getLogger(WebchatInboxRedisConfig.class);

    @Bean
    public RedisMessageListenerContainer webchatInboxRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            WebchatInboxRedisSubscriber subscriber)
    {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber,
                new PatternTopic(WebchatInboxEventPublisher.INBOX_CHANNEL_PREFIX + "*"));
        container.addMessageListener(subscriber,
                new PatternTopic(WebchatInboxEventPublisher.WIDGET_CHANNEL_PREFIX + "*"));
        log.info("Webchat inbox Redis pub/sub listener registered for patterns {}* and {}*",
                WebchatInboxEventPublisher.INBOX_CHANNEL_PREFIX,
                WebchatInboxEventPublisher.WIDGET_CHANNEL_PREFIX);
        return container;
    }
}
