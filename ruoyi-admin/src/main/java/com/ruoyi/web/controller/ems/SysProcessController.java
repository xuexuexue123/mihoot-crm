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
import com.ruoyi.ems.domain.SysProcess;
import com.ruoyi.ems.service.ISysProcessService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * processController
 * 
 * @author luohd
 * @date 2022-12-13
 */
@RestController
@RequestMapping("/ems/process")
public class SysProcessController extends BaseController
{
    @Autowired
    private ISysProcessService sysProcessService;

    /**
     * 查询process列表
     */
    @PreAuthorize("@ss.hasPermi('ems:process:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysProcess sysProcess)
    {
        startPage();
        List<SysProcess> list = sysProcessService.selectSysProcessList(sysProcess);
        return getDataTable(list);
    }

    /**
     * 导出process列表
     */
    @PreAuthorize("@ss.hasPermi('ems:process:export')")
    @Log(title = "process", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysProcess sysProcess)
    {
        List<SysProcess> list = sysProcessService.selectSysProcessList(sysProcess);
        ExcelUtil<SysProcess> util = new ExcelUtil<SysProcess>(SysProcess.class);
        util.exportExcel(response, list, "process数据");
    }

    /**
     * 获取process详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:process:query')")
    @GetMapping(value = "/{processId}")
    public AjaxResult getInfo(@PathVariable("processId") String processId)
    {
        return success(sysProcessService.selectSysProcessByProcessId(processId));
    }

    /**
     * 获取process详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:process:query')")
    @GetMapping(value = "/case/{procDefId}")
    public AjaxResult getInfoByDefId(@PathVariable("procDefId") String procDefId)
    {
        return success(sysProcessService.selectSysProcessByProcDefId(procDefId));
    }

    /**
     * 新增process
     */
    @PreAuthorize("@ss.hasPermi('ems:process:add')")
    @Log(title = "process", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysProcess sysProcess)
    {
        return toAjax(sysProcessService.insertSysProcess(sysProcess));
    }

    /**
     * 修改process
     */
    @PreAuthorize("@ss.hasPermi('ems:process:edit')")
    @Log(title = "process", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysProcess sysProcess)
    {
        return toAjax(sysProcessService.updateSysProcess(sysProcess));
    }

    /**
     * 删除process
     */
    @PreAuthorize("@ss.hasPermi('ems:process:remove')")
    @Log(title = "process", businessType = BusinessType.DELETE)
	@DeleteMapping("/{processIds}")
    public AjaxResult remove(@PathVariable String[] processIds)
    {
        return toAjax(sysProcessService.deleteSysProcessByProcessIds(processIds));
    }
}
