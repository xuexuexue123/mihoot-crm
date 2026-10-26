package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;

@ExtendWith(MockitoExtension.class)
public class WecomStateParserTest
{
    @Mock
    private CrmEventMapper crmEventMapper;

    @InjectMocks
    private WecomStateParser parser;

    private static final String FULL_EVENT_ID = "a814999a-e5f6-7890-abcd-ef999a8eff4";

    @BeforeEach
    public void setUp()
    {
        when(crmEventMapper.selectCrmEventByEventId(anyString())).thenReturn(null);
    }

    @Test
    public void parsesShortEvtSuffix()
    {
        when(crmEventMapper.selectEventIdsByCompactSuffix("814999a8eff4"))
            .thenReturn(Collections.singletonList(FULL_EVENT_ID));
        WecomStateParseResult result = parser.parse("EVT:814999a8eff4");
        assertEquals(FULL_EVENT_ID, result.getCrmEventId());
        assertEquals("814999a8eff4", result.getEventSuffix());
        assertNull(result.getChannel());
        assertEquals("wecom", result.getLeadSource());
    }

    @Test
    public void parsesMultiChannelState()
    {
        when(crmEventMapper.selectEventIdsByCompactSuffix("814999a8eff4"))
            .thenReturn(Collections.singletonList(FULL_EVENT_ID));
        WecomStateParseResult result = parser.parse("EVT:814999a8eff4:poster");
        assertEquals(FULL_EVENT_ID, result.getCrmEventId());
        assertEquals("poster", result.getChannel());
        assertEquals("wecom:poster", result.getLeadSource());
        assertEquals("814999a8eff4", result.getEventSuffix());
    }

    @Test
    public void rejectsAmbiguousSuffixMatch()
    {
        when(crmEventMapper.selectEventIdsByCompactSuffix("999a8eff4"))
            .thenReturn(Arrays.asList(FULL_EVENT_ID, "b814999a-e5f6-7890-abcd-ef999a8eff5"));
        WecomStateParseResult result = parser.parse("EVT:999a8eff4");
        assertNull(result.getCrmEventId());
        assertEquals("999a8eff4", result.getEventSuffix());
    }

    @Test
    public void parsesFullUuidInEvtPrefix()
    {
        CrmEvent event = new CrmEvent();
        event.setEventId(FULL_EVENT_ID);
        when(crmEventMapper.selectCrmEventByEventId(FULL_EVENT_ID)).thenReturn(event);
        WecomStateParseResult result = parser.parse("EVT:" + FULL_EVENT_ID);
        assertEquals(FULL_EVENT_ID, result.getCrmEventId());
    }

    @Test
    public void returnsNullForUnknownState()
    {
        when(crmEventMapper.selectEventIdsByCompactSuffix("deadbeef0000"))
            .thenReturn(Collections.emptyList());
        WecomStateParseResult result = parser.parse("EVT:deadbeef0000");
        assertNull(result.getCrmEventId());
    }

    @Test
    public void parseEventIdCompatibility()
    {
        when(crmEventMapper.selectEventIdsByCompactSuffix("814999a8eff4"))
            .thenReturn(Collections.singletonList(FULL_EVENT_ID));
        assertEquals(FULL_EVENT_ID, parser.parseEventId("EVT:814999a8eff4:live"));
    }
}
