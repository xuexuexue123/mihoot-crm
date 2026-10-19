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
import com.ruoyi.crm.domain.CrmForecast;
import com.ruoyi.crm.service.ICrmForecastService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 销售预测Controller
 * 
 * @author luohd
 * @date 2024-01-18
 */
@RestController
@RequestMapping("/crm/forecast")
public class CrmForecastController extends BaseController
{
    @Autowired
    private ICrmForecastService crmForecastService;

    /**
     * 查询销售预测列表
     */
    @PreAuthorize("@ss.hasPermi('crm:forecast:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmForecast crmForecast)
    {
        startPage();
        List<CrmForecast> list = crmForecastService.selectCrmForecastList(crmForecast);
        return getDataTable(list);
    }

    /**
     * 导出销售预测列表
     */
    @PreAuthorize("@ss.hasPermi('crm:forecast:export')")
    @Log(title = "销售预测", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmForecast crmForecast)
    {
        List<CrmForecast> list = crmForecastService.selectCrmForecastList(crmForecast);
        ExcelUtil<CrmForecast> util = new ExcelUtil<CrmForecast>(CrmForecast.class);
        util.exportExcel(response, list, "销售预测数据");
    }

    /**
     * 获取销售预测详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:forecast:query')")
    @GetMapping(value = "/{forecastId}")
    public AjaxResult getInfo(@PathVariable("forecastId") String forecastId)
    {
        return success(crmForecastService.selectCrmForecastByForecastId(forecastId));
    }

    /**
     * 新增销售预测
     */
    @PreAuthorize("@ss.hasPermi('crm:forecast:add')")
    @Log(title = "销售预测", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmForecast crmForecast)
    {
        return toAjax(crmForecastService.insertCrmForecast(crmForecast));
    }

    /**
     * 修改销售预测
     */
    @PreAuthorize("@ss.hasPermi('crm:forecast:edit')")
    @Log(title = "销售预测", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmForecast crmForecast)
    {
        return toAjax(crmForecastService.updateCrmForecast(crmForecast));
    }

    /**
     * 删除销售预测
     */
    @PreAuthorize("@ss.hasPermi('crm:forecast:remove')")
    @Log(title = "销售预测", businessType = BusinessType.DELETE)
	@DeleteMapping("/{forecastIds}")
    public AjaxResult remove(@PathVariable String[] forecastIds)
    {
        return toAjax(crmForecastService.deleteCrmForecastByForecastIds(forecastIds));
    }
}
