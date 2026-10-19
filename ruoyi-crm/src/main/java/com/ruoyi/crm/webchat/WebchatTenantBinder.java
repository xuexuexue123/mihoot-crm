package com.ruoyi.crm.webchat;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.SysWebchatTokenTenant;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.tenant.service.ISysWebchatTokenTenantService;

/**
 * Resolve website_token → tenant and bind DynamicDataSource (clone Messenger webhook pattern).
 */
@Component
public class WebchatTenantBinder
{
    @Autowired
    private ISysWebchatTokenTenantService webchatTokenTenantService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    /**
     * @return tenantId
     */
    public String resolveAndBind(String websiteToken)
    {
        if (!StringUtils.hasText(websiteToken))
        {
            throw new ServiceException("token is required", HttpStatus.BAD_REQUEST);
        }
        SysWebchatTokenTenant mapping = webchatTokenTenantService.selectByWebsiteToken(websiteToken.trim());
        if (mapping == null || !StringUtils.hasText(mapping.getTenant()))
        {
            throw new ServiceException("invalid token", HttpStatus.FORBIDDEN);
        }
        String tenantId = mapping.getTenant().trim();
        bindTenantDataSource(tenantId);
        return tenantId;
    }

    public void bindTenantDataSource(String tenantId)
    {
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            MasterTenant masterTenant = masterTenantService.selectTenantByTenantId(tenantId);
            if (masterTenant == null)
            {
                throw new ServiceException("tenant not found", HttpStatus.NOT_FOUND);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", masterTenant.getUrl());
            map.put("username", masterTenant.getUsername());
            map.put("password", masterTenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
        DynamicDataSourceContextHolder.setDataSourceKey(tenantId);
    }

    public void clear()
    {
        DynamicDataSourceContextHolder.clearDataSourceKey();
    }
}
