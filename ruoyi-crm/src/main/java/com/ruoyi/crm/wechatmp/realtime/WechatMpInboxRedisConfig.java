package com.ruoyi.crm.wechatmp.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis pub/sub listener for WeChat MP inbox SSE (reuses existing StringRedisTemplate bean).
 */
@Configuration
public class WechatMpInboxRedisConfig
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpInboxRedisConfig.class);

    @Bean
    public RedisMessageListenerContainer wechatMpInboxRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            WechatMpInboxRedisSubscriber subscriber)
    {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber,
                new PatternTopic(WechatMpInboxEventPublisher.CHANNEL_PREFIX + "*"));
        log.info("WeChat MP inbox Redis pub/sub listener registered for pattern {}*",
                WechatMpInboxEventPublisher.CHANNEL_PREFIX);
        return container;
    }
}
