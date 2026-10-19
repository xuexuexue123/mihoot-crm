package com.ruoyi.crm.messenger.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis pub/sub for Messenger inbox SSE multi-instance fanout.
 * Reuses the existing {@link org.springframework.data.redis.core.StringRedisTemplate} bean.
 */
@Configuration
public class MessengerInboxRedisConfig
{
    private static final Logger log = LoggerFactory.getLogger(MessengerInboxRedisConfig.class);

    @Bean
    public RedisMessageListenerContainer messengerInboxRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessengerInboxRedisSubscriber subscriber)
    {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber,
                new PatternTopic(MessengerInboxEventPublisher.CHANNEL_PREFIX + "*"));
        log.info("Messenger inbox Redis pub/sub listener registered for pattern {}*",
                MessengerInboxEventPublisher.CHANNEL_PREFIX);
        return container;
    }
}
