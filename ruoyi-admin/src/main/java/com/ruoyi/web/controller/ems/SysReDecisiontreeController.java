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
import com.ruoyi.ems.domain.SysReDecisiontree;
import com.ruoyi.ems.service.ISysReDecisiontreeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 决策树Controller
 * 
 * @author luohd
 * @date 2023-03-25
 */
@RestController
@RequestMapping("/ems/decisiontree")
public class SysReDecisiontreeController extends BaseController
{
    @Autowired
    private ISysReDecisiontreeService sysReDecisiontreeService;

    /**
     * 查询决策树列表
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontree:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReDecisiontree sysReDecisiontree)
    {
        startPage();
        List<SysReDecisiontree> list = sysReDecisiontreeService.selectSysReDecisiontreeList(sysReDecisiontree);
        return getDataTable(list);
    }

    /**
     * 导出决策树列表
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontree:export')")
    @Log(title = "决策树", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReDecisiontree sysReDecisiontree)
    {
        List<SysReDecisiontree> list = sysReDecisiontreeService.selectSysReDecisiontreeList(sysReDecisiontree);
        ExcelUtil<SysReDecisiontree> util = new ExcelUtil<SysReDecisiontree>(SysReDecisiontree.class);
        util.exportExcel(response, list, "决策树数据");
    }

    /**
     * 获取决策树详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontree:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReDecisiontreeService.selectSysReDecisiontreeByPyId(pyId));
    }

    /**
     * 新增决策树
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontree:add')")
    @Log(title = "决策树", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReDecisiontree sysReDecisiontree)
    {
        return toAjax(sysReDecisiontreeService.insertSysReDecisiontree(sysReDecisiontree));
    }

    /**
     * 修改决策树
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontree:edit')")
    @Log(title = "决策树", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReDecisiontree sysReDecisiontree)
    {
        return toAjax(sysReDecisiontreeService.updateSysReDecisiontree(sysReDecisiontree));
    }

    /**
     * 删除决策树
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontree:remove')")
    @Log(title = "决策树", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        return toAjax(sysReDecisiontreeService.deleteSysReDecisiontreeByPyIds(pyIds));
    }
}
