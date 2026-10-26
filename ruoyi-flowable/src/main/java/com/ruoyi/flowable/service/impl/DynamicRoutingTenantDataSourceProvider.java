package com.ruoyi.flowable.service.impl;

import javax.sql.DataSource;

import com.ruoyi.flowable.service.TenantDataSourceProvider;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.service.IMasterTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DynamicRoutingTenantDataSourceProvider implements TenantDataSourceProvider {

    @Autowired
    private IMasterTenantService masterTenantService;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    private final DynamicRoutingDataSource dynamicRoutingDataSource;


    public DynamicRoutingTenantDataSourceProvider(DynamicRoutingDataSource dynamicRoutingDataSource) {
        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
    }

    @Override
    public DataSource getDataSource(String tenantId) {
        if (!dynamicRoutingDataSource.existDataSource(tenantId)) {
            // 这里也可以抛异常或者返回null
            throw new IllegalArgumentException("DataSource for tenant " + tenantId + " not exists");
        }
        return dynamicRoutingDataSource.getDataSource(tenantId);
    }
}
