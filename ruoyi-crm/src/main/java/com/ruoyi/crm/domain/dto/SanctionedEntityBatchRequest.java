package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.CrmSanctionedEntity;

/**
 * 合规制裁主体批量导入请求
 */
public class SanctionedEntityBatchRequest
{
    private List<CrmSanctionedEntity> rows = new ArrayList<>();

    /** upsert | insert */
    private String mode = "upsert";

    public List<CrmSanctionedEntity> getRows()
    {
        return rows;
    }

    public void setRows(List<CrmSanctionedEntity> rows)
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
