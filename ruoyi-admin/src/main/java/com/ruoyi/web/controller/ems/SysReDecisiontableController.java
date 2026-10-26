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
import com.ruoyi.ems.domain.SysReDecisiontable;
import com.ruoyi.ems.service.ISysReDecisiontableService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 决策表Controller
 * 
 * @author luohd
 * @date 2023-03-30
 */
@RestController
@RequestMapping("/ems/decisiontable")
public class SysReDecisiontableController extends BaseController
{
    @Autowired
    private ISysReDecisiontableService sysReDecisiontableService;

    /**
     * 查询决策表列表
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontable:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReDecisiontable sysReDecisiontable)
    {
        startPage();
        List<SysReDecisiontable> list = sysReDecisiontableService.selectSysReDecisiontableList(sysReDecisiontable);
        return getDataTable(list);
    }

    /**
     * 导出决策表列表
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontable:export')")
    @Log(title = "决策表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReDecisiontable sysReDecisiontable)
    {
        List<SysReDecisiontable> list = sysReDecisiontableService.selectSysReDecisiontableList(sysReDecisiontable);
        ExcelUtil<SysReDecisiontable> util = new ExcelUtil<SysReDecisiontable>(SysReDecisiontable.class);
        util.exportExcel(response, list, "决策表数据");
    }

    /**
     * 获取决策表详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontable:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReDecisiontableService.selectSysReDecisiontableByPyId(pyId));
    }

    /**
     * 新增决策表
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontable:add')")
    @Log(title = "决策表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReDecisiontable sysReDecisiontable)
    {
        return toAjax(sysReDecisiontableService.insertSysReDecisiontable(sysReDecisiontable));
    }

    /**
     * 修改决策表
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontable:edit')")
    @Log(title = "决策表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReDecisiontable sysReDecisiontable)
    {
        return toAjax(sysReDecisiontableService.updateSysReDecisiontable(sysReDecisiontable));
    }

    /**
     * 删除决策表
     */
    @PreAuthorize("@ss.hasPermi('ems:decisiontable:remove')")
    @Log(title = "决策表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        return toAjax(sysReDecisiontableService.deleteSysReDecisiontableByPyIds(pyIds));
    }
}
