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
import com.ruoyi.crm.domain.CrmOptyacceptance;
import com.ruoyi.crm.service.ICrmOptyacceptanceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 销售任务Controller
 * 
 * @author luohd
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/crm/optyacceptance")
public class CrmOptyacceptanceController extends BaseController
{
    @Autowired
    private ICrmOptyacceptanceService crmOptyacceptanceService;

    /**
     * 查询销售任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:optyacceptance:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmOptyacceptance crmOptyacceptance)
    {
        startPage();
        List<CrmOptyacceptance> list = crmOptyacceptanceService.selectCrmOptyacceptanceList(crmOptyacceptance);
        return getDataTable(list);
    }

    /**
     * 导出销售任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:optyacceptance:export')")
    @Log(title = "销售任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmOptyacceptance crmOptyacceptance)
    {
        List<CrmOptyacceptance> list = crmOptyacceptanceService.selectCrmOptyacceptanceList(crmOptyacceptance);
        ExcelUtil<CrmOptyacceptance> util = new ExcelUtil<CrmOptyacceptance>(CrmOptyacceptance.class);
        util.exportExcel(response, list, "销售任务数据");
    }

    /**
     * 获取销售任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:optyacceptance:query')")
    @GetMapping(value = "/{acceptId}")
    public AjaxResult getInfo(@PathVariable("acceptId") String acceptId)
    {
        return success(crmOptyacceptanceService.selectCrmOptyacceptanceByAcceptId(acceptId));
    }

    /**
     * 新增销售任务
     */
    @PreAuthorize("@ss.hasPermi('crm:optyacceptance:add')")
    @Log(title = "销售任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmOptyacceptance crmOptyacceptance)
    {
        return toAjax(crmOptyacceptanceService.insertCrmOptyacceptance(crmOptyacceptance));
    }

    /**
     * 修改销售任务
     */
    @PreAuthorize("@ss.hasPermi('crm:optyacceptance:edit')")
    @Log(title = "销售任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmOptyacceptance crmOptyacceptance)
    {
        return toAjax(crmOptyacceptanceService.updateCrmOptyacceptance(crmOptyacceptance));
    }

    /**
     * 删除销售任务
     */
    @PreAuthorize("@ss.hasPermi('crm:optyacceptance:remove')")
    @Log(title = "销售任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{acceptIds}")
    public AjaxResult remove(@PathVariable String[] acceptIds)
    {
        return toAjax(crmOptyacceptanceService.deleteCrmOptyacceptanceByAcceptIds(acceptIds));
    }
}
