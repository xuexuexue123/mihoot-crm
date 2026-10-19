package com.ruoyi.crm.service.nurture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.enums.EventNurtureActionType;
import com.ruoyi.crm.service.nurture.handler.CreateTaskStepHandler;
import com.ruoyi.crm.service.nurture.handler.DelayStepHandler;
import com.ruoyi.crm.service.nurture.handler.MpTemplateStepHandler;
import com.ruoyi.crm.service.nurture.support.EventNurtureStepResult;

@Component
public class EventNurtureStepExecutor
{
    @Autowired
    private DelayStepHandler delayStepHandler;

    @Autowired
    private MpTemplateStepHandler mpTemplateStepHandler;

    @Autowired
    private CreateTaskStepHandler createTaskStepHandler;

    public EventNurtureStepResult execute(String tenantId, CrmEventNurtureEnrollment enrollment,
            CrmEventNurtureStep step, EventNurtureAudienceSnapshot audience)
    {
        EventNurtureActionType actionType = EventNurtureActionType.fromCode(step.getActionType());
        if (actionType == null)
        {
            return EventNurtureStepResult.failed("unknown_action", false);
        }
        switch (actionType)
        {
            case DELAY:
                return delayStepHandler.execute(enrollment, step);
            case MP_TEMPLATE:
                return mpTemplateStepHandler.execute(tenantId, enrollment, step, audience);
            case CREATE_TASK:
                return createTaskStepHandler.execute(enrollment, step, audience);
            default:
                return EventNurtureStepResult.skipped("unsupported_action");
        }
    }
}
