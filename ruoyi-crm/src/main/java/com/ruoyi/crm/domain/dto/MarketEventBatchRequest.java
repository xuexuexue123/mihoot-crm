package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.CrmMarketEvent;

/**
 * 市场活动批量导入请求
 */
public class MarketEventBatchRequest
{
    private List<CrmMarketEvent> rows = new ArrayList<>();

    /** upsert | insert */
    private String mode = "upsert";

    public List<CrmMarketEvent> getRows()
    {
        return rows;
    }

    public void setRows(List<CrmMarketEvent> rows)
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
