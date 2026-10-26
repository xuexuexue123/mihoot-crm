package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmWecomGroupProvisionTask;

public interface CrmWecomGroupProvisionTaskMapper
{
    CrmWecomGroupProvisionTask selectByTaskId(@Param("taskId") String taskId);

    CrmWecomGroupProvisionTask selectPendingByContact(@Param("tenantId") String tenantId,
            @Param("contactId") String contactId);

    int insert(CrmWecomGroupProvisionTask row);

    int update(CrmWecomGroupProvisionTask row);
}
