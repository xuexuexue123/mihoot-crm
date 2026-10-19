package com.ruoyi.common.tenant;

/**
 * 解析当前请求的租户标识，供 Redis 等跨模块缓存 key 隔离使用。
 */
public interface TenantKeyProvider
{
    /**
     * @return 当前线程上下文中的 tenant key；无法解析时返回 {@code null}
     */
    String currentTenantKey();
}
