package com.ruoyi.tenant.service.impl;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.tenant.domain.SysWhatsappPhoneTenant;
import com.ruoyi.tenant.mapper.SysWhatsappPhoneTenantMapper;
import com.ruoyi.tenant.service.ISysWhatsappPhoneTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysWhatsappPhoneTenantServiceImpl implements ISysWhatsappPhoneTenantService
{
    @Autowired
    private SysWhatsappPhoneTenantMapper sysWhatsappPhoneTenantMapper;

    @Override
    @DataSource(DataSourceType.MASTER)
    public SysWhatsappPhoneTenant selectByPhoneNumberId(String phoneNumberId)
    {
        return sysWhatsappPhoneTenantMapper.selectByPhoneNumberId(phoneNumberId);
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public java.util.List<SysWhatsappPhoneTenant> selectAll()
    {
        return sysWhatsappPhoneTenantMapper.selectAll();
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public void replaceMappingForTenant(String tenantId, String phoneNumberId)
    {
        sysWhatsappPhoneTenantMapper.deleteByTenant(tenantId);
        SysWhatsappPhoneTenant row = new SysWhatsappPhoneTenant();
        row.setPhoneNumberId(phoneNumberId);
        row.setTenant(tenantId);
        sysWhatsappPhoneTenantMapper.insert(row);
    }
}
