package com.ruoyi.crm.service.nurture.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.service.nurture.IEventNurtureEnrollmentService;
import com.ruoyi.crm.service.nurture.IEventNurtureSequenceService;

@ExtendWith(MockitoExtension.class)
class EventNurtureHookSupportTest
{
    @Mock
    private com.ruoyi.crm.service.nurture.IEventNurtureEnrollmentService enrollmentService;

    @Mock
    private com.ruoyi.crm.service.nurture.IEventNurtureSequenceService sequenceService;

    @Mock
    private EventNurtureAudienceResolver audienceResolver;

    @InjectMocks
    private EventNurtureHookSupport hookSupport;

    @Test
    void shouldSkipLegacyWhenNurtureActive()
    {
        when(sequenceService.hasActiveMpSubscribeSequence("ten", "evt-1")).thenReturn(true);
        assertTrue(hookSupport.shouldSkipLegacyMpAutomation("ten", "evt-1"));
    }

    @Test
    void shouldNotSkipLegacyWhenNurtureInactive()
    {
        when(sequenceService.hasActiveMpSubscribeSequence("ten", "evt-1")).thenReturn(false);
        assertFalse(hookSupport.shouldSkipLegacyMpAutomation("ten", "evt-1"));
    }
}
