package com.ruoyi.tenant.service;

import com.ruoyi.tenant.domain.SysWecomCorpTenant;

/**
 * 主库 Corp -> 租户映射
 */
public interface ISysWecomCorpTenantService
{
    /**
     * 根据 corpId 解析租户标识（回调切库）
     */
    SysWecomCorpTenant selectByCorpId(String corpId);

    /**
     * 保存配置时同步：先删该租户旧映射，再写入 corp -> tenant
     */
    void replaceMappingForTenant(String tenantId, String corpId);
}
