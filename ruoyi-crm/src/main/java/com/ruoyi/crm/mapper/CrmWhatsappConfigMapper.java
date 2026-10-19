package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWhatsappConfig;

public interface CrmWhatsappConfigMapper
{
    CrmWhatsappConfig selectByTenantId(@Param("tenantId") String tenantId);

    CrmWhatsappConfig selectByPhoneNumberId(@Param("phoneNumberId") String phoneNumberId);

    int insertOrUpdate(CrmWhatsappConfig row);
}
