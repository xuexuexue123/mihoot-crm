package com.ruoyi.crm.service.nurture.handler;

import org.springframework.stereotype.Component;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.service.nurture.support.EventNurtureStepResult;

@Component
public class DelayStepHandler
{
    public EventNurtureStepResult execute(CrmEventNurtureEnrollment enrollment, CrmEventNurtureStep step)
    {
        return EventNurtureStepResult.sent("delay:" + step.getStepOrder());
    }
}
