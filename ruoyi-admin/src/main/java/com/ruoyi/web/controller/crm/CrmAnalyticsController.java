package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.AnalyticsQueryRequest;
import com.ruoyi.crm.domain.vo.AnalyticsQueryResponse;
import com.ruoyi.crm.service.ICrmAnalyticsQueryService;

/**
 * CRM 聚合指标（AI Assistant analytics tool）
 */
@RestController
@RequestMapping("/crm/analytics")
public class CrmAnalyticsController extends BaseController
{
    @Autowired
    private ICrmAnalyticsQueryService crmAnalyticsQueryService;

    @PreAuthorize("@ss.hasPermi('crm:analytics:query')")
    @Log(title = "CRM Analytics", businessType = BusinessType.OTHER)
    @PostMapping("/query")
    public AjaxResult query(@RequestBody AnalyticsQueryRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        AnalyticsQueryResponse data = crmAnalyticsQueryService.query(tenant, body);
        return success(data);
    }
}
