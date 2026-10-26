package com.ruoyi.crm.service.nurture;

import java.util.List;
import com.ruoyi.crm.domain.dto.EventNurtureSequenceSaveRequest;
import com.ruoyi.crm.domain.dto.EventNurtureStepsSaveRequest;
import com.ruoyi.crm.domain.vo.EventNurtureSequenceVo;
import com.ruoyi.crm.domain.vo.EventNurtureStepVo;

public interface IEventNurtureSequenceService
{
    EventNurtureSequenceVo getSequence(String tenantId, String eventId);

    EventNurtureSequenceVo saveSequence(String tenantId, String eventId,
            EventNurtureSequenceSaveRequest request, String operator);

    List<EventNurtureStepVo> listSteps(String tenantId, String eventId);

    List<EventNurtureStepVo> replaceSteps(String tenantId, String eventId,
            EventNurtureStepsSaveRequest request);

    boolean hasActiveMpSubscribeSequence(String tenantId, String eventId);
}
