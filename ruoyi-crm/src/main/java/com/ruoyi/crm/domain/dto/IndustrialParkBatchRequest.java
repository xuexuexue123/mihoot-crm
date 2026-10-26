package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.CrmIndustrialPark;

/**
 * 工业园区批量导入请求
 */
public class IndustrialParkBatchRequest
{
    private List<CrmIndustrialPark> rows = new ArrayList<>();

    /** upsert | insert */
    private String mode = "upsert";

    public List<CrmIndustrialPark> getRows()
    {
        return rows;
    }

    public void setRows(List<CrmIndustrialPark> rows)
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
