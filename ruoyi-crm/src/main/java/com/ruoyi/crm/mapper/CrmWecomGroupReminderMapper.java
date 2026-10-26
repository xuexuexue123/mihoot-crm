package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmWecomGroupReminder;

public interface CrmWecomGroupReminderMapper
{
    CrmWecomGroupReminder selectById(@Param("id") String id);

    CrmWecomGroupReminder selectByUnique(@Param("tenantId") String tenantId, @Param("contactId") String contactId,
            @Param("ruleId") String ruleId, @Param("sourceRef") String sourceRef);

    List<CrmWecomGroupReminder> selectList(CrmWecomGroupReminder query);

    List<CrmWecomGroupReminder> selectDuePending(@Param("tenantId") String tenantId, @Param("now") Date now,
            @Param("limit") int limit);

    int insert(CrmWecomGroupReminder row);

    int upsertPending(CrmWecomGroupReminder row);

    int update(CrmWecomGroupReminder row);

    int cancelBySourceRef(@Param("tenantId") String tenantId, @Param("sourceRef") String sourceRef);

    int cancelPendingByContact(@Param("tenantId") String tenantId, @Param("contactId") String contactId);

    int claimLease(@Param("id") String id, @Param("leaseUntil") Date leaseUntil, @Param("now") Date now);
}
