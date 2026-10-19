package com.ruoyi.crm.service.support;

import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 显式绑定主库（megaems）数据源。resolve 等无 tenant header 的请求必须先查主库路由表。
 */
public final class MasterDataSourceSupport
{
    private MasterDataSourceSupport()
    {
    }

    public static void bindMasterKey()
    {
        DynamicDataSourceContextHolder.setDataSourceKey(DataSourceType.MASTER.name());
    }

    public static String currentKey()
    {
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }

    public static void restoreKey(String previousKey)
    {
        if (previousKey != null && !previousKey.isEmpty())
        {
            DynamicDataSourceContextHolder.setDataSourceKey(previousKey);
        }
        else
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }
}
