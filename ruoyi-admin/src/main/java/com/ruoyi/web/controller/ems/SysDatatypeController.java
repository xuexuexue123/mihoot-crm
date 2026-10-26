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
import com.ruoyi.ems.domain.SysDatatype;
import com.ruoyi.ems.service.ISysDatatypeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * datatypeController
 * 
 * @author luohd
 * @date 2022-12-13
 */
@RestController
@RequestMapping("/ems/datatype")
public class SysDatatypeController extends BaseController
{
    @Autowired
    private ISysDatatypeService sysDatatypeService;

    /**
     * 查询datatype列表
     */
    @PreAuthorize("@ss.hasPermi('ems:datatype:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDatatype sysDatatype)
    {
        startPage();
        List<SysDatatype> list = sysDatatypeService.selectSysDatatypeList(sysDatatype);
        return getDataTable(list);
    }

    /**
     * 导出datatype列表
     */
    @PreAuthorize("@ss.hasPermi('ems:datatype:export')")
    @Log(title = "datatype", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDatatype sysDatatype)
    {
        List<SysDatatype> list = sysDatatypeService.selectSysDatatypeList(sysDatatype);
        ExcelUtil<SysDatatype> util = new ExcelUtil<SysDatatype>(SysDatatype.class);
        util.exportExcel(response, list, "datatype数据");
    }

    /**
     * 获取datatype详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:datatype:query')")
    @GetMapping(value = "/{dataId}")
    public AjaxResult getInfo(@PathVariable("dataId") Long dataId)
    {
        return success(sysDatatypeService.selectSysDatatypeByDataId(dataId));
    }

    /**
     * 新增datatype
     */
    @PreAuthorize("@ss.hasPermi('ems:datatype:add')")
    @Log(title = "datatype", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysDatatype sysDatatype)
    {
        return toAjax(sysDatatypeService.insertSysDatatype(sysDatatype));
    }

    /**
     * 修改datatype
     */
    @PreAuthorize("@ss.hasPermi('ems:datatype:edit')")
    @Log(title = "datatype", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysDatatype sysDatatype)
    {
        return toAjax(sysDatatypeService.updateSysDatatype(sysDatatype));
    }

    /**
     * 删除datatype
     */
    @PreAuthorize("@ss.hasPermi('ems:datatype:remove')")
    @Log(title = "datatype", businessType = BusinessType.DELETE)
	@DeleteMapping("/{dataIds}")
    public AjaxResult remove(@PathVariable Long[] dataIds)
    {
        return toAjax(sysDatatypeService.deleteSysDatatypeByDataIds(dataIds));
    }
}
