package com.ruoyi.crm.messenger.realtime;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;

/**
 * Per-tenant local registry of Messenger Inbox SSE emitters + heartbeat ping.
 */
@Component
public class MessengerInboxSseHub
{
    private static final Logger log = LoggerFactory.getLogger(MessengerInboxSseHub.class);

    /** 30 minutes */
    public static final long EMITTER_TIMEOUT_MS = 30L * 60L * 1000L;

    private static final long PING_INTERVAL_SEC = 20L;

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emittersByTenant =
            new ConcurrentHashMap<>();

    private ScheduledExecutorService heartbeatExecutor;

    @PostConstruct
    public void startHeartbeat()
    {
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "messenger-inbox-sse-ping");
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
        for (Map.Entry<String, CopyOnWriteArrayList<SseEmitter>> e : emittersByTenant.entrySet())
        {
            for (SseEmitter emitter : e.getValue())
            {
                try
                {
                    emitter.complete();
                }
                catch (Exception ignored)
                {
                }
            }
        }
        emittersByTenant.clear();
    }

    public SseEmitter register(String tenantId)
    {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        CopyOnWriteArrayList<SseEmitter> list = emittersByTenant.computeIfAbsent(tenantId,
                k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        Runnable cleanup = () -> remove(tenantId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ex -> cleanup.run());

        try
        {
            send(emitter, MessengerInboxSsePayloads.EVENT_PING, MessengerInboxSsePayloads.ping());
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
            throw new IllegalStateException("Failed to open Messenger inbox SSE", e);
        }

        log.debug("Messenger inbox SSE registered tenant={} localCount={}", tenantId, list.size());
        return emitter;
    }

    public void remove(String tenantId, SseEmitter emitter)
    {
        CopyOnWriteArrayList<SseEmitter> list = emittersByTenant.get(tenantId);
        if (list == null)
        {
            return;
        }
        list.remove(emitter);
        if (list.isEmpty())
        {
            emittersByTenant.remove(tenantId, list);
        }
    }

    public boolean hasLocalSubscribers(String tenantId)
    {
        CopyOnWriteArrayList<SseEmitter> list = emittersByTenant.get(tenantId);
        return list != null && !list.isEmpty();
    }

    /**
     * Fan-out to all local emitters for the tenant.
     *
     * @param eventType SSE event name
     * @param data      JSON-serializable payload (also embedded as data.type)
     */
    public void broadcast(String tenantId, String eventType, Object data)
    {
        CopyOnWriteArrayList<SseEmitter> list = emittersByTenant.get(tenantId);
        if (list == null || list.isEmpty())
        {
            return;
        }
        for (SseEmitter emitter : list)
        {
            try
            {
                send(emitter, eventType, data);
            }
            catch (IOException e)
            {
                remove(tenantId, emitter);
                try
                {
                    emitter.complete();
                }
                catch (Exception ignored)
                {
                }
            }
        }
    }

    private void pingAll()
    {
        Map<String, Object> ping = MessengerInboxSsePayloads.ping();
        for (Map.Entry<String, CopyOnWriteArrayList<SseEmitter>> entry : emittersByTenant.entrySet())
        {
            List<SseEmitter> list = entry.getValue();
            Iterator<SseEmitter> it = list.iterator();
            while (it.hasNext())
            {
                SseEmitter emitter = it.next();
                try
                {
                    send(emitter, MessengerInboxSsePayloads.EVENT_PING, ping);
                }
                catch (IOException e)
                {
                    remove(entry.getKey(), emitter);
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
}
