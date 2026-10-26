package com.ruoyi.crm.service.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 无 TenantInterceptor 的 MCP 请求（validate / audit）手动绑定租户数据源
 */
@Component
public class McpApiKeyTenantResolver
{
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private McpTenantCode mcpTenantCode;

    /**
     * 通过 key 中的 tenantCode 反查真实 tenantId（遍历活跃租户 HMAC 比对）
     */
    public String resolveByTenantCode(String tenantCode)
    {
        if (StringUtils.isBlank(tenantCode))
        {
            return null;
        }
        MasterTenant query = new MasterTenant();
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(query);
        for (MasterTenant tenant : tenants)
        {
            if (!isActiveTenant(tenant))
            {
                continue;
            }
            try
            {
                if (tenantCode.equals(mcpTenantCode.compute(tenant.getTenant())))
                {
                    return tenant.getTenant();
                }
            }
            catch (Exception ignored)
            {
                // skip tenants that fail HMAC compute
            }
        }
        return null;
    }

    /**
     * 绑定租户数据源（validate / audit 等无 TenantInterceptor 的请求）
     */
    public void bindTenant(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new IllegalArgumentException("tenantId is blank");
        }
        MasterTenant masterTenant = masterTenantService.selectMasterTenant(tenantId);
        if (masterTenant == null || !isActiveTenant(masterTenant))
        {
            throw new IllegalArgumentException("tenant not available: " + tenantId);
        }
        ensureTenantDataSourceReady(masterTenant);
        DynamicDataSourceContextHolder.setDataSourceKey(tenantId);
    }

    public void clearTenant()
    {
        DynamicDataSourceContextHolder.clearDataSourceKey();
    }

    private boolean isActiveTenant(MasterTenant tenant)
    {
        if (tenant == null || StringUtils.isBlank(tenant.getTenant()))
        {
            return false;
        }
        if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus()))
        {
            return false;
        }
        return tenant.getExpirationDate() == null || !tenant.getExpirationDate().before(DateUtils.getNowDate());
    }

    private void ensureTenantDataSourceReady(MasterTenant tenant)
    {
        String tenantId = tenant.getTenant();
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", tenant.getUrl());
            map.put("username", tenant.getUsername());
            map.put("password", tenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
    }
}
