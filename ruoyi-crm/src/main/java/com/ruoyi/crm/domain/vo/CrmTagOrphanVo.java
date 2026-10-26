package com.ruoyi.crm.domain.vo;

/**
 * 非词表 orphan 标签
 */
public class CrmTagOrphanVo
{
    private String id;
    private String text;
    private long count;

    public CrmTagOrphanVo()
    {
    }

    public CrmTagOrphanVo(String id, String text, long count)
    {
        this.id = id;
        this.text = text;
        this.count = count;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public long getCount()
    {
        return count;
    }

    public void setCount(long count)
    {
        this.count = count;
    }
}
