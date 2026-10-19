package com.ruoyi.crm.domain.vo;

/**
 * 标签使用量
 */
public class CrmTagUsageVo
{
    private String dictValue;
    private long contactCount;
    private long leadCount;

    public CrmTagUsageVo()
    {
    }

    public CrmTagUsageVo(String dictValue, long contactCount, long leadCount)
    {
        this.dictValue = dictValue;
        this.contactCount = contactCount;
        this.leadCount = leadCount;
    }

    public String getDictValue()
    {
        return dictValue;
    }

    public void setDictValue(String dictValue)
    {
        this.dictValue = dictValue;
    }

    public long getContactCount()
    {
        return contactCount;
    }

    public void setContactCount(long contactCount)
    {
        this.contactCount = contactCount;
    }

    public long getLeadCount()
    {
        return leadCount;
    }

    public void setLeadCount(long leadCount)
    {
        this.leadCount = leadCount;
    }
}
