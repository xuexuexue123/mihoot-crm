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
import com.ruoyi.ems.domain.SysReApplication;
import com.ruoyi.ems.service.ISysReApplicationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 应用Controller
 * 
 * @author luohd
 * @date 2023-03-28
 */
@RestController
@RequestMapping("/ems/application")
public class SysReApplicationController extends BaseController
{
    @Autowired
    private ISysReApplicationService sysReApplicationService;

    /**
     * 查询应用列表
     */
    @PreAuthorize("@ss.hasPermi('ems:application:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReApplication sysReApplication)
    {
        startPage();
        List<SysReApplication> list = sysReApplicationService.selectSysReApplicationList(sysReApplication);
        return getDataTable(list);
    }

    /**
     * 导出应用列表
     */
    @PreAuthorize("@ss.hasPermi('ems:application:export')")
    @Log(title = "应用", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReApplication sysReApplication)
    {
        List<SysReApplication> list = sysReApplicationService.selectSysReApplicationList(sysReApplication);
        ExcelUtil<SysReApplication> util = new ExcelUtil<SysReApplication>(SysReApplication.class);
        util.exportExcel(response, list, "应用数据");
    }

    /**
     * 获取应用详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:application:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReApplicationService.selectSysReApplicationByPyId(pyId));
    }

    /**
     * 新增应用
     */
    @PreAuthorize("@ss.hasPermi('ems:application:add')")
    @Log(title = "应用", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReApplication sysReApplication)
    {
        return toAjax(sysReApplicationService.insertSysReApplication(sysReApplication));
    }

    /**
     * 修改应用
     */
    @PreAuthorize("@ss.hasPermi('ems:application:edit')")
    @Log(title = "应用", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReApplication sysReApplication)
    {
        return toAjax(sysReApplicationService.updateSysReApplication(sysReApplication));
    }

    /**
     * 删除应用
     */
    @PreAuthorize("@ss.hasPermi('ems:application:remove')")
    @Log(title = "应用", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        return toAjax(sysReApplicationService.deleteSysReApplicationByPyIds(pyIds));
    }
}
