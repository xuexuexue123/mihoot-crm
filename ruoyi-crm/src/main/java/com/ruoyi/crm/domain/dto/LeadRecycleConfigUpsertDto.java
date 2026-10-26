package com.ruoyi.crm.domain.dto;

import com.ruoyi.crm.domain.vo.LeadRecycleConvertConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecycleDedupeConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecyclePoolConfigVo;

public class LeadRecycleConfigUpsertDto
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
