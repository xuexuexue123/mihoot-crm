package com.ruoyi.crm.domain.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.crm.domain.vo.EventNurtureStepVo;

class EventNurtureStepsSaveRequestTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializesWrappedSteps() throws Exception
    {
        String json = "{\"steps\":[{\"stepOrder\":1,\"actionType\":\"delay\",\"delaySeconds\":0,\"actionJson\":{}}]}";
        EventNurtureStepsSaveRequest request = mapper.readValue(json, EventNurtureStepsSaveRequest.class);
        assertNotNull(request.getSteps());
        assertEquals(1, request.getSteps().size());
        assertEquals("delay", request.getSteps().get(0).getActionType());
    }

    @Test
    void deserializesRawArray() throws Exception
    {
        String json = "[{\"stepOrder\":1,\"actionType\":\"delay\",\"delaySeconds\":300,\"actionJson\":{\"delaySeconds\":300}}]";
        EventNurtureStepsSaveRequest request = mapper.readValue(json, EventNurtureStepsSaveRequest.class);
        assertEquals(1, request.getSteps().size());
        assertEquals(300, request.getSteps().get(0).getDelaySeconds());
    }

    @Test
    void deserializesEmptyObjectAsEmptySteps() throws Exception
    {
        EventNurtureStepsSaveRequest request = mapper.readValue("{}", EventNurtureStepsSaveRequest.class);
        assertNotNull(request.getSteps());
        assertTrue(request.getSteps().isEmpty());
    }

    @Test
    void roundTripsStepVoFields() throws Exception
    {
        Map<String, Object> actionJson = new HashMap<>();
        actionJson.put("presetId", 12);
        EventNurtureStepVo step = new EventNurtureStepVo();
        step.setStepOrder(1);
        step.setActionType("mp_template");
        step.setDelaySeconds(0);
        step.setActionJson(actionJson);

        EventNurtureStepsSaveRequest wrapped = new EventNurtureStepsSaveRequest();
        wrapped.setSteps(java.util.Collections.singletonList(step));
        String json = mapper.writeValueAsString(wrapped);
        EventNurtureStepsSaveRequest parsed = mapper.readValue(json, EventNurtureStepsSaveRequest.class);
        assertEquals(12, parsed.getSteps().get(0).getActionJson().get("presetId"));
    }
}
