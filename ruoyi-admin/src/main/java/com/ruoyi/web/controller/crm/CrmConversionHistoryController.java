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
import com.ruoyi.crm.domain.CrmConversionHistory;
import com.ruoyi.crm.service.ICrmConversionHistoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 转化历史Controller
 * 
 * @author luohd
 * @date 2025-04-29
 */
@RestController
@RequestMapping("/crm/conversionhistory")
public class CrmConversionHistoryController extends BaseController
{
    @Autowired
    private ICrmConversionHistoryService crmConversionHistoryService;

    /**
     * 查询转化历史列表
     */
    @PreAuthorize("@ss.hasPermi('crm:conversionhistory:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmConversionHistory crmConversionHistory)
    {
        startPage();
        List<CrmConversionHistory> list = crmConversionHistoryService.selectCrmConversionHistoryList(crmConversionHistory);
        return getDataTable(list);
    }

    /**
     * 导出转化历史列表
     */
    @PreAuthorize("@ss.hasPermi('crm:conversionhistory:export')")
    @Log(title = "转化历史", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmConversionHistory crmConversionHistory)
    {
        List<CrmConversionHistory> list = crmConversionHistoryService.selectCrmConversionHistoryList(crmConversionHistory);
        ExcelUtil<CrmConversionHistory> util = new ExcelUtil<CrmConversionHistory>(CrmConversionHistory.class);
        util.exportExcel(response, list, "转化历史数据");
    }

    /**
     * 获取转化历史详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:conversionhistory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(crmConversionHistoryService.selectCrmConversionHistoryById(id));
    }

    /**
     * 新增转化历史
     */
    @PreAuthorize("@ss.hasPermi('crm:conversionhistory:add')")
    @Log(title = "转化历史", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmConversionHistory crmConversionHistory)
    {
        return toAjax(crmConversionHistoryService.insertCrmConversionHistory(crmConversionHistory));
    }

    /**
     * 修改转化历史
     */
    @PreAuthorize("@ss.hasPermi('crm:conversionhistory:edit')")
    @Log(title = "转化历史", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmConversionHistory crmConversionHistory)
    {
        return toAjax(crmConversionHistoryService.updateCrmConversionHistory(crmConversionHistory));
    }

    /**
     * 删除转化历史
     */
    @PreAuthorize("@ss.hasPermi('crm:conversionhistory:remove')")
    @Log(title = "转化历史", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(crmConversionHistoryService.deleteCrmConversionHistoryByIds(ids));
    }
}
