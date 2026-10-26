package com.ruoyi.tenant.service.impl;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.tenant.domain.SysMessengerPageTenant;
import com.ruoyi.tenant.mapper.SysMessengerPageTenantMapper;
import com.ruoyi.tenant.service.ISysMessengerPageTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysMessengerPageTenantServiceImpl implements ISysMessengerPageTenantService
{
    @Autowired
    private SysMessengerPageTenantMapper sysMessengerPageTenantMapper;

    @Override
    @DataSource(DataSourceType.MASTER)
    public SysMessengerPageTenant selectByPageId(String pageId)
    {
        return sysMessengerPageTenantMapper.selectByPageId(pageId);
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public java.util.List<SysMessengerPageTenant> selectAll()
    {
        return sysMessengerPageTenantMapper.selectAll();
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public void replaceMappingForTenant(String tenantId, String pageId)
    {
        sysMessengerPageTenantMapper.deleteByTenant(tenantId);
        SysMessengerPageTenant row = new SysMessengerPageTenant();
        row.setPageId(pageId);
        row.setTenant(tenantId);
        sysMessengerPageTenantMapper.insert(row);
    }
}
