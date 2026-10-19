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
import com.ruoyi.ems.domain.SysCasetype;
import com.ruoyi.ems.service.ISysCasetypeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * casetypeController
 *
 * @author luohd
 * @date 2022-12-15
 */
@RestController
@RequestMapping("/ems/casetype")
public class SysCasetypeController extends BaseController
{
    @Autowired
    private ISysCasetypeService sysCasetypeService;

    /**
     * 查询casetype列表
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysCasetype sysCasetype)
    {
        startPage();
        List<SysCasetype> list = sysCasetypeService.selectSysCasetypeList(sysCasetype);
        return getDataTable(list);
    }

    /**
     * 导出casetype列表
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:export')")
    @Log(title = "casetype", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysCasetype sysCasetype)
    {
        List<SysCasetype> list = sysCasetypeService.selectSysCasetypeList(sysCasetype);
        ExcelUtil<SysCasetype> util = new ExcelUtil<SysCasetype>(SysCasetype.class);
        util.exportExcel(response, list, "casetype数据");
    }

    /**
     * 获取casetype详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysCasetypeService.selectSysCasetypeById(id));
    }

    /**
     * 获取casetype详细信息 by Case Type Id
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:query')")
    @GetMapping(value = "/ct/{caseTypeId}")
    public AjaxResult getInfo(@PathVariable("caseTypeId") String caseTypeId)
    {
        return success(sysCasetypeService.selectSysCasetypeByCaseTypeId(caseTypeId));
    }
    /**
     * 获取casetype详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:query')")
    @GetMapping(value = "/data/{id}")
    public AjaxResult getDatatypeInfo(@PathVariable("id") Long id)
    {
        return success(sysCasetypeService.selectSysCasetypeDatatypeById(id));
    }

    /**
     * 新增casetype
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:add')")
    @Log(title = "casetype", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysCasetype sysCasetype)
    {
        return toAjax(sysCasetypeService.insertSysCasetype(sysCasetype));
    }

    /**
     * 修改casetype
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:edit')")
    @Log(title = "casetype", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysCasetype sysCasetype)
    {
        return toAjax(sysCasetypeService.updateSysCasetype(sysCasetype));
    }
    /**
     * 修改casetype
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:edit')")
    @Log(title = "casetype", businessType = BusinessType.UPDATE)
    @PostMapping("/only")
    public AjaxResult editCaseTypeOnly(@RequestBody SysCasetype sysCasetype)
    {
        return toAjax(sysCasetypeService.updateSysCasetypeOnly(sysCasetype));
    }

    /**
     * 删除casetype
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:remove')")
    @Log(title = "casetype", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(sysCasetypeService.deleteSysCasetypeByIds(ids));
    }

    /**
     * 删除casetype
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:remove')")
    @Log(title = "casetype", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{caseTypeId}")
    public AjaxResult removeByCaseTypeId(@PathVariable String caseTypeId)
    {
        return toAjax(sysCasetypeService.deleteSysCasetypeByCaseTypeId(caseTypeId));
    }
}
