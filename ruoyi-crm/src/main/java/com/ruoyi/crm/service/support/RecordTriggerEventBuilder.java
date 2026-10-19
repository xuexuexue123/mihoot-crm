package com.ruoyi.crm.service.support;

import java.beans.PropertyDescriptor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.CrmRecordDomainEvent;

/**
 * 构建 CRM Record Trigger 领域事件 payload
 */
public final class RecordTriggerEventBuilder
{
    private static final Set<String> IGNORED_FIELDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "createTime", "updateTime", "createBy", "updateBy", "searchValue", "params", "remark")));

    private RecordTriggerEventBuilder()
    {
    }

    public static CrmRecordDomainEvent buildCreateEvent(
            String entityType, String recordId, Object record, String source)
    {
        CrmRecordDomainEvent event = baseEvent(entityType, recordId, source);
        event.setOperation(RecordTriggerUtils.OPERATION_CREATE);
        event.setEventType("created");
        event.setRecord(toRecordMap(record));
        event.setPreviousRecord(null);
        event.setChangedFields(Collections.emptyList());
        return event;
    }

    public static CrmRecordDomainEvent buildUpdateEvent(
            String entityType, String recordId, Object record, Object previousRecord, String source)
    {
        Map<String, Object> currentMap = toRecordMap(record);
        Map<String, Object> previousMap = toRecordMap(previousRecord);
        List<String> changedFields = diffFields(previousMap, currentMap);

        CrmRecordDomainEvent event = baseEvent(entityType, recordId, source);
        event.setOperation(RecordTriggerUtils.OPERATION_UPDATE);
        event.setEventType("updated");
        event.setRecord(currentMap);
        event.setPreviousRecord(previousMap);
        event.setChangedFields(changedFields);
        return event;
    }

    public static CrmRecordDomainEvent buildDeleteEvent(
            String entityType, String recordId, Object record, String source)
    {
        CrmRecordDomainEvent event = baseEvent(entityType, recordId, source);
        event.setOperation(RecordTriggerUtils.OPERATION_DELETE);
        event.setEventType("deleted");
        event.setRecord(toRecordMap(record));
        event.setPreviousRecord(null);
        event.setChangedFields(Collections.emptyList());
        return event;
    }

    public static List<String> diffFields(Map<String, Object> before, Map<String, Object> after)
    {
        if (before == null || before.isEmpty())
        {
            return after != null ? new ArrayList<>(after.keySet()) : Collections.emptyList();
        }
        if (after == null || after.isEmpty())
        {
            return Collections.emptyList();
        }
        Set<String> keys = new HashSet<>();
        keys.addAll(before.keySet());
        keys.addAll(after.keySet());

        List<String> changed = new ArrayList<>();
        for (String key : keys)
        {
            if (IGNORED_FIELDS.contains(key))
            {
                continue;
            }
            Object oldValue = before.get(key);
            Object newValue = after.get(key);
            if (!valuesEqual(oldValue, newValue))
            {
                changed.add(key);
            }
        }
        Collections.sort(changed);
        return changed;
    }

    public static Map<String, Object> toRecordMap(Object record)
    {
        if (record == null)
        {
            return Collections.emptyMap();
        }
        if (record instanceof Map)
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) record;
            return new HashMap<>(map);
        }
        return JSON.parseObject(JSON.toJSONString(record), new TypeReference<Map<String, Object>>() {});
    }

    private static CrmRecordDomainEvent baseEvent(String entityType, String recordId, String source)
    {
        CrmRecordDomainEvent event = new CrmRecordDomainEvent();
        event.setEntityType(entityType);
        event.setRecordId(recordId);
        event.setSource(StringUtils.isNotBlank(source) ? source : "ui");
        event.setOccurredAt(Instant.now().toString());
        return event;
    }

    private static boolean valuesEqual(Object oldValue, Object newValue)
    {
        if (oldValue == newValue)
        {
            return true;
        }
        if (oldValue == null || newValue == null)
        {
            return false;
        }
        return oldValue.equals(newValue);
    }
}
