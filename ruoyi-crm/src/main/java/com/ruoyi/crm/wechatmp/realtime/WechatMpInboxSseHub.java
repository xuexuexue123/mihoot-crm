package com.ruoyi.crm.wechatmp.realtime;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;

/**
 * Per-tenant local registry of WeChat MP Inbox SSE emitters + heartbeat.
 * Each subscription carries a snapshot of visible appIds for fanout filtering.
 */
@Component
public class WechatMpInboxSseHub
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpInboxSseHub.class);

    public static final long EMITTER_TIMEOUT_MS = 30L * 60L * 1000L;

    private static final long PING_INTERVAL_SEC = 20L;

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Subscription>> emittersByTenant =
            new ConcurrentHashMap<>();

    private ScheduledExecutorService heartbeatExecutor;

    @PostConstruct
    public void startHeartbeat()
    {
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "wechat-mp-inbox-sse-ping");
            t.setDaemon(true);
            return t;
        });
        heartbeatExecutor.scheduleAtFixedRate(this::pingAll, PING_INTERVAL_SEC, PING_INTERVAL_SEC,
                TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown()
    {
        if (heartbeatExecutor != null)
        {
            heartbeatExecutor.shutdownNow();
        }
        for (Map.Entry<String, CopyOnWriteArrayList<Subscription>> e : emittersByTenant.entrySet())
        {
            for (Subscription sub : e.getValue())
            {
                try
                {
                    sub.emitter.complete();
                }
                catch (Exception ignored)
                {
                }
            }
        }
        emittersByTenant.clear();
    }

    public SseEmitter register(String tenantId, Collection<String> visibleAppIds)
    {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        Set<String> apps = visibleAppIds == null ? Collections.emptySet()
                : Collections.unmodifiableSet(new HashSet<>(visibleAppIds));
        Subscription sub = new Subscription(emitter, apps);

        CopyOnWriteArrayList<Subscription> list = emittersByTenant.computeIfAbsent(tenantId,
                k -> new CopyOnWriteArrayList<>());
        list.add(sub);

        Runnable cleanup = () -> remove(tenantId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ex -> cleanup.run());

        try
        {
            send(emitter, WechatMpInboxSsePayloads.EVENT_PING, WechatMpInboxSsePayloads.ping());
        }
        catch (IOException e)
        {
            remove(tenantId, emitter);
            try
            {
                emitter.completeWithError(e);
            }
            catch (Exception ignored)
            {
            }
            throw new IllegalStateException("Failed to open WeChat MP inbox SSE", e);
        }

        log.debug("WeChat MP inbox SSE registered tenant={} apps={} localCount={}",
                tenantId, apps.size(), list.size());
        return emitter;
    }

    public void remove(String tenantId, SseEmitter emitter)
    {
        CopyOnWriteArrayList<Subscription> list = emittersByTenant.get(tenantId);
        if (list == null)
        {
            return;
        }
        list.removeIf(s -> s.emitter == emitter);
        if (list.isEmpty())
        {
            emittersByTenant.remove(tenantId, list);
        }
    }

    public boolean hasLocalSubscribers(String tenantId)
    {
        CopyOnWriteArrayList<Subscription> list = emittersByTenant.get(tenantId);
        return list != null && !list.isEmpty();
    }

    /**
     * Fan-out to local emitters; skips subscribers whose visibleAppIds do not contain payload appId.
     * Ping events (no appId) are always delivered.
     */
    public void broadcast(String tenantId, String eventType, Object data)
    {
        CopyOnWriteArrayList<Subscription> list = emittersByTenant.get(tenantId);
        if (list == null || list.isEmpty())
        {
            return;
        }
        String appId = WechatMpInboxSsePayloads.EVENT_PING.equals(eventType)
                ? null : WechatMpInboxSsePayloads.extractAppId(data);
        for (Subscription sub : list)
        {
            if (StringUtils.hasText(appId) && !sub.visibleAppIds.contains(appId))
            {
                continue;
            }
            try
            {
                send(sub.emitter, eventType, data);
            }
            catch (IOException e)
            {
                remove(tenantId, sub.emitter);
                try
                {
                    sub.emitter.complete();
                }
                catch (Exception ignored)
                {
                }
            }
        }
    }

    private void pingAll()
    {
        Map<String, Object> ping = WechatMpInboxSsePayloads.ping();
        for (Map.Entry<String, CopyOnWriteArrayList<Subscription>> entry : emittersByTenant.entrySet())
        {
            Iterator<Subscription> it = entry.getValue().iterator();
            while (it.hasNext())
            {
                Subscription sub = it.next();
                try
                {
                    send(sub.emitter, WechatMpInboxSsePayloads.EVENT_PING, ping);
                }
                catch (IOException e)
                {
                    remove(entry.getKey(), sub.emitter);
                }
            }
        }
    }

    private static void send(SseEmitter emitter, String eventType, Object data) throws IOException
    {
        String json = data instanceof String ? (String) data : JSON.toJSONString(data);
        emitter.send(SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(eventType)
                .data(json));
    }

    static final class Subscription
    {
        final SseEmitter emitter;
        final Set<String> visibleAppIds;

        Subscription(SseEmitter emitter, Set<String> visibleAppIds)
        {
            this.emitter = emitter;
            this.visibleAppIds = visibleAppIds;
        }
    }
}
