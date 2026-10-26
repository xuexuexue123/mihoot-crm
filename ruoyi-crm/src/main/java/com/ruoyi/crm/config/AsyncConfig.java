package com.ruoyi.crm.config;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步任务线程池配置。
 */
@Configuration
@EnableAsync
public class AsyncConfig
{
    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    /**
     * 智能推送专用异步线程池。
     */
    @Bean(name = "smartPushTaskExecutor")
    public Executor taskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("smart-push-");
        executor.initialize();
        return executor;
    }

    /**
     * WhatsApp 入站媒体落 OSS 专用池。
     * 使用 JDK {@link ThreadPoolExecutor}（非 Spring {@link ThreadPoolTaskExecutor}），
     * 避免与 Flowable 所需的 {@code AsyncListenableTaskExecutor} 产生多候选注入冲突。
     */
    @Bean(name = "whatsappMediaTaskExecutor")
    public Executor whatsappMediaTaskExecutor()
    {
        AtomicInteger seq = new AtomicInteger(1);
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r, "wa-media-" + seq.getAndIncrement());
            t.setDaemon(true);
            return t;
        };
        return new ThreadPoolExecutor(
                2,
                4,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                threadFactory,
                (r, pool) -> log.warn(
                        "WhatsApp media persist queue full; dropping task active={} queue={}",
                        pool.getActiveCount(), pool.getQueue().size()));
    }

    /**
     * Inbox 移动推送（FCM）专用池；满则丢弃，不阻塞入站 webhook。
     */
    @Bean(name = "mobilePushTaskExecutor")
    public Executor mobilePushTaskExecutor()
    {
        AtomicInteger seq = new AtomicInteger(1);
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r, "mobile-push-" + seq.getAndIncrement());
            t.setDaemon(true);
            return t;
        };
        return new ThreadPoolExecutor(
                2,
                4,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                threadFactory,
                (r, pool) -> log.warn(
                        "Mobile push queue full; dropping task active={} queue={}",
                        pool.getActiveCount(), pool.getQueue().size()));
    }
}
