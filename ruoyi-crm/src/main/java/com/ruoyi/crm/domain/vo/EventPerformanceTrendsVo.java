package com.ruoyi.crm.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动效果趋势响应
 */
public class EventPerformanceTrendsVo
{
    private String granularity;
    private List<EventPerformanceTrendPointVo> series = new ArrayList<>();

    public String getGranularity() { return granularity; }
    public void setGranularity(String granularity) { this.granularity = granularity; }
    public List<EventPerformanceTrendPointVo> getSeries() { return series; }
    public void setSeries(List<EventPerformanceTrendPointVo> series) { this.series = series; }
}
