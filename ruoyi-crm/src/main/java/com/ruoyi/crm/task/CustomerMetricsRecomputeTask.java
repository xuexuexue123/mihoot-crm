package com.ruoyi.crm.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruoyi.crm.service.ICustomerMetricsService;

/**
 * Customer Metrics 全量重算定时任务
 * 每天凌晨 2:00 执行，防止增量漂移
 *
 * @author luohd
 * @date 2026-06-16
 */
@Component("customerMetricsRecomputeTask")
public class CustomerMetricsRecomputeTask
{
    private static final Logger log = LoggerFactory.getLogger(CustomerMetricsRecomputeTask.class);

    @Autowired
    private ICustomerMetricsService customerMetricsService;

    /**
     * 每天凌晨 2:00 全量重算 customer_metrics
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void recomputeAllMetrics()
    {
        log.info("CustomerMetrics full recompute task started");
        try
        {
            customerMetricsService.recomputeAll();
        }
        catch (Exception e)
        {
            log.error("CustomerMetrics full recompute failed", e);
        }
        log.info("CustomerMetrics full recompute task completed");
    }
}
