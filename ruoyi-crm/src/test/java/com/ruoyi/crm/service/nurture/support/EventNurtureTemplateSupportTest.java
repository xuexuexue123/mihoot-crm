package com.ruoyi.crm.service.nurture.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.CrmEventNurtureSequence;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;

class EventNurtureTemplateSupportTest
{
    @Test
    void rendersPlaceholders()
    {
        CrmEvent event = new CrmEvent();
        event.setEventId("evt-1");
        event.setSubject("六月义诊");
        CrmEventNurtureEnrollment enrollment = new CrmEventNurtureEnrollment();
        enrollment.setDisplayName("张三");
        enrollment.setMobilePhone("13800138000");
        Map<String, String> vars = EventNurtureTemplateSupport.buildVars(event, enrollment, null);
        String subject = EventNurtureTemplateSupport.render("跟进：{{displayName}} / {{eventSubject}}", vars);
        assertEquals("跟进：张三 / 六月义诊", subject);
    }

    @Test
    void shouldExitWhenContactKnown()
    {
        CrmEventNurtureSequence sequence = new CrmEventNurtureSequence();
        sequence.setExitOnContact("1");
        EventNurtureAudienceSnapshot audience = new EventNurtureAudienceSnapshot();
        audience.setAudienceStatus("contact_known");
        assertTrue(isExitCandidate(sequence, audience));
    }

    @Test
    void shouldNotExitWhenChannelOnly()
    {
        CrmEventNurtureSequence sequence = new CrmEventNurtureSequence();
        sequence.setExitOnContact("1");
        EventNurtureAudienceSnapshot audience = new EventNurtureAudienceSnapshot();
        audience.setAudienceStatus("channel_only");
        assertFalse(isExitCandidate(sequence, audience));
    }

    private boolean isExitCandidate(CrmEventNurtureSequence sequence, EventNurtureAudienceSnapshot audience)
    {
        if (!sequence.isExitOnContactFlag())
        {
            return false;
        }
        String status = audience.getAudienceStatus();
        return "converted_contact".equals(status) || "contact_known".equals(status);
    }
}
