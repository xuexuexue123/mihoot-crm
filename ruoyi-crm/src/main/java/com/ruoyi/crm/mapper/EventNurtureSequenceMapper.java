package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEventNurtureSequence;

public interface EventNurtureSequenceMapper
{
    CrmEventNurtureSequence selectByEventId(@Param("tenantId") String tenantId,
            @Param("eventId") String eventId);

    CrmEventNurtureSequence selectEnabledByEventAndTrigger(@Param("tenantId") String tenantId,
            @Param("eventId") String eventId,
            @Param("triggerType") String triggerType);

    int insert(CrmEventNurtureSequence row);

    int update(CrmEventNurtureSequence row);

    int countSteps(@Param("sequenceId") Long sequenceId);
}
