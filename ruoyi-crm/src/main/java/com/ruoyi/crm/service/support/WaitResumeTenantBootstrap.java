package com.ruoyi.crm.service.support;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 为 Wait Resume 绑定租户数据源。仅读取主库 {@code master_tenant} 注册连接信息，
 * 所有 {@code w_wait_resume} 读写必须在 bind 之后进行。
 */
@Component
public class WaitResumeTenantBootstrap
{
    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    /**
     * 注册（若尚未注册）并绑定 tenant 数据源。调用 {@link IMasterTenantService} 后主库切面会
     * clear 上下文，因此必须在末尾再次 bind。
     */
    public void bindTenantDataSource(String tenantId)
    {
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            MasterTenant masterTenant = masterTenantService.selectMasterTenant(tenantId);
            if (masterTenant == null)
            {
                throw new ServiceException("租户不存在: " + tenantId, HttpStatus.ERROR);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", masterTenant.getUrl());
            map.put("username", masterTenant.getUsername());
            map.put("password", masterTenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
        TenantDataSourceSupport.bindTenantKey(tenantId);
    }
}
