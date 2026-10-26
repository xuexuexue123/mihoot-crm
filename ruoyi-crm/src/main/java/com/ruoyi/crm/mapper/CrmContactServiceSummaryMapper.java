package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmContactServiceSummary;

public interface CrmContactServiceSummaryMapper
{
    CrmContactServiceSummary selectByContactId(@Param("tenantId") String tenantId,
            @Param("contactId") String contactId);

    int insert(CrmContactServiceSummary row);

    int update(CrmContactServiceSummary row);

    int updateStatus(@Param("tenantId") String tenantId, @Param("contactId") String contactId,
            @Param("status") String status, @Param("errorMessage") String errorMessage);
}
