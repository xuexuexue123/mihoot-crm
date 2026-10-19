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
import com.ruoyi.ems.domain.SysAudittrail;
import com.ruoyi.ems.service.ISysAudittrailService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * case历史Controller
 * 
 * @author luohd
 * @date 2023-01-06
 */
@RestController
@RequestMapping("/ems/audittrail")
public class SysAudittrailController extends BaseController
{
    @Autowired
    private ISysAudittrailService sysAudittrailService;

    /**
     * 查询case历史列表
     */
    @PreAuthorize("@ss.hasPermi('ems:audittrail:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysAudittrail sysAudittrail)
    {
        startPage();
        List<SysAudittrail> list = sysAudittrailService.selectSysAudittrailList(sysAudittrail);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ems:audittrail:list')")
    @GetMapping("/listByCase/{pyCaseId}")
    public TableDataInfo listByCase(@PathVariable("pyCaseId") String pyCaseId)
    {
//        startPage();
        List<SysAudittrail> list = sysAudittrailService.selectSysAudittrailByCaseId(pyCaseId);
        return getDataTable(list);
    }

    /**
     * 导出case历史列表
     */
    @PreAuthorize("@ss.hasPermi('ems:audittrail:export')")
    @Log(title = "case历史", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysAudittrail sysAudittrail)
    {
        List<SysAudittrail> list = sysAudittrailService.selectSysAudittrailList(sysAudittrail);
        ExcelUtil<SysAudittrail> util = new ExcelUtil<SysAudittrail>(SysAudittrail.class);
        util.exportExcel(response, list, "case历史数据");
    }

    /**
     * 获取case历史详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:audittrail:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(sysAudittrailService.selectSysAudittrailById(id));
    }

    /**
     * 新增case历史
     */
    @PreAuthorize("@ss.hasPermi('ems:audittrail:add')")
    @Log(title = "case历史", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysAudittrail sysAudittrail)
    {
        return toAjax(sysAudittrailService.insertSysAudittrail(sysAudittrail));
    }

    /**
     * 修改case历史
     */
    @PreAuthorize("@ss.hasPermi('ems:audittrail:edit')")
    @Log(title = "case历史", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysAudittrail sysAudittrail)
    {
        return toAjax(sysAudittrailService.updateSysAudittrail(sysAudittrail));
    }

    /**
     * 删除case历史
     */
    @PreAuthorize("@ss.hasPermi('ems:audittrail:remove')")
    @Log(title = "case历史", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(sysAudittrailService.deleteSysAudittrailByIds(ids));
    }
}
