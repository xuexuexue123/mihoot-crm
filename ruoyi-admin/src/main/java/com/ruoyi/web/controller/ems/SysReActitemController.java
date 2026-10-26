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
import com.ruoyi.ems.domain.SysReActitem;
import com.ruoyi.ems.service.ISysReActitemService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * activity项Controller
 * 
 * @author luohd
 * @date 2023-03-14
 */
@RestController
@RequestMapping("/ems/actitem")
public class SysReActitemController extends BaseController
{
    @Autowired
    private ISysReActitemService sysReActitemService;

    /**
     * 查询activity项列表
     */
    @PreAuthorize("@ss.hasPermi('ems:actitem:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReActitem sysReActitem)
    {
        startPage();
        List<SysReActitem> list = sysReActitemService.selectSysReActitemList(sysReActitem);
        return getDataTable(list);
    }

    /**
     * 导出activity项列表
     */
    @PreAuthorize("@ss.hasPermi('ems:actitem:export')")
    @Log(title = "activity项", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReActitem sysReActitem)
    {
        List<SysReActitem> list = sysReActitemService.selectSysReActitemList(sysReActitem);
        ExcelUtil<SysReActitem> util = new ExcelUtil<SysReActitem>(SysReActitem.class);
        util.exportExcel(response, list, "activity项数据");
    }

    /**
     * 获取activity项详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:actitem:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReActitemService.selectSysReActitemByPyId(pyId));
    }

    /**
     * 新增activity项
     */
    @PreAuthorize("@ss.hasPermi('ems:actitem:add')")
    @Log(title = "activity项", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReActitem sysReActitem)
    {
        return toAjax(sysReActitemService.insertSysReActitem(sysReActitem));
    }

    /**
     * 修改activity项
     */
    @PreAuthorize("@ss.hasPermi('ems:actitem:edit')")
    @Log(title = "activity项", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReActitem sysReActitem)
    {
        return toAjax(sysReActitemService.updateSysReActitem(sysReActitem));
    }

    /**
     * 删除activity项
     */
    @PreAuthorize("@ss.hasPermi('ems:actitem:remove')")
    @Log(title = "activity项", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        return toAjax(sysReActitemService.deleteSysReActitemByPyIds(pyIds));
    }

    /**
     * 删除activity项
     */
    @PreAuthorize("@ss.hasPermi('ems:actitem:remove')")
    @Log(title = "activity项", businessType = BusinessType.DELETE)
    @DeleteMapping("/act/{actId}")
    public AjaxResult removeByActId(@PathVariable String actId)
    {
        return toAjax(sysReActitemService.deleteSysReActitemByActId(actId));
    }

    /**
     * 批量新增数据转换项
     */
    @PreAuthorize("@ss.hasPermi('ems:actitem:add')")
    @Log(title = "activity项", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchAdd(@RequestBody List<SysReActitem> list)
    {
        return toAjax(sysReActitemService.batchSysReActitemList(list));
    }
}
