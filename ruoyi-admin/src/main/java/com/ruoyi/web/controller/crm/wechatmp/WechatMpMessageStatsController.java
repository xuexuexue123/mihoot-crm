package com.ruoyi.web.controller.crm.wechatmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessageStatsService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpMessageStatsController extends BaseController
{
    @Autowired
    private IWechatMpMessageStatsService messageStatsService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/stats/message/upstream")
    public AjaxResult upstream(
            @RequestParam String appId,
            @RequestParam String beginDate,
            @RequestParam String endDate)
    {
        return messageStatsService.getMessageStats(appId, beginDate, endDate, "getupstreammsg");
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/stats/message/upstream-week")
    public AjaxResult upstreamWeek(
            @RequestParam String appId,
            @RequestParam String beginDate,
            @RequestParam String endDate)
    {
        return messageStatsService.getMessageStats(appId, beginDate, endDate, "getupstreammsgweek");
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/stats/message/upstream-month")
    public AjaxResult upstreamMonth(
            @RequestParam String appId,
            @RequestParam String beginDate,
            @RequestParam String endDate)
    {
        return messageStatsService.getMessageStats(appId, beginDate, endDate, "getupstreammsgmonth");
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/stats/message/upstream-hour")
    public AjaxResult upstreamHour(
            @RequestParam String appId,
            @RequestParam String beginDate,
            @RequestParam String endDate)
    {
        return messageStatsService.getMessageStats(appId, beginDate, endDate, "getupstreammsghour");
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/stats/message/upstream-dist")
    public AjaxResult upstreamDist(
            @RequestParam String appId,
            @RequestParam String beginDate,
            @RequestParam String endDate)
    {
        return messageStatsService.getMessageStats(appId, beginDate, endDate, "getupstreammsgdist");
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/stats/message/upstream-dist-week")
    public AjaxResult upstreamDistWeek(
            @RequestParam String appId,
            @RequestParam String beginDate,
            @RequestParam String endDate)
    {
        return messageStatsService.getMessageStats(appId, beginDate, endDate, "getupstreammsgdistweek");
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/stats/message/upstream-dist-month")
    public AjaxResult upstreamDistMonth(
            @RequestParam String appId,
            @RequestParam String beginDate,
            @RequestParam String endDate)
    {
        return messageStatsService.getMessageStats(appId, beginDate, endDate, "getupstreammsgdistmonth");
    }
}
