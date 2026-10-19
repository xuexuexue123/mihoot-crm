package com.ruoyi.tenant.service.impl;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.tenant.domain.SysWebchatTokenTenant;
import com.ruoyi.tenant.mapper.SysWebchatTokenTenantMapper;
import com.ruoyi.tenant.service.ISysWebchatTokenTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysWebchatTokenTenantServiceImpl implements ISysWebchatTokenTenantService
{
    @Autowired
    private SysWebchatTokenTenantMapper sysWebchatTokenTenantMapper;

    @Override
    @DataSource(DataSourceType.MASTER)
    public SysWebchatTokenTenant selectByWebsiteToken(String websiteToken)
    {
        return sysWebchatTokenTenantMapper.selectByWebsiteToken(websiteToken);
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public SysWebchatTokenTenant selectByTenant(String tenant)
    {
        return sysWebchatTokenTenantMapper.selectByTenant(tenant);
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public java.util.List<SysWebchatTokenTenant> selectAll()
    {
        return sysWebchatTokenTenantMapper.selectAll();
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public void replaceMappingForTenant(String tenantId, String websiteToken)
    {
        sysWebchatTokenTenantMapper.deleteByTenant(tenantId);
        SysWebchatTokenTenant row = new SysWebchatTokenTenant();
        row.setWebsiteToken(websiteToken);
        row.setTenant(tenantId);
        sysWebchatTokenTenantMapper.insert(row);
    }
}
