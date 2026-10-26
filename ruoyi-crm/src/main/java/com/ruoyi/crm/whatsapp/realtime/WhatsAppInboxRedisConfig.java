package com.ruoyi.crm.whatsapp.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * String Redis pub/sub for WhatsApp inbox SSE multi-instance fanout.
 */
@Configuration
public class WhatsAppInboxRedisConfig
{
    private static final Logger log = LoggerFactory.getLogger(WhatsAppInboxRedisConfig.class);

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory)
    {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(StringRedisSerializer.UTF_8);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashValueSerializer(StringRedisSerializer.UTF_8);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisMessageListenerContainer whatsAppInboxRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            WhatsAppInboxRedisSubscriber subscriber)
    {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber,
                new PatternTopic(WhatsAppInboxEventPublisher.CHANNEL_PREFIX + "*"));
        log.info("WhatsApp inbox Redis pub/sub listener registered for pattern {}*",
                WhatsAppInboxEventPublisher.CHANNEL_PREFIX);
        return container;
    }
}
