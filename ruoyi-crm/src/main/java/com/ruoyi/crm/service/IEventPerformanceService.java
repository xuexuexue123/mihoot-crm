package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.EventPerformanceQuery;
import com.ruoyi.crm.domain.dto.EventPerformanceTrendsQuery;
import com.ruoyi.crm.domain.vo.EventPerformanceChannelRowVo;
import com.ruoyi.crm.domain.vo.EventPerformanceListRowVo;
import com.ruoyi.crm.domain.vo.EventPerformanceSummaryVo;
import com.ruoyi.crm.domain.vo.EventPerformanceTrendsVo;

/**
 * 活动效果与 ROI 聚合
 */
public interface IEventPerformanceService
{
    EventPerformanceSummaryVo summarize(String tenantId, String eventId, EventPerformanceQuery query);

    List<EventPerformanceChannelRowVo> listChannels(String tenantId, String eventId);

    List<EventPerformanceListRowVo> listPerformance(String tenantId, EventPerformanceQuery query);

    long countPerformanceList(String tenantId, EventPerformanceQuery query);

    EventPerformanceTrendsVo getTrends(String tenantId, String eventId, EventPerformanceTrendsQuery query);

    void refreshSnapshot(String tenantId, String eventId);
}
