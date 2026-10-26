package com.ruoyi.crm.event;


import com.ruoyi.crm.enums.EventType;

import java.util.function.Consumer;
import java.util.Map;

public interface EventDispatcher {
    void register(EventType type, String sourceId, Consumer<Map<String, Object>> handler);
    void dispatch(EventType type, String sourceId, Map<String, Object> payload);
}
