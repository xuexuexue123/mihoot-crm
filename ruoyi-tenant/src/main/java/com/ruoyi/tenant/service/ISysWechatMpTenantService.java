package com.ruoyi.tenant.service;

import com.ruoyi.tenant.domain.SysWechatMpTenant;

/**
 * 主库 appId -> 租户映射
 */
public interface ISysWechatMpTenantService
{
    /**
     * 根据 appId 解析租户标识（回调切库）
     */
    SysWechatMpTenant selectByAppId(String appId);

    /**
     * 删除指定租户下所有 appId 的旧映射
     */
    void deleteByTenant(String tenantId);

    /**
     * 保存配置时同步：先删该租户旧映射，再写入 appId -> tenant
     */
    void replaceMappingForTenant(String tenantId, String appId);
}
