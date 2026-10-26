package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.vo.StandupAgendaItem;
import com.ruoyi.crm.domain.vo.StandupDailyVo;
import com.ruoyi.crm.domain.vo.StandupMemberDetailVo;
import com.ruoyi.crm.domain.vo.StandupRiskItem;
import com.ruoyi.crm.domain.vo.StandupWinItem;
import com.ruoyi.crm.service.ICrmStandupService;

/**
 * 销售晨会报告 Standup
 */
@RestController
@RequestMapping("/crm/standup")
public class CrmStandupController extends BaseController
{
    @Autowired
    private ICrmStandupService crmStandupService;

    /**
     * 一页晨会主包：昨日战报 + 成员表 + 月进度
     */
    @PreAuthorize("@ss.hasPermi('crm:standup:view')")
    @GetMapping("/daily")
    public AjaxResult daily(
            @RequestParam("date") String date,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "ownerId", required = false) String ownerId,
            @RequestParam(value = "includeKpi", required = false) Boolean includeKpi,
            @RequestParam(value = "kpiCodes", required = false) String kpiCodes)
    {
        StandupDailyVo data = crmStandupService.daily(date, deptId, ownerId, includeKpi, kpiCodes);
        return success(data);
    }

    /**
     * 今日作战清单（MVP：booking）
     */
    @PreAuthorize("@ss.hasPermi('crm:standup:view')")
    @GetMapping("/agenda")
    public TableDataInfo agenda(
            @RequestParam("date") String date,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "ownerId", required = false) String ownerId,
            @RequestParam(value = "includeCancelled", required = false) Boolean includeCancelled)
    {
        startPage();
        List<StandupAgendaItem> list = crmStandupService.agenda(date, type, deptId, ownerId, includeCancelled);
        return getDataTable(list);
    }

    /**
     * 风险名单
     */
    @PreAuthorize("@ss.hasPermi('crm:standup:view')")
    @GetMapping("/risks")
    public TableDataInfo risks(
            @RequestParam("date") String date,
            @RequestParam(value = "types", required = false) String types,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "ownerId", required = false) String ownerId)
    {
        // 多源合并后内存分页：避免 PageHelper 只截断首条 SQL
        List<StandupRiskItem> list = crmStandupService.risks(date, types, deptId, ownerId);
        return getDataTable(list);
    }

    /**
     * 昨日签约明细
     */
    @PreAuthorize("@ss.hasPermi('crm:standup:view')")
    @GetMapping("/wins")
    public TableDataInfo wins(
            @RequestParam("date") String date,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "ownerId", required = false) String ownerId)
    {
        startPage();
        List<StandupWinItem> list = crmStandupService.wins(date, deptId, ownerId);
        return getDataTable(list);
    }

    /**
     * 成员下钻明细
     */
    @PreAuthorize("@ss.hasPermi('crm:standup:view')")
    @GetMapping("/member/{ownerId}")
    public AjaxResult member(
            @PathVariable("ownerId") String ownerId,
            @RequestParam("date") String date)
    {
        StandupMemberDetailVo data = crmStandupService.memberDetail(ownerId, date);
        return success(data);
    }
}
