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
import com.ruoyi.ems.domain.SysCaseinstance;
import com.ruoyi.ems.service.ISysCaseinstanceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * case实例Controller
 * 
 * @author luohd
 * @date 2022-12-29
 */
@RestController
@RequestMapping("/ems/caseinstance")
public class SysCaseinstanceController extends BaseController
{
    @Autowired
    private ISysCaseinstanceService sysCaseinstanceService;

    /**
     * 查询case实例列表
     */
    @PreAuthorize("@ss.hasPermi('ems:caseinstance:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysCaseinstance sysCaseinstance)
    {
        startPage();
        List<SysCaseinstance> list = sysCaseinstanceService.selectSysCaseinstanceList(sysCaseinstance);
        return getDataTable(list);
    }

    /**
     * 导出case实例列表
     */
    @PreAuthorize("@ss.hasPermi('ems:caseinstance:export')")
    @Log(title = "case实例", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysCaseinstance sysCaseinstance)
    {
        List<SysCaseinstance> list = sysCaseinstanceService.selectSysCaseinstanceList(sysCaseinstance);
        ExcelUtil<SysCaseinstance> util = new ExcelUtil<SysCaseinstance>(SysCaseinstance.class);
        util.exportExcel(response, list, "case实例数据");
    }

    /**
     * 获取case实例详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:caseinstance:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysCaseinstanceService.selectSysCaseinstanceById(id));
    }

    /**
     * 获取case实例详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:caseinstance:query')")
    @GetMapping(value = "/case/{pyCaseId}")
    public AjaxResult getInfoByCaseId(@PathVariable("pyCaseId") String pyCaseId)
    {
        return success(sysCaseinstanceService.selectSysCaseinstanceByCaseId(pyCaseId));
    }

    /**
     * 新增case实例
     */
    @PreAuthorize("@ss.hasPermi('ems:caseinstance:add')")
    @Log(title = "case实例", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysCaseinstance sysCaseinstance)
    {
        return toAjax(sysCaseinstanceService.insertSysCaseinstance(sysCaseinstance));
    }

    /**
     * 修改case实例
     */
    @PreAuthorize("@ss.hasPermi('ems:caseinstance:edit')")
    @Log(title = "case实例", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysCaseinstance sysCaseinstance)
    {
        return toAjax(sysCaseinstanceService.updateSysCaseinstance(sysCaseinstance));
    }

    /**
     * 删除case实例
     */
    @PreAuthorize("@ss.hasPermi('ems:caseinstance:remove')")
    @Log(title = "case实例", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysCaseinstanceService.deleteSysCaseinstanceByIds(ids));
    }
}
