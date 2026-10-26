package com.ruoyi.web.controller.ems;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.ems.domain.SysReDatatransform;
import com.ruoyi.ems.service.ISysReDatatransformService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 数据转换Controller
 * 
 * @author luohd
 * @date 2023-03-09
 */
@RestController
@RequestMapping("/ems/datatransform")
public class SysReDatatransformController extends BaseController
{
    @Autowired
    private ISysReDatatransformService sysReDatatransformService;

    /**
     * 查询数据转换列表
     */
    @PreAuthorize("@ss.hasPermi('ems:datatransform:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReDatatransform sysReDatatransform)
    {
        startPage();
        List<SysReDatatransform> list = sysReDatatransformService.selectSysReDatatransformList(sysReDatatransform);
        return getDataTable(list);
    }

    /**
     * 导出数据转换列表
     */
    @PreAuthorize("@ss.hasPermi('ems:datatransform:export')")
    @Log(title = "数据转换", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReDatatransform sysReDatatransform)
    {
        List<SysReDatatransform> list = sysReDatatransformService.selectSysReDatatransformList(sysReDatatransform);
        ExcelUtil<SysReDatatransform> util = new ExcelUtil<SysReDatatransform>(SysReDatatransform.class);
        util.exportExcel(response, list, "数据转换数据");
    }

    /**
     * 获取数据转换详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:datatransform:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") Long pyId)
    {
        return success(sysReDatatransformService.selectSysReDatatransformByPyId(pyId));
    }
    /**
     * 获取数据转换详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:datatransform:query')")
    @PostMapping(value = "/case")
    public AjaxResult getInfoByCase(@RequestBody SysReDatatransform sysReDatatransform)
    {
        return success(sysReDatatransformService.selectSysReDatatransformByCase(sysReDatatransform));
    }

    /**
     * 新增数据转换
     */
    @PreAuthorize("@ss.hasPermi('ems:datatransform:add')")
    @Log(title = "数据转换", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReDatatransform sysReDatatransform)
    {
        return toAjax(sysReDatatransformService.insertSysReDatatransform(sysReDatatransform));
    }

    /**
     * 修改数据转换
     */
    @PreAuthorize("@ss.hasPermi('ems:datatransform:edit')")
    @Log(title = "数据转换", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReDatatransform sysReDatatransform)
    {
        return toAjax(sysReDatatransformService.updateSysReDatatransform(sysReDatatransform));
    }

    /**
     * 删除数据转换
     */
    @PreAuthorize("@ss.hasPermi('ems:datatransform:remove')")
    @Log(title = "数据转换", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable Long[] pyIds)
    {
        return toAjax(sysReDatatransformService.deleteSysReDatatransformByPyIds(pyIds));
    }
}
