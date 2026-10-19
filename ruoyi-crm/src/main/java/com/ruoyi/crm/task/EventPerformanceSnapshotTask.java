package com.ruoyi.crm.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.mapper.EventPerformanceMapper;
import com.ruoyi.crm.service.IEventPerformanceService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;

/**
 * 活动效果 ROI 快照日批任务
 */
@Component("eventPerformanceSnapshotTask")
public class EventPerformanceSnapshotTask
{
    private static final Logger log = LoggerFactory.getLogger(EventPerformanceSnapshotTask.class);

    @Autowired
    private EventPerformanceMapper eventPerformanceMapper;

    @Autowired
    private IEventPerformanceService eventPerformanceService;

    @Scheduled(cron = "0 30 2 * * ?")
    public void refreshAllSnapshots()
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isEmpty(tenantId))
        {
            log.warn("EventPerformance snapshot skipped: tenant datasource not bound");
            return;
        }
        log.info("EventPerformance snapshot task started for tenant={}", tenantId);
        try
        {
            List<String> eventIds = eventPerformanceMapper.selectAllEventIds();
            for (String eventId : eventIds)
            {
                eventPerformanceService.refreshSnapshot(tenantId, eventId);
            }
        }
        catch (Exception e)
        {
            log.error("EventPerformance snapshot task failed", e);
        }
    }
}
