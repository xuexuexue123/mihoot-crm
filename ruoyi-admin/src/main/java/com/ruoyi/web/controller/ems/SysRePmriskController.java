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
import com.ruoyi.ems.domain.SysRePmrisk;
import com.ruoyi.ems.service.ISysRePmriskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目风险Controller
 * 
 * @author luohd
 * @date 2023-05-19
 */
@RestController
@RequestMapping("/ems/pmrisk")
public class SysRePmriskController extends BaseController
{
    @Autowired
    private ISysRePmriskService sysRePmriskService;

    /**
     * 查询项目风险列表
     */
    @PreAuthorize("@ss.hasPermi('ems:pmrisk:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRePmrisk sysRePmrisk)
    {
        startPage();
        List<SysRePmrisk> list = sysRePmriskService.selectSysRePmriskList(sysRePmrisk);
        return getDataTable(list);
    }

    /**
     * 导出项目风险列表
     */
    @PreAuthorize("@ss.hasPermi('ems:pmrisk:export')")
    @Log(title = "项目风险", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRePmrisk sysRePmrisk)
    {
        List<SysRePmrisk> list = sysRePmriskService.selectSysRePmriskList(sysRePmrisk);
        ExcelUtil<SysRePmrisk> util = new ExcelUtil<SysRePmrisk>(SysRePmrisk.class);
        util.exportExcel(response, list, "项目风险数据");
    }

    /**
     * 获取项目风险详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:pmrisk:query')")
    @GetMapping(value = "/{riskId}")
    public AjaxResult getInfo(@PathVariable("riskId") String riskId)
    {
        return success(sysRePmriskService.selectSysRePmriskByRiskId(riskId));
    }

    /**
     * 新增项目风险
     */
    @PreAuthorize("@ss.hasPermi('ems:pmrisk:add')")
    @Log(title = "项目风险", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysRePmrisk sysRePmrisk)
    {
        return toAjax(sysRePmriskService.insertSysRePmrisk(sysRePmrisk));
    }

    /**
     * 修改项目风险
     */
    @PreAuthorize("@ss.hasPermi('ems:pmrisk:edit')")
    @Log(title = "项目风险", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysRePmrisk sysRePmrisk)
    {
        return toAjax(sysRePmriskService.updateSysRePmrisk(sysRePmrisk));
    }

    /**
     * 删除项目风险
     */
    @PreAuthorize("@ss.hasPermi('ems:pmrisk:remove')")
    @Log(title = "项目风险", businessType = BusinessType.DELETE)
	@DeleteMapping("/{riskIds}")
    public AjaxResult remove(@PathVariable String[] riskIds)
    {
        return toAjax(sysRePmriskService.deleteSysRePmriskByRiskIds(riskIds));
    }
}
