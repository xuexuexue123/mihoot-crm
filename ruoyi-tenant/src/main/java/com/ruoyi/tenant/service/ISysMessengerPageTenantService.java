package com.ruoyi.tenant.service;

import com.ruoyi.tenant.domain.SysMessengerPageTenant;

import java.util.List;

/**
 * 主库 Messenger page_id -> 租户映射
 */
public interface ISysMessengerPageTenantService
{
    /**
     * 根据 page_id 解析租户标识（回调切库）
     */
    SysMessengerPageTenant selectByPageId(String pageId);

    /**
     * 获取所有 page_id → tenant 映射（用于 Webhook verify 遍历）
     */
    List<SysMessengerPageTenant> selectAll();

    /**
     * 保存配置时同步：先删该租户旧映射，再写入 page_id -> tenant
     */
    void replaceMappingForTenant(String tenantId, String pageId);
}
