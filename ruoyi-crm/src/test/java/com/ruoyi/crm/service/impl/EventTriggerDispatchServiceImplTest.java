package com.ruoyi.crm.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.ruoyi.crm.domain.dto.EventTriggerDispatchPayload;

/**
 * Dispatch skip paths（未配置 URL / disabled）不抛异常、不发 HTTP。
 */
class EventTriggerDispatchServiceImplTest
{
    private EventTriggerDispatchServiceImpl dispatchService;

    @BeforeEach
    void setUp()
    {
        dispatchService = new EventTriggerDispatchServiceImpl();
    }

    @Test
    void dispatchAfterCommit_disabled_skips()
    {
        ReflectionTestUtils.setField(dispatchService, "dispatchEnabled", false);
        ReflectionTestUtils.setField(dispatchService, "nextAppUrl", "http://localhost:3000");
        dispatchService.dispatchAfterCommit("demo", samplePayload());
    }

    @Test
    void dispatchAfterCommit_blankUrl_skips()
    {
        ReflectionTestUtils.setField(dispatchService, "dispatchEnabled", true);
        ReflectionTestUtils.setField(dispatchService, "nextAppUrl", "");
        dispatchService.dispatchAfterCommit("demo", samplePayload());
    }

    private static EventTriggerDispatchPayload samplePayload()
    {
        EventTriggerDispatchPayload payload = new EventTriggerDispatchPayload();
        payload.setPulseId("pulse-1");
        payload.setEventType("engagement.assessment.completed");
        payload.setSubjectType("Lead");
        payload.setSubjectId("lead-1");
        payload.setSubjectEntity("lead");
        payload.setOccurredAt("2026-07-21T10:30:00+08:00");
        return payload;
    }
}
