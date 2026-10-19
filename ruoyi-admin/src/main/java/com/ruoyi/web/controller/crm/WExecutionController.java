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
import com.ruoyi.crm.domain.WExecution;
import com.ruoyi.crm.service.IWExecutionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * WExecutionController
 * 
 * @author luohd
 * @date 2026-05-19
 */
@RestController
@RequestMapping("/crm/wexecution")
public class WExecutionController extends BaseController
{
    @Autowired
    private IWExecutionService wExecutionService;

    /**
     * 查询WExecution列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wexecution:list')")
    @GetMapping("/list")
    public TableDataInfo list(WExecution wExecution)
    {
        startPage();
        List<WExecution> list = wExecutionService.selectWExecutionList(wExecution);
        return getDataTable(list);
    }

    /**
     * 导出WExecution列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wexecution:export')")
    @Log(title = "WExecution", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WExecution wExecution)
    {
        List<WExecution> list = wExecutionService.selectWExecutionList(wExecution);
        ExcelUtil<WExecution> util = new ExcelUtil<WExecution>(WExecution.class);
        util.exportExcel(response, list, "WExecution数据");
    }

    /**
     * 获取WExecution详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:wexecution:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wExecutionService.selectWExecutionById(id));
    }

    /**
     * 新增WExecution
     */
    @PreAuthorize("@ss.hasPermi('crm:wexecution:add')")
    @Log(title = "WExecution", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WExecution wExecution)
    {
        return toAjax(wExecutionService.insertWExecution(wExecution));
    }

    /**
     * 修改WExecution
     */
    @PreAuthorize("@ss.hasPermi('crm:wexecution:edit')")
    @Log(title = "WExecution", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WExecution wExecution)
    {
        return toAjax(wExecutionService.updateWExecution(wExecution));
    }

    /**
     * 删除WExecution
     */
    @PreAuthorize("@ss.hasPermi('crm:wexecution:remove')")
    @Log(title = "WExecution", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wExecutionService.deleteWExecutionByIds(ids));
    }
}
