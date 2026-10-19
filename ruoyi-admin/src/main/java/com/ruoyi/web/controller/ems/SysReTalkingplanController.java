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
import com.ruoyi.ems.domain.SysReTalkingplan;
import com.ruoyi.ems.service.ISysReTalkingplanService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 沟通计划Controller
 * 
 * @author luohd
 * @date 2023-05-19
 */
@RestController
@RequestMapping("/ems/talkingplan")
public class SysReTalkingplanController extends BaseController
{
    @Autowired
    private ISysReTalkingplanService sysReTalkingplanService;

    /**
     * 查询沟通计划列表
     */
    @PreAuthorize("@ss.hasPermi('ems:talkingplan:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReTalkingplan sysReTalkingplan)
    {
        startPage();
        List<SysReTalkingplan> list = sysReTalkingplanService.selectSysReTalkingplanList(sysReTalkingplan);
        return getDataTable(list);
    }

    /**
     * 导出沟通计划列表
     */
    @PreAuthorize("@ss.hasPermi('ems:talkingplan:export')")
    @Log(title = "沟通计划", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReTalkingplan sysReTalkingplan)
    {
        List<SysReTalkingplan> list = sysReTalkingplanService.selectSysReTalkingplanList(sysReTalkingplan);
        ExcelUtil<SysReTalkingplan> util = new ExcelUtil<SysReTalkingplan>(SysReTalkingplan.class);
        util.exportExcel(response, list, "沟通计划数据");
    }

    /**
     * 获取沟通计划详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:talkingplan:query')")
    @GetMapping(value = "/{planId}")
    public AjaxResult getInfo(@PathVariable("planId") String planId)
    {
        return success(sysReTalkingplanService.selectSysReTalkingplanByPlanId(planId));
    }

    /**
     * 新增沟通计划
     */
    @PreAuthorize("@ss.hasPermi('ems:talkingplan:add')")
    @Log(title = "沟通计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReTalkingplan sysReTalkingplan)
    {
        return toAjax(sysReTalkingplanService.insertSysReTalkingplan(sysReTalkingplan));
    }

    /**
     * 修改沟通计划
     */
    @PreAuthorize("@ss.hasPermi('ems:talkingplan:edit')")
    @Log(title = "沟通计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReTalkingplan sysReTalkingplan)
    {
        return toAjax(sysReTalkingplanService.updateSysReTalkingplan(sysReTalkingplan));
    }

    /**
     * 删除沟通计划
     */
    @PreAuthorize("@ss.hasPermi('ems:talkingplan:remove')")
    @Log(title = "沟通计划", businessType = BusinessType.DELETE)
	@DeleteMapping("/{planIds}")
    public AjaxResult remove(@PathVariable String[] planIds)
    {
        return toAjax(sysReTalkingplanService.deleteSysReTalkingplanByPlanIds(planIds));
    }
}
