package com.ruoyi.crm.wecom.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis pub/sub listener for WeCom KF inbox SSE.
 */
@Configuration
public class WecomKfInboxRedisConfig
{
    private static final Logger log = LoggerFactory.getLogger(WecomKfInboxRedisConfig.class);

    @Bean
    public RedisMessageListenerContainer wecomKfInboxRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            WecomKfInboxRedisSubscriber subscriber)
    {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber,
                new PatternTopic(WecomKfInboxEventPublisher.CHANNEL_PREFIX + "*"));
        log.info("WeCom KF inbox Redis pub/sub listener registered for pattern {}*",
                WecomKfInboxEventPublisher.CHANNEL_PREFIX);
        return container;
    }
}
