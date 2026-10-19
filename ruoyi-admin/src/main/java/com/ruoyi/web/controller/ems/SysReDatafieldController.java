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
import com.ruoyi.ems.domain.SysReDatafield;
import com.ruoyi.ems.service.ISysReDatafieldService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 字段Controller
 * 
 * @author luohd
 * @date 2023-04-19
 */
@RestController
@RequestMapping("/ems/datafield")
public class SysReDatafieldController extends BaseController
{
    @Autowired
    private ISysReDatafieldService sysReDatafieldService;

    /**
     * 查询字段列表
     */
    @PreAuthorize("@ss.hasPermi('ems:datafield:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReDatafield sysReDatafield)
    {
        startPage();
        List<SysReDatafield> list = sysReDatafieldService.selectSysReDatafieldList(sysReDatafield);
        return getDataTable(list);
    }

    /**
     * 导出字段列表
     */
    @PreAuthorize("@ss.hasPermi('ems:datafield:export')")
    @Log(title = "字段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReDatafield sysReDatafield)
    {
        List<SysReDatafield> list = sysReDatafieldService.selectSysReDatafieldList(sysReDatafield);
        ExcelUtil<SysReDatafield> util = new ExcelUtil<SysReDatafield>(SysReDatafield.class);
        util.exportExcel(response, list, "字段数据");
    }

    /**
     * 获取字段详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:datafield:query')")
    @GetMapping(value = "/{fieldId}")
    public AjaxResult getInfo(@PathVariable("fieldId") String fieldId)
    {
        return success(sysReDatafieldService.selectSysReDatafieldByFieldId(fieldId));
    }

    /**
     * 新增字段
     */
    @PreAuthorize("@ss.hasPermi('ems:datafield:add')")
    @Log(title = "字段", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReDatafield sysReDatafield)
    {
        return toAjax(sysReDatafieldService.insertSysReDatafield(sysReDatafield));
    }

    /**
     * 修改字段
     */
    @PreAuthorize("@ss.hasPermi('ems:datafield:edit')")
    @Log(title = "字段", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReDatafield sysReDatafield)
    {
        return toAjax(sysReDatafieldService.updateSysReDatafield(sysReDatafield));
    }

    /**
     * 删除字段
     */
    @PreAuthorize("@ss.hasPermi('ems:datafield:remove')")
    @Log(title = "字段", businessType = BusinessType.DELETE)
	@DeleteMapping("/{fieldIds}")
    public AjaxResult remove(@PathVariable String[] fieldIds)
    {
        return toAjax(sysReDatafieldService.deleteSysReDatafieldByFieldIds(fieldIds));
    }


    /**
     * 批量新增数据转换项
     */
    @PreAuthorize("@ss.hasPermi('ems:datafield:add')")
    @Log(title = "表字段项", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchAdd(@RequestBody List<SysReDatafield> list)
    {
        return toAjax(sysReDatafieldService.batchInsertSysReDatafieldList(list));
    }
}
