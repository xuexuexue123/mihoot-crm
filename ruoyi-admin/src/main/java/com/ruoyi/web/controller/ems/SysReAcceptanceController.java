package com.ruoyi.web.controller.ems;

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
import com.ruoyi.ems.domain.SysReAcceptance;
import com.ruoyi.ems.service.ISysReAcceptanceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 任务验收Controller
 * 
 * @author luohd
 * @date 2023-05-19
 */
@RestController
@RequestMapping("/ems/acceptance")
public class SysReAcceptanceController extends BaseController
{
    @Autowired
    private ISysReAcceptanceService sysReAcceptanceService;

    /**
     * 查询任务验收列表
     */
    @PreAuthorize("@ss.hasPermi('ems:acceptance:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReAcceptance sysReAcceptance)
    {
        startPage();
        List<SysReAcceptance> list = sysReAcceptanceService.selectSysReAcceptanceList(sysReAcceptance);
        return getDataTable(list);
    }

    /**
     * 导出任务验收列表
     */
    @PreAuthorize("@ss.hasPermi('ems:acceptance:export')")
    @Log(title = "任务验收", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReAcceptance sysReAcceptance)
    {
        List<SysReAcceptance> list = sysReAcceptanceService.selectSysReAcceptanceList(sysReAcceptance);
        ExcelUtil<SysReAcceptance> util = new ExcelUtil<SysReAcceptance>(SysReAcceptance.class);
        util.exportExcel(response, list, "任务验收数据");
    }

    /**
     * 获取任务验收详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:acceptance:query')")
    @GetMapping(value = "/{acceptId}")
    public AjaxResult getInfo(@PathVariable("acceptId") String acceptId)
    {
        return success(sysReAcceptanceService.selectSysReAcceptanceByAcceptId(acceptId));
    }

    /**
     * 新增任务验收
     */
    @PreAuthorize("@ss.hasPermi('ems:acceptance:add')")
    @Log(title = "任务验收", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReAcceptance sysReAcceptance)
    {
        return toAjax(sysReAcceptanceService.insertSysReAcceptance(sysReAcceptance));
    }

    /**
     * 修改任务验收
     */
    @PreAuthorize("@ss.hasPermi('ems:acceptance:edit')")
    @Log(title = "任务验收", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReAcceptance sysReAcceptance)
    {
        return toAjax(sysReAcceptanceService.updateSysReAcceptance(sysReAcceptance));
    }

    /**
     * 删除任务验收
     */
    @PreAuthorize("@ss.hasPermi('ems:acceptance:remove')")
    @Log(title = "任务验收", businessType = BusinessType.DELETE)
	@DeleteMapping("/{acceptIds}")
    public AjaxResult remove(@PathVariable String[] acceptIds)
    {
        return toAjax(sysReAcceptanceService.deleteSysReAcceptanceByAcceptIds(acceptIds));
    }


    /**
     * 删除任务验收
     */
    @PreAuthorize("@ss.hasPermi('ems:acceptance:remove')")
    @Log(title = "任务验收", businessType = BusinessType.DELETE)
    @DeleteMapping("/task/{taskIds}")
    public AjaxResult removeByTaskId(@PathVariable String[] taskIds)
    {
        sysReAcceptanceService.deleteSysReAcceptanceByTaskIds(taskIds);
        return AjaxResult.success("操作成功");

    }

}
