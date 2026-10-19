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
import com.ruoyi.ems.domain.SysReDtitem;
import com.ruoyi.ems.service.ISysReDtitemService;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 数据转换项Controller
 * 
 * @author luohd
 * @date 2023-03-09
 */
@RestController
@RequestMapping("/ems/dtitem")
public class SysReDtitemController extends BaseController
{
    @Autowired
    private ISysReDtitemService sysReDtitemService;

    /**
     * 查询数据转换项列表
     */
    @PreAuthorize("@ss.hasPermi('ems:dtitem:list')")
    @GetMapping("/list")
    public AjaxResult list(SysReDtitem sysReDtitem)
    {
        List<SysReDtitem> list = sysReDtitemService.selectSysReDtitemList(sysReDtitem);
        return success(list);
    }

    /**
     * 导出数据转换项列表
     */
    @PreAuthorize("@ss.hasPermi('ems:dtitem:export')")
    @Log(title = "数据转换项", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReDtitem sysReDtitem)
    {
        List<SysReDtitem> list = sysReDtitemService.selectSysReDtitemList(sysReDtitem);
        ExcelUtil<SysReDtitem> util = new ExcelUtil<SysReDtitem>(SysReDtitem.class);
        util.exportExcel(response, list, "数据转换项数据");
    }

    /**
     * 获取数据转换项详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:dtitem:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReDtitemService.selectSysReDtitemByPyId(pyId));
    }

    /**
     * 新增数据转换项
     */
    @PreAuthorize("@ss.hasPermi('ems:dtitem:add')")
    @Log(title = "数据转换项", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReDtitem sysReDtitem)
    {
        return toAjax(sysReDtitemService.insertSysReDtitem(sysReDtitem));
    }

    /**
     * 修改数据转换项
     */
    @PreAuthorize("@ss.hasPermi('ems:dtitem:edit')")
    @Log(title = "数据转换项", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReDtitem sysReDtitem)
    {
        return toAjax(sysReDtitemService.updateSysReDtitem(sysReDtitem));
    }

    /**
     * 删除数据转换项
     */
    @PreAuthorize("@ss.hasPermi('ems:dtitem:remove')")
    @Log(title = "数据转换项", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        return toAjax(sysReDtitemService.deleteSysReDtitemByPyIds(pyIds));
    }

    /**
     * 删除数据转换项
     */
    @PreAuthorize("@ss.hasPermi('ems:dtitem:remove')")
    @Log(title = "数据转换项", businessType = BusinessType.DELETE)
    @DeleteMapping("/dt/{dtId}")
    public AjaxResult removeByDtId(@PathVariable Long dtId)
    {
        return toAjax(sysReDtitemService.deleteSysReDtitemByDtId(dtId));
    }
    /**
     * 批量新增数据转换项
     */
    @PreAuthorize("@ss.hasPermi('ems:dtitem:add')")
    @Log(title = "数据转换项", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchAdd(@RequestBody List<SysReDtitem> list)
    {
        return toAjax(sysReDtitemService.batchSysReDtitemList(list));
    }
}
