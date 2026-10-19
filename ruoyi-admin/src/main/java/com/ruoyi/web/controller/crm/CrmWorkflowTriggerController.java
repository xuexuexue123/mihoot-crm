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
import com.ruoyi.crm.domain.CrmWorkflowTrigger;
import com.ruoyi.crm.service.ICrmWorkflowTriggerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * worflowtriggerController
 * 
 * @author luohd
 * @date 2025-06-05
 */
@RestController
@RequestMapping("/crm/worflowtrigger")
public class CrmWorkflowTriggerController extends BaseController
{
    @Autowired
    private ICrmWorkflowTriggerService crmWorkflowTriggerService;

    /**
     * 查询worflowtrigger列表
     */
    @PreAuthorize("@ss.hasPermi('crm:worflowtrigger:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmWorkflowTrigger crmWorkflowTrigger)
    {
        startPage();
        List<CrmWorkflowTrigger> list = crmWorkflowTriggerService.selectCrmWorkflowTriggerList(crmWorkflowTrigger);
        return getDataTable(list);
    }

    /**
     * 导出worflowtrigger列表
     */
    @PreAuthorize("@ss.hasPermi('crm:worflowtrigger:export')")
    @Log(title = "worflowtrigger", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmWorkflowTrigger crmWorkflowTrigger)
    {
        List<CrmWorkflowTrigger> list = crmWorkflowTriggerService.selectCrmWorkflowTriggerList(crmWorkflowTrigger);
        ExcelUtil<CrmWorkflowTrigger> util = new ExcelUtil<CrmWorkflowTrigger>(CrmWorkflowTrigger.class);
        util.exportExcel(response, list, "worflowtrigger数据");
    }

    /**
     * 获取worflowtrigger详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:worflowtrigger:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(crmWorkflowTriggerService.selectCrmWorkflowTriggerById(id));
    }

    /**
     * 新增worflowtrigger
     */
    @PreAuthorize("@ss.hasPermi('crm:worflowtrigger:add')")
    @Log(title = "worflowtrigger", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmWorkflowTrigger crmWorkflowTrigger)
    {
        return toAjax(crmWorkflowTriggerService.insertCrmWorkflowTrigger(crmWorkflowTrigger));
    }

    /**
     * 修改worflowtrigger
     */
    @PreAuthorize("@ss.hasPermi('crm:worflowtrigger:edit')")
    @Log(title = "worflowtrigger", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmWorkflowTrigger crmWorkflowTrigger)
    {
        return toAjax(crmWorkflowTriggerService.updateCrmWorkflowTrigger(crmWorkflowTrigger));
    }

    /**
     * 删除worflowtrigger
     */
    @PreAuthorize("@ss.hasPermi('crm:worflowtrigger:remove')")
    @Log(title = "worflowtrigger", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(crmWorkflowTriggerService.deleteCrmWorkflowTriggerByIds(ids));
    }
}
