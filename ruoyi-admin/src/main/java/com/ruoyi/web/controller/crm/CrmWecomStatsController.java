package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsBackfillResult;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsStatusResponse;
import com.ruoyi.crm.service.ICrmWecomStatsInitService;
import com.ruoyi.crm.service.ICrmWecomStatsQueryService;
import com.ruoyi.crm.service.ICrmWecomStatsSnapshotService;

/**
 * 企业微信统计数据快照辅助接口（历史成员名册、初始化状态、历史回补）
 */
@RestController
@RequestMapping("/crm/wecom/stats")
public class CrmWecomStatsController extends BaseController
{
    private static final int MAX_BACKFILL_DAYS = 180;

    @Autowired
    private ICrmWecomStatsQueryService statsQueryService;

    @Autowired
    private ICrmWecomStatsSnapshotService statsSnapshotService;

    @Autowired
    private ICrmWecomStatsInitService statsInitService;

    /**
     * 历史成员名册（含已删除成员）
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @GetMapping("/members")
    public AjaxResult listMembers()
    {
        return AjaxResult.success(statsQueryService.listHistoricalMembers());
    }

    /**
     * 冷启动初始化状态（供前端轮询）
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @GetMapping("/status")
    public AjaxResult status()
    {
        return AjaxResult.success(statsInitService.getStatus());
    }

    /**
     * 历史回补（最多 180 天，仅当前仍存在成员）；force=true 时强制重跑
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @PostMapping("/backfill")
    public AjaxResult backfill(@RequestParam(defaultValue = "180") int days,
                               @RequestParam(defaultValue = "both") String dataset,
                               @RequestParam(defaultValue = "false") boolean force)
    {
        if (!force)
        {
            CrmWecomStatsStatusResponse status = statsInitService.getStatus();
            if (Boolean.TRUE.equals(status.getInitializing()))
            {
                return AjaxResult.success("初始化进行中", status);
            }
        }
        else
        {
            statsInitService.forceAcquireForManualBackfill();
        }

        int effectiveDays = Math.min(Math.max(days, 1), MAX_BACKFILL_DAYS);
        CrmWecomStatsBackfillResult result = statsSnapshotService.backfillTenant(effectiveDays, dataset);
        if (hasBlockingBackfillError(result))
        {
            statsInitService.failColdStart(result.getErrors().get(0));
            return AjaxResult.error(result.getErrors().get(0));
        }
        statsInitService.completeColdStart(result, effectiveDays);
        return AjaxResult.success("回补完成", result);
    }

    private static boolean hasBlockingBackfillError(CrmWecomStatsBackfillResult result)
    {
        if (result == null || result.getErrors().isEmpty())
        {
            return false;
        }
        for (String err : result.getErrors())
        {
            if (err.contains("未找到已连接") || err.contains("access_token"))
            {
                return true;
            }
        }
        return false;
    }
}
