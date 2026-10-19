package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.CrmChineseEnterprise;

/**
 * 中资企业批量导入请求
 */
public class ChineseEnterpriseBatchRequest
{
    private List<CrmChineseEnterprise> rows = new ArrayList<>();

    /** upsert | insert */
    private String mode = "upsert";

    public List<CrmChineseEnterprise> getRows()
    {
        return rows;
    }

    public void setRows(List<CrmChineseEnterprise> rows)
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
