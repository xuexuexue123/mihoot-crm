package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEventNurtureStepExecution;

public interface EventNurtureStepExecutionMapper
{
    CrmEventNurtureStepExecution selectByEnrollmentAndStep(@Param("enrollmentId") Long enrollmentId,
            @Param("stepId") Long stepId);

    int insert(CrmEventNurtureStepExecution row);

    int countRecentSentByOpenIdAndPreset(@Param("openId") String openId,
            @Param("presetId") Long presetId,
            @Param("since") java.util.Date since);
}
