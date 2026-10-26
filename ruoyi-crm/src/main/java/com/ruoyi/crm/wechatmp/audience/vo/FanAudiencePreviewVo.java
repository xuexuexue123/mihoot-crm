package com.ruoyi.crm.wechatmp.audience.vo;

import java.util.List;

public class FanAudiencePreviewVo
{
    private long count;
    private List<String> sampleOpenIds;

    public FanAudiencePreviewVo()
    {
    }

    public FanAudiencePreviewVo(long count, List<String> sampleOpenIds)
    {
        this.count = count;
        this.sampleOpenIds = sampleOpenIds;
    }

    public long getCount()
    {
        return count;
    }

    public void setCount(long count)
    {
        this.count = count;
    }

    public List<String> getSampleOpenIds()
    {
        return sampleOpenIds;
    }

    public void setSampleOpenIds(List<String> sampleOpenIds)
    {
        this.sampleOpenIds = sampleOpenIds;
    }
}
