package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.CrmProfessionalService;

/**
 * 专业服务机构批量导入请求
 */
public class ProfessionalServiceBatchRequest
{
    private List<CrmProfessionalService> rows = new ArrayList<>();

    /** upsert | insert */
    private String mode = "upsert";

    public List<CrmProfessionalService> getRows()
    {
        return rows;
    }

    public void setRows(List<CrmProfessionalService> rows)
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
