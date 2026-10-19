package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.CustomerMetrics;
import com.ruoyi.crm.service.ICustomerMetricsService;

/**
 * Activity Metrics Controller — 客户聚合指标查询
 *
 * @author luohd
 * @date 2026-06-16
 */
@RestController
@RequestMapping("/crm/activity")
public class CrmActivityMetricsController extends BaseController
{
    @Autowired
    private ICustomerMetricsService customerMetricsService;

    /**
     * 获取客户的聚合指标快照
     *
     * @param contactId 客户ID
     * @return CustomerMetrics 聚合指标
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:list')")
    @GetMapping("/metrics/{contactId}")
    public AjaxResult getMetrics(@PathVariable("contactId") String contactId)
    {
        CustomerMetrics metrics = customerMetricsService.getMetrics(contactId);
        if (metrics == null)
        {
            metrics = new CustomerMetrics();
            metrics.setContactId(contactId);
        }
        return success(metrics);
    }

    /**
     * 手动触发全量重算 customer_metrics
     * 首次上线后建议先手动执行一次，而不是等到凌晨 2:00
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:list')")
    @PostMapping("/metrics/recompute")
    public AjaxResult recomputeMetrics()
    {
        customerMetricsService.recomputeAll();
        return success();
    }
}
