package com.ruoyi.crm.wechatmp.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;

/**
 * 公众号粉丝操作时间线单项
 */
public class WechatMpFanTimelineItem
{
    /** activity | unmatched */
    private String source;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime sortTime;

    private CrmActivity activity;

    private ActivityUnmatched unmatched;

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public OffsetDateTime getSortTime()
    {
        return sortTime;
    }

    public void setSortTime(OffsetDateTime sortTime)
    {
        this.sortTime = sortTime;
    }

    public CrmActivity getActivity()
    {
        return activity;
    }

    public void setActivity(CrmActivity activity)
    {
        this.activity = activity;
    }

    public ActivityUnmatched getUnmatched()
    {
        return unmatched;
    }

    public void setUnmatched(ActivityUnmatched unmatched)
    {
        this.unmatched = unmatched;
    }
}
