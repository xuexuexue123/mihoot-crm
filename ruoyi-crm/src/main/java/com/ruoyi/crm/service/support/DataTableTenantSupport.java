package com.ruoyi.crm.service.support;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmDataTable;
import com.ruoyi.crm.mapper.CrmDataTableMapper;

/**
 * Data Table 租户与表归属校验
 */
public final class DataTableTenantSupport
{
    private DataTableTenantSupport()
    {
    }

    public static String requireTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
        }
        return tenantId.trim();
    }

    public static CrmDataTable requireTable(CrmDataTableMapper mapper, String tableId, String tenantId)
    {
        if (StringUtils.isBlank(tableId))
        {
            throw new ServiceException("tableId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmDataTable table = mapper.selectByIdAndTenant(tableId, tenantId);
        if (table == null)
        {
            throw new ServiceException("Data table not found", HttpStatus.NOT_FOUND);
        }
        return table;
    }
}
