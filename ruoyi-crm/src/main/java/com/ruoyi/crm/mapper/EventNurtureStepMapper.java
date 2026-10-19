package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEventNurtureStep;

public interface EventNurtureStepMapper
{
    List<CrmEventNurtureStep> selectBySequenceId(@Param("sequenceId") Long sequenceId);

    CrmEventNurtureStep selectBySequenceAndOrder(@Param("sequenceId") Long sequenceId,
            @Param("stepOrder") Integer stepOrder);

    int deleteBySequenceId(@Param("sequenceId") Long sequenceId);

    int insertBatch(@Param("steps") List<CrmEventNurtureStep> steps);
}
