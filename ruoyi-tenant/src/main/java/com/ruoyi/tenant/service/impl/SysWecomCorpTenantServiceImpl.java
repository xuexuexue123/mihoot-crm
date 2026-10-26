package com.ruoyi.tenant.service.impl;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.tenant.domain.SysWecomCorpTenant;
import com.ruoyi.tenant.mapper.SysWecomCorpTenantMapper;
import com.ruoyi.tenant.service.ISysWecomCorpTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysWecomCorpTenantServiceImpl implements ISysWecomCorpTenantService
{
    @Autowired
    private SysWecomCorpTenantMapper sysWecomCorpTenantMapper;

    @Override
    @DataSource(DataSourceType.MASTER)
    public SysWecomCorpTenant selectByCorpId(String corpId)
    {
        return sysWecomCorpTenantMapper.selectByCorpId(corpId);
    }

    @Override
    @DataSource(DataSourceType.MASTER)
    public void replaceMappingForTenant(String tenantId, String corpId)
    {
        sysWecomCorpTenantMapper.deleteByTenant(tenantId);
        SysWecomCorpTenant row = new SysWecomCorpTenant();
        row.setCorpId(corpId);
        row.setTenant(tenantId);
        sysWecomCorpTenantMapper.insert(row);
    }
}
