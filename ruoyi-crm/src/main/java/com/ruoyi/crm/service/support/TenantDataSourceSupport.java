package com.ruoyi.crm.service.support;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * {@link com.ruoyi.common.annotation.DataSource} 切面在方法结束后会 clear 数据源，
 * 从 MASTER 返回后需恢复租户 key，否则后续 SQL 会打到主库。
 */
public final class TenantDataSourceSupport
{
    private TenantDataSourceSupport()
    {
    }

    public static String currentTenantKey()
    {
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }

    public static void bindTenantKey(String tenantKey)
    {
        if (StringUtils.isBlank(tenantKey))
        {
            throw new ServiceException("tenant 不能为空", HttpStatus.BAD_REQUEST);
        }
        DynamicDataSourceContextHolder.setDataSourceKey(tenantKey);
    }

    public static void requireTenantKey()
    {
        if (StringUtils.isBlank(currentTenantKey()))
        {
            throw new ServiceException("tenant 数据源未设置，w_wait_resume 只能在租户库访问", HttpStatus.ERROR);
        }
    }

    public static void restoreTenantKey(String tenantKey)
    {
        if (StringUtils.isNotBlank(tenantKey))
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenantKey);
        }
    }
}
