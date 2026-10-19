package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmEventSpendItem;
import com.ruoyi.crm.domain.dto.EventPerformanceQuery;
import com.ruoyi.crm.domain.dto.EventPerformanceTrendsQuery;
import com.ruoyi.crm.domain.dto.EventSpendItemSaveRequest;
import com.ruoyi.crm.domain.vo.EventPerformanceChannelRowVo;
import com.ruoyi.crm.domain.vo.EventPerformanceListRowVo;
import com.ruoyi.crm.service.IEventPerformanceService;
import com.ruoyi.crm.service.IEventSpendItemService;

/**
 * 活动效果与 ROI 聚合
 */
@RestController
@RequestMapping("/crm/event")
public class CrmEventPerformanceController extends BaseController
{
    @Autowired
    private IEventPerformanceService eventPerformanceService;

    @Autowired
    private IEventSpendItemService eventSpendItemService;

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/performance/list")
    public TableDataInfo list(EventPerformanceQuery query,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<EventPerformanceListRowVo> rows = eventPerformanceService.listPerformance(tenant, query);
        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("success");
        data.setRows(rows);
        data.setTotal(eventPerformanceService.countPerformanceList(tenant, query));
        return data;
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/performance/summary")
    public AjaxResult summary(@PathVariable("eventId") String eventId,
            EventPerformanceQuery query,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(eventPerformanceService.summarize(tenant, eventId, query));
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/performance/channels")
    public AjaxResult channels(@PathVariable("eventId") String eventId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<EventPerformanceChannelRowVo> rows = eventPerformanceService.listChannels(tenant, eventId);
        return success(rows);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/performance/trends")
    public AjaxResult trends(@PathVariable("eventId") String eventId,
            EventPerformanceTrendsQuery query,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(eventPerformanceService.getTrends(tenant, eventId, query));
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/spend-items")
    public AjaxResult listSpendItems(@PathVariable("eventId") String eventId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<CrmEventSpendItem> items = eventSpendItemService.listByEvent(tenant, eventId);
        return success(items);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @PutMapping("/{eventId}/spend-items")
    public AjaxResult saveSpendItems(@PathVariable("eventId") String eventId,
            @RequestBody EventSpendItemSaveRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        eventSpendItemService.replaceSpendItems(tenant, eventId, request, SecurityUtils.getUsername());
        return success();
    }
}
