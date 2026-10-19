package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.CrmTenantEnterprise;

/**
 * 入驻企业批量导入请求
 */
public class TenantEnterpriseBatchRequest
{
    private List<CrmTenantEnterprise> rows = new ArrayList<>();

    /** upsert | insert */
    private String mode = "upsert";

    public List<CrmTenantEnterprise> getRows()
    {
        return rows;
    }

    public void setRows(List<CrmTenantEnterprise> rows)
    {
        this.rows = rows != null ? rows : new ArrayList<>();
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }
}
