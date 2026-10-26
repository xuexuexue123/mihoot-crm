package com.ruoyi.crm.wecom.realtime;

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
 * Per-tenant local registry of WeCom KF Inbox SSE emitters + heartbeat.
 * Empty visibleOpenKfids means allow all openKfids (matches KF config semantics).
 */
@Component
public class WecomKfInboxSseHub
{
    private static final Logger log = LoggerFactory.getLogger(WecomKfInboxSseHub.class);

    public static final long EMITTER_TIMEOUT_MS = 30L * 60L * 1000L;

    private static final long PING_INTERVAL_SEC = 20L;

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Subscription>> emittersByTenant =
            new ConcurrentHashMap<>();

    private ScheduledExecutorService heartbeatExecutor;

    @PostConstruct
    public void startHeartbeat()
    {
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "wecom-kf-inbox-sse-ping");
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

    public SseEmitter register(String tenantId, Collection<String> visibleOpenKfids)
    {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        Set<String> openKfids = visibleOpenKfids == null ? Collections.emptySet()
                : Collections.unmodifiableSet(new HashSet<>(visibleOpenKfids));
        Subscription sub = new Subscription(emitter, openKfids);

        CopyOnWriteArrayList<Subscription> list = emittersByTenant.computeIfAbsent(tenantId,
                k -> new CopyOnWriteArrayList<>());
        list.add(sub);

        Runnable cleanup = () -> remove(tenantId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ex -> cleanup.run());

        try
        {
            send(emitter, WecomKfInboxSsePayloads.EVENT_PING, WecomKfInboxSsePayloads.ping());
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
            throw new IllegalStateException("Failed to open WeCom KF inbox SSE", e);
        }

        log.debug("WeCom KF inbox SSE registered tenant={} openKfids={} localCount={}",
                tenantId, openKfids.size(), list.size());
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

    /** Close all SSE connections for a tenant (e.g. channel disabled). */
    public void closeTenant(String tenantId)
    {
        if (!StringUtils.hasText(tenantId))
        {
            return;
        }
        CopyOnWriteArrayList<Subscription> list = emittersByTenant.remove(tenantId);
        if (list == null || list.isEmpty())
        {
            return;
        }
        for (Subscription sub : list)
        {
            try
            {
                sub.emitter.complete();
            }
            catch (Exception ignored)
            {
            }
        }
        log.info("WeCom KF inbox SSE closed for tenant={} count={}", tenantId, list.size());
    }

    public boolean hasLocalSubscribers(String tenantId)
    {
        CopyOnWriteArrayList<Subscription> list = emittersByTenant.get(tenantId);
        return list != null && !list.isEmpty();
    }

    /**
     * Fan-out to local emitters. Empty visibleOpenKfids = allow all.
     * Ping events are always delivered.
     */
    public void broadcast(String tenantId, String eventType, Object data)
    {
        CopyOnWriteArrayList<Subscription> list = emittersByTenant.get(tenantId);
        if (list == null || list.isEmpty())
        {
            return;
        }
        String openKfid = WecomKfInboxSsePayloads.EVENT_PING.equals(eventType)
                ? null : WecomKfInboxSsePayloads.extractOpenKfid(data);
        for (Subscription sub : list)
        {
            if (StringUtils.hasText(openKfid)
                    && !sub.visibleOpenKfids.isEmpty()
                    && !sub.visibleOpenKfids.contains(openKfid))
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
        Map<String, Object> ping = WecomKfInboxSsePayloads.ping();
        for (Map.Entry<String, CopyOnWriteArrayList<Subscription>> entry : emittersByTenant.entrySet())
        {
            Iterator<Subscription> it = entry.getValue().iterator();
            while (it.hasNext())
            {
                Subscription sub = it.next();
                try
                {
                    send(sub.emitter, WecomKfInboxSsePayloads.EVENT_PING, ping);
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
        final Set<String> visibleOpenKfids;

        Subscription(SseEmitter emitter, Set<String> visibleOpenKfids)
        {
            this.emitter = emitter;
            this.visibleOpenKfids = visibleOpenKfids;
        }
    }
}
