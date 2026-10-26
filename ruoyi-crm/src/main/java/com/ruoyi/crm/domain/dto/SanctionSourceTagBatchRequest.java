package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.CrmSanctionSourceTag;

/**
 * 制裁源标签批量导入请求
 */
public class SanctionSourceTagBatchRequest
{
    private List<CrmSanctionSourceTag> rows = new ArrayList<>();

    /** upsert | insert */
    private String mode = "upsert";

    public List<CrmSanctionSourceTag> getRows()
    {
        return rows;
    }

    public void setRows(List<CrmSanctionSourceTag> rows)
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
