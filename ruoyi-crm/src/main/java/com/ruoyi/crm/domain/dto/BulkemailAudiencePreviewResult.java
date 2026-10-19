package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 受众 preview 响应
 */
public class BulkemailAudiencePreviewResult
{
    private long total;
    private long validEmailCount;
    private long invalidCount;
    private List<BulkemailAudiencePreviewRow> rows = new ArrayList<>();

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public long getValidEmailCount()
    {
        return validEmailCount;
    }

    public void setValidEmailCount(long validEmailCount)
    {
        this.validEmailCount = validEmailCount;
    }

    public long getInvalidCount()
    {
        return invalidCount;
    }

    public void setInvalidCount(long invalidCount)
    {
        this.invalidCount = invalidCount;
    }

    public List<BulkemailAudiencePreviewRow> getRows()
    {
        return rows;
    }

    public void setRows(List<BulkemailAudiencePreviewRow> rows)
    {
        this.rows = rows;
    }
}
