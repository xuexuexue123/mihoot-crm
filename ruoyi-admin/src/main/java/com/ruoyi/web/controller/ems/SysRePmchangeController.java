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
import com.ruoyi.ems.domain.SysRePmchange;
import com.ruoyi.ems.service.ISysRePmchangeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目变更Controller
 * 
 * @author luohd
 * @date 2023-05-19
 */
@RestController
@RequestMapping("/ems/pmchange")
public class SysRePmchangeController extends BaseController
{
    @Autowired
    private ISysRePmchangeService sysRePmchangeService;

    /**
     * 查询项目变更列表
     */
    @PreAuthorize("@ss.hasPermi('ems:pmchange:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRePmchange sysRePmchange)
    {
        startPage();
        List<SysRePmchange> list = sysRePmchangeService.selectSysRePmchangeList(sysRePmchange);
        return getDataTable(list);
    }

    /**
     * 导出项目变更列表
     */
    @PreAuthorize("@ss.hasPermi('ems:pmchange:export')")
    @Log(title = "项目变更", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRePmchange sysRePmchange)
    {
        List<SysRePmchange> list = sysRePmchangeService.selectSysRePmchangeList(sysRePmchange);
        ExcelUtil<SysRePmchange> util = new ExcelUtil<SysRePmchange>(SysRePmchange.class);
        util.exportExcel(response, list, "项目变更数据");
    }

    /**
     * 获取项目变更详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:pmchange:query')")
    @GetMapping(value = "/{changeId}")
    public AjaxResult getInfo(@PathVariable("changeId") String changeId)
    {
        return success(sysRePmchangeService.selectSysRePmchangeByChangeId(changeId));
    }

    /**
     * 新增项目变更
     */
    @PreAuthorize("@ss.hasPermi('ems:pmchange:add')")
    @Log(title = "项目变更", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysRePmchange sysRePmchange)
    {
        return toAjax(sysRePmchangeService.insertSysRePmchange(sysRePmchange));
    }

    /**
     * 修改项目变更
     */
    @PreAuthorize("@ss.hasPermi('ems:pmchange:edit')")
    @Log(title = "项目变更", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysRePmchange sysRePmchange)
    {
        return toAjax(sysRePmchangeService.updateSysRePmchange(sysRePmchange));
    }

    /**
     * 删除项目变更
     */
    @PreAuthorize("@ss.hasPermi('ems:pmchange:remove')")
    @Log(title = "项目变更", businessType = BusinessType.DELETE)
	@DeleteMapping("/{changeIds}")
    public AjaxResult remove(@PathVariable String[] changeIds)
    {
        return toAjax(sysRePmchangeService.deleteSysRePmchangeByChangeIds(changeIds));
    }
}
