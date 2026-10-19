package com.ruoyi.crm.event;

import com.ruoyi.crm.enums.EventType;
import com.ruoyi.crm.event.EventDispatcher;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class SimpleEventDispatcher implements EventDispatcher {

    private final Map<String, List<Consumer<Map<String, Object>>>> handlerMap = new ConcurrentHashMap<>();

    private String key(EventType type, String sourceId) {
        return type.name() + "::" + sourceId;
    }

    @Override
    public void register(EventType type, String sourceId, Consumer<Map<String, Object>> handler) {
        String key = key(type, sourceId);
        handlerMap.computeIfAbsent(key, k -> new ArrayList<>()).add(handler);
    }

    @Override
    public void dispatch(EventType type, String sourceId, Map<String, Object> payload) {
        String key = key(type, sourceId);
        List<Consumer<Map<String, Object>>> handlers = handlerMap.getOrDefault(key, Collections.emptyList());
        for (Consumer<Map<String, Object>> handler : handlers) {
            handler.accept(payload);
        }
    }
}
