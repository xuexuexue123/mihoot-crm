package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;

public interface EventNurtureEnrollmentMapper
{
    CrmEventNurtureEnrollment selectById(@Param("id") Long id);

    CrmEventNurtureEnrollment selectBySequenceAndAudienceKey(@Param("tenantId") String tenantId,
            @Param("sequenceId") Long sequenceId,
            @Param("audienceKey") String audienceKey);

    List<CrmEventNurtureEnrollment> selectDue(@Param("tenantId") String tenantId,
            @Param("now") Date now,
            @Param("limit") int limit);

    int countActiveBySequenceId(@Param("sequenceId") Long sequenceId);

    int insert(CrmEventNurtureEnrollment row);

    int updateProgress(CrmEventNurtureEnrollment row);

    int markExited(CrmEventNurtureEnrollment row);

    int markCompleted(@Param("id") Long id);
}
