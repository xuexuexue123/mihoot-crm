package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMobilePushDevice;

public interface CrmMobilePushDeviceMapper
{
    List<CrmMobilePushDevice> selectByUser(@Param("tenantId") String tenantId,
            @Param("userId") Long userId);

    CrmMobilePushDevice selectByToken(@Param("tenantId") String tenantId,
            @Param("pushToken") String pushToken);

    int upsert(CrmMobilePushDevice row);

    int deleteByToken(@Param("tenantId") String tenantId, @Param("userId") Long userId,
            @Param("pushToken") String pushToken);

    int deleteAllByUser(@Param("tenantId") String tenantId, @Param("userId") Long userId);

    int deleteByTokenOnly(@Param("tenantId") String tenantId, @Param("pushToken") String pushToken);
}
