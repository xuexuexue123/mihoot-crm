package com.ruoyi.framework.tenant;

import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.tenant.TenantKeyProvider;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 从动态数据源上下文或登录用户解析 tenant key。
 */
@Component
public class DynamicDataSourceTenantKeyProvider implements TenantKeyProvider
{
    @Override
    public String currentTenantKey()
    {
        String key = DynamicDataSourceContextHolder.getDataSourceKey();
        if (isTenantKey(key))
        {
            return key;
        }
        return tenantFromLoginUser();
    }

    private static boolean isTenantKey(String key)
    {
        return StringUtils.isNotBlank(key)
                && !DataSourceType.MASTER.name().equals(key)
                && !"default_db".equals(key);
    }

    private static String tenantFromLoginUser()
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null)
            {
                return null;
            }
            Long userId = loginUser.getUserId();
            if (userId != null && (userId.longValue() == 99999L || userId.longValue() == 0L))
            {
                return null;
            }
            return loginUser.getTenant();
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
