package com.ruoyi.tenant.service;

import com.ruoyi.tenant.domain.SysWebchatTokenTenant;

import java.util.List;

/**
 * 主库 Web Chat website_token -> 租户映射
 */
public interface ISysWebchatTokenTenantService
{
    SysWebchatTokenTenant selectByWebsiteToken(String websiteToken);

    SysWebchatTokenTenant selectByTenant(String tenant);

    List<SysWebchatTokenTenant> selectAll();

    /**
     * 保存/轮换配置时同步：先删该租户旧映射，再写入 website_token -> tenant
     */
    void replaceMappingForTenant(String tenantId, String websiteToken);
}
