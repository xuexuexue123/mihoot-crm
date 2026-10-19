package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.ruoyi.crm.domain.dto.CrmRecordDomainEvent;

public class RecordTriggerEventBuilderTest
{
    @Test
    public void diffFieldsIgnoresAuditColumns()
    {
        Map<String, Object> before = new HashMap<>();
        before.put("leadName", "A");
        before.put("updateTime", "2026-01-01");

        Map<String, Object> after = new HashMap<>();
        after.put("leadName", "B");
        after.put("updateTime", "2026-01-02");

        List<String> changed = RecordTriggerEventBuilder.diffFields(before, after);
        assertEquals(1, changed.size());
        assertEquals("leadName", changed.get(0));
    }

    @Test
    public void buildUpdateEventIncludesChangedFields()
    {
        Map<String, Object> before = new HashMap<>();
        before.put("stage", "new");
        Map<String, Object> after = new HashMap<>();
        after.put("stage", "qualified");

        CrmRecordDomainEvent event = RecordTriggerEventBuilder.buildUpdateEvent(
                "lead", "id-1", after, before, "ui");
        assertEquals("updated", event.getEventType());
        assertEquals("update", event.getOperation());
        assertEquals(Arrays.asList("stage"), event.getChangedFields());
    }

    @Test
    public void buildCreateEventSetsCreatedOperation()
    {
        Map<String, Object> record = new HashMap<>();
        record.put("leadName", "Acme");
        CrmRecordDomainEvent event = RecordTriggerEventBuilder.buildCreateEvent("lead", "id-1", record, "ui");
        assertEquals("created", event.getEventType());
        assertEquals("create", event.getOperation());
        assertTrue(event.getChangedFields().isEmpty());
    }
}
