package com.ruoyi.web.controller.crm.wechatmp;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.wechatmp.service.IWechatMpStatsService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpStatsController extends BaseController
{
    @Autowired
    private IWechatMpStatsService statsService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:config')")
    @GetMapping("/stats")
    public AjaxResult stats(
            @RequestParam String appId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime)
    {
        statsService.syncAccountInfo(appId);
        Map<String, Object> data = statsService.getStats(appId, beginTime, endTime);
        return AjaxResult.success(data);
    }
}
