package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.dto.CrmLeadStatsQuery;
import com.ruoyi.crm.service.ICrmLeadStatsService;

@RestController
@RequestMapping("/crm/lead/stats")
public class CrmLeadStatsController extends BaseController
{
    @Autowired
    private ICrmLeadStatsService leadStatsService;

    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/summary")
    public AjaxResult summary(CrmLeadStatsQuery query)
    {
        return success(leadStatsService.summary(query));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/funnel")
    public AjaxResult funnel(CrmLeadStatsQuery query)
    {
        return success(leadStatsService.funnel(query));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/conversion-by-owner")
    public AjaxResult conversionByOwner(CrmLeadStatsQuery query)
    {
        return success(leadStatsService.conversionByOwner(query));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/trend")
    public AjaxResult trend(CrmLeadStatsQuery query)
    {
        return success(leadStatsService.trend(query));
    }
}
