package com.ruoyi.crm.domain.vo;

public class LeadRecycleConfigVo
{
    private LeadRecyclePoolConfigVo pool;
    private LeadRecycleDedupeConfigVo dedupe;
    private LeadRecycleConvertConfigVo convert;

    public LeadRecyclePoolConfigVo getPool()
    {
        return pool;
    }

    public void setPool(LeadRecyclePoolConfigVo pool)
    {
        this.pool = pool;
    }

    public LeadRecycleDedupeConfigVo getDedupe()
    {
        return dedupe;
    }

    public void setDedupe(LeadRecycleDedupeConfigVo dedupe)
    {
        this.dedupe = dedupe;
    }

    public LeadRecycleConvertConfigVo getConvert()
    {
        return convert;
    }

    public void setConvert(LeadRecycleConvertConfigVo convert)
    {
        this.convert = convert;
    }
}
