package com.ruoyi.crm.domain.vo;

/**
 * 分群预览结果
 */
public class SegmentPreviewVo
{
    private long count;

    public SegmentPreviewVo()
    {
    }

    public SegmentPreviewVo(long count)
    {
        this.count = count;
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
