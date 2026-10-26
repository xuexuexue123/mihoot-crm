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
import com.ruoyi.crm.domain.CrmTimeperiod;
import com.ruoyi.crm.service.ICrmTimeperiodService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 销售周期Controller
 * 
 * @author luohd
 * @date 2024-02-27
 */
@RestController
@RequestMapping("/crm/timeperiod")
public class CrmTimeperiodController extends BaseController
{
    @Autowired
    private ICrmTimeperiodService crmTimeperiodService;

    /**
     * 查询销售周期列表
     */
    @PreAuthorize("@ss.hasPermi('crm:timeperiod:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmTimeperiod crmTimeperiod)
    {
        startPage();
        List<CrmTimeperiod> list = crmTimeperiodService.selectCrmTimeperiodList(crmTimeperiod);
        return getDataTable(list);
    }

    /**
     * 导出销售周期列表
     */
    @PreAuthorize("@ss.hasPermi('crm:timeperiod:export')")
    @Log(title = "销售周期", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmTimeperiod crmTimeperiod)
    {
        List<CrmTimeperiod> list = crmTimeperiodService.selectCrmTimeperiodList(crmTimeperiod);
        ExcelUtil<CrmTimeperiod> util = new ExcelUtil<CrmTimeperiod>(CrmTimeperiod.class);
        util.exportExcel(response, list, "销售周期数据");
    }

    /**
     * 获取销售周期详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:timeperiod:query')")
    @GetMapping(value = "/{periodId}")
    public AjaxResult getInfo(@PathVariable("periodId") String periodId)
    {
        return success(crmTimeperiodService.selectCrmTimeperiodByPeriodId(periodId));
    }

    /**
     * 新增销售周期
     */
    @PreAuthorize("@ss.hasPermi('crm:timeperiod:add')")
    @Log(title = "销售周期", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmTimeperiod crmTimeperiod)
    {
        return toAjax(crmTimeperiodService.insertCrmTimeperiod(crmTimeperiod));
    }

    /**
     * 修改销售周期
     */
    @PreAuthorize("@ss.hasPermi('crm:timeperiod:edit')")
    @Log(title = "销售周期", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmTimeperiod crmTimeperiod)
    {
        return toAjax(crmTimeperiodService.updateCrmTimeperiod(crmTimeperiod));
    }

    /**
     * 修改销售周期
     */
    @PreAuthorize("@ss.hasPermi('crm:timeperiod:edit')")
    @Log(title = "销售周期", businessType = BusinessType.UPDATE)
    @PutMapping("/inactive")
    public AjaxResult editInactive(@RequestBody CrmTimeperiod crmTimeperiod)
    {
        return toAjax(crmTimeperiodService.updateCrmTimeperiodToNotActive(crmTimeperiod));
    }
    /**
     * 删除销售周期
     */
    @PreAuthorize("@ss.hasPermi('crm:timeperiod:remove')")
    @Log(title = "销售周期", businessType = BusinessType.DELETE)
	@DeleteMapping("/{periodIds}")
    public AjaxResult remove(@PathVariable String[] periodIds)
    {
        return toAjax(crmTimeperiodService.deleteCrmTimeperiodByPeriodIds(periodIds));
    }
}
