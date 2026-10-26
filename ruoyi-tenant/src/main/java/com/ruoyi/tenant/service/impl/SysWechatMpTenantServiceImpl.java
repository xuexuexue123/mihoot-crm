package com.ruoyi.tenant.service.impl;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.tenant.domain.SysWechatMpTenant;
import com.ruoyi.tenant.mapper.SysWechatMpTenantMapper;
import com.ruoyi.tenant.service.ISysWechatMpTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysWechatMpTenantServiceImpl implements ISysWechatMpTenantService
{
    @Autowired
    private SysWechatMpTenantMapper sysWechatMpTenantMapper;

    @Override
    @DataSource(DataSourceType.MASTER)
    public SysWechatMpTenant selectByAppId(String appId)
    {
        return sysWechatMpTenantMapper.selectByAppId(appId);
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public void deleteByTenant(String tenantId)
    {
        sysWechatMpTenantMapper.deleteByTenant(tenantId);
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public void replaceMappingForTenant(String tenantId, String appId)
    {
        sysWechatMpTenantMapper.deleteByTenant(tenantId);
        SysWechatMpTenant row = new SysWechatMpTenant();
        row.setAppId(appId);
        row.setTenant(tenantId);
        sysWechatMpTenantMapper.insert(row);
    }
}
