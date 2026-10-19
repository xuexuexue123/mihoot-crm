package com.ruoyi.crm.mapper;

import java.util.Date;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmPatientAutomationLog;

public interface CrmPatientAutomationLogMapper
{
    int insertIfNotExists(CrmPatientAutomationLog row);

    int existsByContactKeyDue(@Param("contactId") String contactId,
            @Param("automationKey") String automationKey, @Param("dueDate") Date dueDate);
}
