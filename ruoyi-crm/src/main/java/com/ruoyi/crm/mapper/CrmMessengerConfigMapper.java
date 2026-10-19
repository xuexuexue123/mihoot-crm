package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMessengerConfig;

public interface CrmMessengerConfigMapper
{
    CrmMessengerConfig selectByTenantId(@Param("tenantId") String tenantId);

    int insertOrUpdate(CrmMessengerConfig row);
}
