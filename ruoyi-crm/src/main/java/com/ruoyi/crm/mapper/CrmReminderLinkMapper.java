package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmReminderLink;

public interface CrmReminderLinkMapper
{
    int insert(CrmReminderLink row);

    CrmReminderLink selectByTenantAndToken(@Param("tenantId") String tenantId,
            @Param("token") String token);

    int revokeActiveByRef(@Param("tenantId") String tenantId,
            @Param("refType") String refType,
            @Param("refId") String refId);

    int markOpened(@Param("id") Long id, @Param("tenantId") String tenantId);

    int markExpired(@Param("id") Long id, @Param("tenantId") String tenantId);
}
