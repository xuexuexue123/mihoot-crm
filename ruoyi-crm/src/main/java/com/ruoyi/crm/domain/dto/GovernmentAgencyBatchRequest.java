package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.CrmGovernmentAgency;

/**
 * 政府招商机构批量导入请求
 */
public class GovernmentAgencyBatchRequest
{
    private List<CrmGovernmentAgency> rows = new ArrayList<>();

    /** upsert | insert */
    private String mode = "upsert";

    public List<CrmGovernmentAgency> getRows()
    {
        return rows;
    }

    public void setRows(List<CrmGovernmentAgency> rows)
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
