package com.ruoyi.crm.service.nurture;

import com.ruoyi.crm.domain.CrmEventNurtureSequence;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.wechatmp.automation.EventMpOutreachContext;

public interface IEventNurtureEnrollmentService
{
    void tryEnroll(String tenantId, String eventId, String triggerType,
            EventNurtureAudienceSnapshot audience);

    void tryEnrollFromMpContext(EventMpOutreachContext ctx);

    void tryEnrollFromLeadRelease(String tenantId, String eventId, String leadId);

    boolean shouldExit(CrmEventNurtureSequence sequence, EventNurtureAudienceSnapshot audience);

    void exitEnrollment(Long enrollmentId, String reason);

    void processDueEnrollment(Long enrollmentId, String tenantKey);
}
