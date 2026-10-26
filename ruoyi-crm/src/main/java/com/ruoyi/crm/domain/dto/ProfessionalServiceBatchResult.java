package com.ruoyi.crm.domain.dto;

/**
 * 专业服务机构批量导入结果
 */
public class ProfessionalServiceBatchResult
{
    private int inserted;
    private int updated;
    private int skipped;

    public ProfessionalServiceBatchResult()
    {
    }

    public ProfessionalServiceBatchResult(int inserted, int updated, int skipped)
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
