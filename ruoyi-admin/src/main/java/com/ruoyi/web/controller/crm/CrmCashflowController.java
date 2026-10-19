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
import com.ruoyi.crm.domain.CrmCashflow;
import com.ruoyi.crm.service.ICrmCashflowService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 资金流水Controller
 * 
 * @author luohd
 * @date 2024-06-19
 */
@RestController
@RequestMapping("/crm/cashflow")
public class CrmCashflowController extends BaseController
{
    @Autowired
    private ICrmCashflowService crmCashflowService;

    /**
     * 查询资金流水列表
     */
    @PreAuthorize("@ss.hasPermi('crm:cashflow:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmCashflow crmCashflow)
    {
        startPage();
        List<CrmCashflow> list = crmCashflowService.selectCrmCashflowList(crmCashflow);
        return getDataTable(list);
    }

    /**
     * 导出资金流水列表
     */
    @PreAuthorize("@ss.hasPermi('crm:cashflow:export')")
    @Log(title = "资金流水", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmCashflow crmCashflow)
    {
        List<CrmCashflow> list = crmCashflowService.selectCrmCashflowList(crmCashflow);
        ExcelUtil<CrmCashflow> util = new ExcelUtil<CrmCashflow>(CrmCashflow.class);
        util.exportExcel(response, list, "资金流水数据");
    }

    /**
     * 获取资金流水详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:cashflow:query')")
    @GetMapping(value = "/{cashflowId}")
    public AjaxResult getInfo(@PathVariable("cashflowId") String cashflowId)
    {
        return success(crmCashflowService.selectCrmCashflowByCashflowId(cashflowId));
    }

    /**
     * 新增资金流水
     */
    @PreAuthorize("@ss.hasPermi('crm:cashflow:add')")
    @Log(title = "资金流水", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmCashflow crmCashflow)
    {
        return toAjax(crmCashflowService.insertCrmCashflow(crmCashflow));
    }

    /**
     * 修改资金流水
     */
    @PreAuthorize("@ss.hasPermi('crm:cashflow:edit')")
    @Log(title = "资金流水", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmCashflow crmCashflow)
    {
        return toAjax(crmCashflowService.updateCrmCashflow(crmCashflow));
    }

    /**
     * 删除资金流水
     */
    @PreAuthorize("@ss.hasPermi('crm:cashflow:remove')")
    @Log(title = "资金流水", businessType = BusinessType.DELETE)
	@DeleteMapping("/{cashflowIds}")
    public AjaxResult remove(@PathVariable String[] cashflowIds)
    {
        return toAjax(crmCashflowService.deleteCrmCashflowByCashflowIds(cashflowIds));
    }
}
