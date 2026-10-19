package com.ruoyi.tenant.mapper;

import com.ruoyi.tenant.domain.SysWhatsappPhoneTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysWhatsappPhoneTenantMapper
{
    SysWhatsappPhoneTenant selectByPhoneNumberId(@Param("phoneNumberId") String phoneNumberId);

    List<SysWhatsappPhoneTenant> selectAll();

    int deleteByTenant(@Param("tenant") String tenant);

    int insert(SysWhatsappPhoneTenant row);
}
