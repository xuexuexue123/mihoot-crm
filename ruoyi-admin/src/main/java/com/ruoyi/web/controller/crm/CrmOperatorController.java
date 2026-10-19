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
import com.ruoyi.crm.domain.CrmOperator;
import com.ruoyi.crm.service.ICrmOperatorService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 销售人员Controller
 * 
 * @author luohd
 * @date 2024-01-29
 */
@RestController
@RequestMapping("/crm/operator")
public class CrmOperatorController extends BaseController
{
    @Autowired
    private ICrmOperatorService crmOperatorService;

    /**
     * 查询销售人员列表
     */
    @PreAuthorize("@ss.hasPermi('crm:operator:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmOperator crmOperator)
    {
        startPage();
        List<CrmOperator> list = crmOperatorService.selectCrmOperatorList(crmOperator);
        return getDataTable(list);
    }

    /**
     * 导出销售人员列表
     */
    @PreAuthorize("@ss.hasPermi('crm:operator:export')")
    @Log(title = "销售人员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmOperator crmOperator)
    {
        List<CrmOperator> list = crmOperatorService.selectCrmOperatorList(crmOperator);
        ExcelUtil<CrmOperator> util = new ExcelUtil<CrmOperator>(CrmOperator.class);
        util.exportExcel(response, list, "销售人员数据");
    }

    /**
     * 获取销售人员详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:operator:query')")
    @GetMapping(value = "/{operatorId}")
    public AjaxResult getInfo(@PathVariable("operatorId") String operatorId)
    {
        return success(crmOperatorService.selectCrmOperatorByOperatorId(operatorId));
    }

    /**
     * 新增销售人员
     */
    @PreAuthorize("@ss.hasPermi('crm:operator:add')")
    @Log(title = "销售人员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmOperator crmOperator)
    {
        return toAjax(crmOperatorService.insertCrmOperator(crmOperator));
    }

    /**
     * 修改销售人员
     */
    @PreAuthorize("@ss.hasPermi('crm:operator:edit')")
    @Log(title = "销售人员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmOperator crmOperator)
    {
        return toAjax(crmOperatorService.updateCrmOperator(crmOperator));
    }

    /**
     * 删除销售人员
     */
    @PreAuthorize("@ss.hasPermi('crm:operator:remove')")
    @Log(title = "销售人员", businessType = BusinessType.DELETE)
	@DeleteMapping("/{operatorIds}")
    public AjaxResult remove(@PathVariable String[] operatorIds)
    {
        return toAjax(crmOperatorService.deleteCrmOperatorByOperatorIds(operatorIds));
    }
}
