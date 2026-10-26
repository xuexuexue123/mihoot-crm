package com.ruoyi.web.controller.crm;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.CrmWecomCallbackEvent;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.IWecomCallbackEventService;

/**
 * 企业微信回调事件查询与统计
 */
@RestController
@RequestMapping("/crm/wecom/callback-events")
public class CrmWecomCallbackEventController extends BaseController
{
    @Autowired
    private IWecomCallbackEventService wecomCallbackEventService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(value = "event", required = false) String event,
            @RequestParam(value = "changeType", required = false) String changeType,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime)
    {
        String corpId = resolveCorpId();
        CrmWecomCallbackEvent query = new CrmWecomCallbackEvent();
        query.setCorpId(corpId);
        query.setEvent(event);
        query.setChangeType(changeType);
        if (beginTime != null && !beginTime.isEmpty())
        {
            query.getParams().put("beginTime", beginTime);
        }
        if (endTime != null && !endTime.isEmpty())
        {
            query.getParams().put("endTime", endTime);
        }
        startPage();
        return getDataTable(wecomCallbackEventService.selectList(query));
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/stats")
    public AjaxResult stats(
            @RequestParam(value = "event", required = false) String event,
            @RequestParam(value = "changeType", required = false) String changeType,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime)
    {
        String corpId = resolveCorpId();
        CrmWecomCallbackEvent query = new CrmWecomCallbackEvent();
        query.setCorpId(corpId);
        query.setEvent(event);
        query.setChangeType(changeType);
        if (beginTime != null && !beginTime.isEmpty())
        {
            query.getParams().put("beginTime", beginTime);
        }
        if (endTime != null && !endTime.isEmpty())
        {
            query.getParams().put("endTime", endTime);
        }
        List<Map<String, Object>> stats = wecomCallbackEventService.countByEventType(query);
        return AjaxResult.success(stats);
    }

    private String resolveCorpId()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || cfg.getCorpId() == null || cfg.getCorpId().isEmpty())
        {
            return "";
        }
        return cfg.getCorpId();
    }
}
