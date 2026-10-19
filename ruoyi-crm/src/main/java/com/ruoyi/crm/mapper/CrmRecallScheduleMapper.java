package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmRecallSchedule;

public interface CrmRecallScheduleMapper
{
    CrmRecallSchedule selectByContactAndPolicy(@Param("tenantId") String tenantId,
            @Param("contactId") String contactId,
            @Param("policyId") String policyId);

    List<CrmRecallSchedule> selectList(@Param("tenantId") String tenantId,
            @Param("contactId") String contactId,
            @Param("status") String status);

    List<CrmRecallSchedule> selectDueScheduled(@Param("tenantId") String tenantId,
            @Param("limit") int limit);

    int insertCrmRecallSchedule(CrmRecallSchedule row);

    int upsertCrmRecallSchedule(CrmRecallSchedule row);

    int markFired(@Param("scheduleId") String scheduleId);
}
