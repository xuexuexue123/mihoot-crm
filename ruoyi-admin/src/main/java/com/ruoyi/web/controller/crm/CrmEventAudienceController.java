package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.dto.EventAudienceQuery;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.service.IEventAudienceService;

/**
 * 活动受众聚合
 */
@RestController
@RequestMapping("/crm/event")
public class CrmEventAudienceController extends BaseController
{
    @Autowired
    private IEventAudienceService eventAudienceService;

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/audience/summary")
    public AjaxResult summary(@PathVariable("eventId") String eventId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(eventAudienceService.getSummary(tenant, eventId));
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/audience")
    public TableDataInfo list(@PathVariable("eventId") String eventId,
            EventAudienceQuery query,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        long total = eventAudienceService.countAudience(tenant, eventId, query);
        List<EventAudienceRowVo> rows = eventAudienceService.listAudience(tenant, eventId, query);
        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("success");
        data.setTotal(total);
        data.setRows(rows);
        return data;
    }
}
