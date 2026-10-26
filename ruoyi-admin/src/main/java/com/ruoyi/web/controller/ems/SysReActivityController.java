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
import com.ruoyi.ems.domain.SysReActivity;
import com.ruoyi.ems.service.ISysReActivityService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * activityController
 * 
 * @author luohd
 * @date 2023-03-13
 */
@RestController
@RequestMapping("/ems/activity")
public class SysReActivityController extends BaseController
{
    @Autowired
    private ISysReActivityService sysReActivityService;

    /**
     * 查询activity列表
     */
    @PreAuthorize("@ss.hasPermi('ems:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReActivity sysReActivity)
    {
        startPage();
        List<SysReActivity> list = sysReActivityService.selectSysReActivityList(sysReActivity);
        return getDataTable(list);
    }

    /**
     * 导出activity列表
     */
    @PreAuthorize("@ss.hasPermi('ems:activity:export')")
    @Log(title = "activity", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReActivity sysReActivity)
    {
        List<SysReActivity> list = sysReActivityService.selectSysReActivityList(sysReActivity);
        ExcelUtil<SysReActivity> util = new ExcelUtil<SysReActivity>(SysReActivity.class);
        util.exportExcel(response, list, "activity数据");
    }

    /**
     * 获取activity详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:activity:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReActivityService.selectSysReActivityByPyId(pyId));
    }

    /**
     * 新增activity
     */
    @PreAuthorize("@ss.hasPermi('ems:activity:add')")
    @Log(title = "activity", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReActivity sysReActivity)
    {
        return toAjax(sysReActivityService.insertSysReActivity(sysReActivity));
    }

    /**
     * 修改activity
     */
    @PreAuthorize("@ss.hasPermi('ems:activity:edit')")
    @Log(title = "activity", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReActivity sysReActivity)
    {
        return toAjax(sysReActivityService.updateSysReActivity(sysReActivity));
    }

    /**
     * 删除activity
     */
    @PreAuthorize("@ss.hasPermi('ems:activity:remove')")
    @Log(title = "activity", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        return toAjax(sysReActivityService.deleteSysReActivityByPyIds(pyIds));
    }
}
