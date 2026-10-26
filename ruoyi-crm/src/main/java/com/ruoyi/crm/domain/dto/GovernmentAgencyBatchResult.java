package com.ruoyi.crm.domain.dto;

/**
 * 政府招商机构批量导入结果
 */
public class GovernmentAgencyBatchResult
{
    private int inserted;
    private int updated;
    private int skipped;

    public GovernmentAgencyBatchResult()
    {
    }

    public GovernmentAgencyBatchResult(int inserted, int updated, int skipped)
    {
        this.inserted = inserted;
        this.updated = updated;
        this.skipped = skipped;
    }

    public int getInserted()
    {
        return inserted;
    }

    public void setInserted(int inserted)
    {
        this.inserted = inserted;
    }

    public int getUpdated()
    {
        return updated;
    }

    public void setUpdated(int updated)
    {
        this.updated = updated;
    }

    public int getSkipped()
    {
        return skipped;
    }

    public void setSkipped(int skipped)
    {
        this.skipped = skipped;
    }
}
