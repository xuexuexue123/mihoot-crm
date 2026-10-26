package com.ruoyi.framework.datasource;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 注册并绑定租户数据源（线程级 lookup key）。
 * <p>
 * 登录、回调等路径在 {@code @DataSource(MASTER)} 切面结束后可能丢失租户 key，
 * 须在访问租户库 SQL 前显式调用 {@link #bindTenant(String)}。
 * </p>
 */
@Component
public class TenantDataSourceRegistry
{
    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    /**
     * 校验租户、按需注册连接池，并设置当前线程数据源 key。
     *
     * @throws RuntimeException 租户缺失、不存在、已禁用或已过期
     */
    public void bindTenant(String tenant)
    {
        if (StringUtils.isBlank(tenant))
        {
            throw new RuntimeException("缺少请求头 tenant，无法切换租户数据源");
        }
        ensureRegistered(tenant);
        DynamicDataSourceContextHolder.setDataSourceKey(tenant);
    }

    private void ensureRegistered(String tenant)
    {
        if (dynamicRoutingDataSource.existDataSource(tenant))
        {
            return;
        }
        MasterTenant masterTenant = masterTenantService.selectMasterTenant(tenant);
        if (masterTenant == null)
        {
            throw new RuntimeException("No such application:" + tenant);
        }
        if (TenantStatus.DISABLE.getCode().equals(masterTenant.getStatus()))
        {
            throw new RuntimeException("Application [" + tenant + "] expired");
        }
        if (masterTenant.getExpirationDate() != null
                && masterTenant.getExpirationDate().before(DateUtils.getNowDate()))
        {
            throw new RuntimeException("Application [" + tenant + "] expired");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("driverClassName", driverClassName);
        map.put("url", masterTenant.getUrl());
        map.put("username", masterTenant.getUsername());
        map.put("password", masterTenant.getPassword());
        dynamicRoutingDataSource.addDataSource(tenant, map);
        DynamicDataSourceContextHolder.setDataSourceKey(tenant);
    }
}
