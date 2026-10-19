package com.ruoyi.crm.webchat.realtime;

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
 * Local registry of Web Chat Inbox + Widget SSE emitters + heartbeat ping.
 */
@Component
public class WebchatInboxSseHub
{
    private static final Logger log = LoggerFactory.getLogger(WebchatInboxSseHub.class);

    public static final long EMITTER_TIMEOUT_MS = 30L * 60L * 1000L;

    private static final long PING_INTERVAL_SEC = 20L;

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emittersByTenant =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emittersByWidget =
            new ConcurrentHashMap<>();

    private ScheduledExecutorService heartbeatExecutor;

    @PostConstruct
    public void startHeartbeat()
    {
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "webchat-inbox-sse-ping");
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
        completeAll(emittersByTenant);
        completeAll(emittersByWidget);
    }

    public SseEmitter register(String tenantId)
    {
        return registerInternal(emittersByTenant, tenantId, "inbox");
    }

    public SseEmitter registerWidget(String tenantId, Long conversationId)
    {
        return registerInternal(emittersByWidget, widgetKey(tenantId, conversationId), "widget");
    }

    public void remove(String tenantId, SseEmitter emitter)
    {
        removeInternal(emittersByTenant, tenantId, emitter);
    }

    public void removeWidget(String tenantId, Long conversationId, SseEmitter emitter)
    {
        removeInternal(emittersByWidget, widgetKey(tenantId, conversationId), emitter);
    }

    public boolean hasLocalSubscribers(String tenantId)
    {
        CopyOnWriteArrayList<SseEmitter> list = emittersByTenant.get(tenantId);
        return list != null && !list.isEmpty();
    }

    public boolean hasLocalWidgetSubscribers(String tenantId, Long conversationId)
    {
        CopyOnWriteArrayList<SseEmitter> list = emittersByWidget.get(widgetKey(tenantId, conversationId));
        return list != null && !list.isEmpty();
    }

    public void broadcast(String tenantId, String eventType, Object data)
    {
        broadcastInternal(emittersByTenant, tenantId, eventType, data);
    }

    public void broadcastWidget(String tenantId, Long conversationId, String eventType, Object data)
    {
        broadcastInternal(emittersByWidget, widgetKey(tenantId, conversationId), eventType, data);
    }

    private SseEmitter registerInternal(ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> map,
            String key, String kind)
    {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        CopyOnWriteArrayList<SseEmitter> list = map.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        Runnable cleanup = () -> removeInternal(map, key, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ex -> cleanup.run());

        try
        {
            send(emitter, WebchatInboxSsePayloads.EVENT_PING, WebchatInboxSsePayloads.ping());
        }
        catch (IOException e)
        {
            removeInternal(map, key, emitter);
            try
            {
                emitter.completeWithError(e);
            }
            catch (Exception ignored)
            {
            }
            throw new IllegalStateException("Failed to open Webchat " + kind + " SSE", e);
        }
        log.debug("Webchat {} SSE registered key={} localCount={}", kind, key, list.size());
        return emitter;
    }

    private void broadcastInternal(ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> map,
            String key, String eventType, Object data)
    {
        CopyOnWriteArrayList<SseEmitter> list = map.get(key);
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
                removeInternal(map, key, emitter);
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

    private void removeInternal(ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> map,
            String key, SseEmitter emitter)
    {
        CopyOnWriteArrayList<SseEmitter> list = map.get(key);
        if (list == null)
        {
            return;
        }
        list.remove(emitter);
        if (list.isEmpty())
        {
            map.remove(key, list);
        }
    }

    private void pingAll()
    {
        Map<String, Object> ping = WebchatInboxSsePayloads.ping();
        pingMap(emittersByTenant, ping);
        pingMap(emittersByWidget, ping);
    }

    private void pingMap(ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> map,
            Map<String, Object> ping)
    {
        for (Map.Entry<String, CopyOnWriteArrayList<SseEmitter>> entry : map.entrySet())
        {
            List<SseEmitter> list = entry.getValue();
            Iterator<SseEmitter> it = list.iterator();
            while (it.hasNext())
            {
                SseEmitter emitter = it.next();
                try
                {
                    send(emitter, WebchatInboxSsePayloads.EVENT_PING, ping);
                }
                catch (IOException e)
                {
                    removeInternal(map, entry.getKey(), emitter);
                }
            }
        }
    }

    private static void completeAll(ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> map)
    {
        for (Map.Entry<String, CopyOnWriteArrayList<SseEmitter>> e : map.entrySet())
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
        map.clear();
    }

    public static String widgetKey(String tenantId, Long conversationId)
    {
        return tenantId + ":" + conversationId;
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
