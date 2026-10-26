package com.ruoyi.web.controller.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.crm.domain.CrmSalesgoal;
import com.ruoyi.crm.domain.dto.SalesgoalAssessBatchRequest;
import com.ruoyi.crm.domain.vo.CrmSalesgoalListVo;
import com.ruoyi.crm.service.ICrmSalesgoalService;

/**
 * 销售目标 / KPI 目标管理 Controller
 *
 * @author luohd
 * @date 2024-01-19
 */
@RestController
@RequestMapping("/crm/salesgoal")
public class CrmSalesgoalController extends BaseController
{
    @Autowired
    private ICrmSalesgoalService crmSalesgoalService;

    /**
     * 查询销售目标列表（含 actualValue / completionRate）
     */
    @PreAuthorize("@ss.hasPermi('crm:salesgoal:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmSalesgoal crmSalesgoal)
    {
        startPage();
        List<CrmSalesgoalListVo> list = crmSalesgoalService.selectCrmSalesgoalListWithActual(crmSalesgoal);
        return getDataTable(list);
    }

    /**
     * 查询当前用户销售目标
     */
    @PreAuthorize("@ss.hasPermi('crm:salesgoal:list')")
    @GetMapping("/mysalesgoal")
    public TableDataInfo mysalesgoal(CrmSalesgoal crmSalesgoal)
    {
        startPage();
        List<CrmSalesgoal> list = crmSalesgoalService.selectMyCrmSalesgoal(crmSalesgoal);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:salesgoal:export')")
    @Log(title = "销售目标", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmSalesgoal crmSalesgoal)
    {
        List<CrmSalesgoal> list = crmSalesgoalService.selectCrmSalesgoalList(crmSalesgoal);
        ExcelUtil<CrmSalesgoal> util = new ExcelUtil<CrmSalesgoal>(CrmSalesgoal.class);
        util.exportExcel(response, list, "销售目标数据");
    }

    @PreAuthorize("@ss.hasPermi('crm:salesgoal:query')")
    @GetMapping(value = "/{goalId}")
    public AjaxResult getInfo(@PathVariable("goalId") String goalId)
    {
        return success(crmSalesgoalService.selectCrmSalesgoalByGoalId(goalId));
    }

    @PreAuthorize("@ss.hasPermi('crm:salesgoal:add')")
    @Log(title = "销售目标", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmSalesgoal crmSalesgoal)
    {
        return toAjax(crmSalesgoalService.insertCrmSalesgoal(crmSalesgoal));
    }

    @PreAuthorize("@ss.hasPermi('crm:salesgoal:edit')")
    @Log(title = "销售目标", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmSalesgoal crmSalesgoal)
    {
        return toAjax(crmSalesgoalService.updateCrmSalesgoal(crmSalesgoal));
    }

    /**
     * 全局重置（旧接口，KPI 流程请用 assess/batch）。
     * @deprecated 使用 {@link #assessBatch}
     */
    @Deprecated
    @PreAuthorize("@ss.hasPermi('crm:salesgoal:edit')")
    @Log(title = "销售目标", businessType = BusinessType.UPDATE)
    @PutMapping("/reset")
    public AjaxResult reset(@RequestBody CrmSalesgoal crmSalesgoal)
    {
        return toAjax(crmSalesgoalService.resetCrmSalesgoal(crmSalesgoal));
    }

    /**
     * 批量考核：该月 + 该 KPI 下已存在行全部更新 isActive。
     * isActive: "0"=一键开启考核，"1"=一键关闭考核。
     */
    @PreAuthorize("@ss.hasPermi('crm:salesgoal:assess')")
    @Log(title = "销售目标考核", businessType = BusinessType.UPDATE)
    @PutMapping("/assess/batch")
    public AjaxResult assessBatch(@RequestBody SalesgoalAssessBatchRequest request)
    {
        int updated = crmSalesgoalService.assessBatch(request);
        Map<String, Object> data = new HashMap<>(2);
        data.put("updated", updated);
        return AjaxResult.success(data);
    }

    /**
     * 预建行：缺失的 (ownerId, periodId, kpiCode) 插入 goalValue=0、isActive=1。
     */
    @PreAuthorize("@ss.hasPermi('crm:salesgoal:assess')")
    @Log(title = "销售目标预建行", businessType = BusinessType.INSERT)
    @PostMapping("/ensure-rows")
    public AjaxResult ensureRows(@RequestBody SalesgoalAssessBatchRequest request)
    {
        Map<String, Integer> data = crmSalesgoalService.ensureRows(request);
        return AjaxResult.success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:salesgoal:remove')")
    @Log(title = "销售目标", businessType = BusinessType.DELETE)
    @DeleteMapping("/{goalIds}")
    public AjaxResult remove(@PathVariable String[] goalIds)
    {
        return toAjax(crmSalesgoalService.deleteCrmSalesgoalByGoalIds(goalIds));
    }
}
