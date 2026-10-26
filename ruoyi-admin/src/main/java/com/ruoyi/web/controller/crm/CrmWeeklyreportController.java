package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmWeeklyreport;
import com.ruoyi.crm.service.ICrmWeeklyreportService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 周报Controller
 * 
 * @author luohd
 * @date 2024-04-16
 */
@RestController
@RequestMapping("/crm/weeklyreport")
public class CrmWeeklyreportController extends BaseController
{
    @Autowired
    private ICrmWeeklyreportService crmWeeklyreportService;

    /**
     * 查询周报列表
     */
    @PreAuthorize("@ss.hasPermi('crm:weeklyreport:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmWeeklyreport crmWeeklyreport)
    {
        startPage();
        List<CrmWeeklyreport> list = crmWeeklyreportService.selectCrmWeeklyreportList(crmWeeklyreport);
        return getDataTable(list);
    }

    /**
     * 查询本周周报列表
     */
    @PreAuthorize("@ss.hasPermi('crm:weeklyreport:list')")
    @GetMapping("/listweekly")
    public TableDataInfo listweekly(CrmWeeklyreport crmWeeklyreport)
    {
        startPage();
        List<CrmWeeklyreport> list = crmWeeklyreportService.selectCrmWeeklyreportListWeekly(crmWeeklyreport);
        return getDataTable(list);
    }

    /**
     * 导出周报列表
     */
    @PreAuthorize("@ss.hasPermi('crm:weeklyreport:export')")
    @Log(title = "周报", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmWeeklyreport crmWeeklyreport)
    {
        List<CrmWeeklyreport> list = crmWeeklyreportService.selectCrmWeeklyreportList(crmWeeklyreport);
        ExcelUtil<CrmWeeklyreport> util = new ExcelUtil<CrmWeeklyreport>(CrmWeeklyreport.class);
        util.exportExcel(response, list, "周报数据");
    }

    /**
     * 获取周报详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:weeklyreport:query')")
    @GetMapping(value = "/{reportId}")
    public AjaxResult getInfo(@PathVariable("reportId") String reportId)
    {
        return success(crmWeeklyreportService.selectCrmWeeklyreportByReportId(reportId));
    }

    /**
     * 新增周报
     */
    @PreAuthorize("@ss.hasPermi('crm:weeklyreport:add')")
    @Log(title = "周报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmWeeklyreport crmWeeklyreport)
    {
        return toAjax(crmWeeklyreportService.insertCrmWeeklyreport(crmWeeklyreport));
    }

    /**
     * 修改周报
     */
    @PreAuthorize("@ss.hasPermi('crm:weeklyreport:edit')")
    @Log(title = "周报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmWeeklyreport crmWeeklyreport)
    {
        return toAjax(crmWeeklyreportService.updateCrmWeeklyreport(crmWeeklyreport));
    }

    /**
     * 删除周报
     */
    @PreAuthorize("@ss.hasPermi('crm:weeklyreport:remove')")
    @Log(title = "周报", businessType = BusinessType.DELETE)
	@DeleteMapping("/{reportIds}")
    public AjaxResult remove(@PathVariable String[] reportIds)
    {
        return toAjax(crmWeeklyreportService.deleteCrmWeeklyreportByReportIds(reportIds));
    }
}
