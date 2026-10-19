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
import com.ruoyi.ems.domain.SysReWorkload;
import com.ruoyi.ems.service.ISysReWorkloadService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工作量Controller
 * 
 * @author luohd
 * @date 2023-05-22
 */
@RestController
@RequestMapping("/ems/workload")
public class SysReWorkloadController extends BaseController
{
    @Autowired
    private ISysReWorkloadService sysReWorkloadService;

    /**
     * 查询工作量列表
     */
    @PreAuthorize("@ss.hasPermi('ems:workload:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReWorkload sysReWorkload)
    {
        startPage();
        List<SysReWorkload> list = sysReWorkloadService.selectSysReWorkloadList(sysReWorkload);
        return getDataTable(list);
    }

    /**
     * 导出工作量列表
     */
    @PreAuthorize("@ss.hasPermi('ems:workload:export')")
    @Log(title = "工作量", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReWorkload sysReWorkload)
    {
        List<SysReWorkload> list = sysReWorkloadService.selectSysReWorkloadList(sysReWorkload);
        ExcelUtil<SysReWorkload> util = new ExcelUtil<SysReWorkload>(SysReWorkload.class);
        util.exportExcel(response, list, "工作量数据");
    }

    /**
     * 获取工作量详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:workload:query')")
    @GetMapping(value = "/{workloadId}")
    public AjaxResult getInfo(@PathVariable("workloadId") String workloadId)
    {
        return success(sysReWorkloadService.selectSysReWorkloadByWorkloadId(workloadId));
    }

    /**
     * 新增工作量
     */
    @PreAuthorize("@ss.hasPermi('ems:workload:add')")
    @Log(title = "工作量", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReWorkload sysReWorkload)
    {
        return toAjax(sysReWorkloadService.insertSysReWorkload(sysReWorkload));
    }

    /**
     * 修改工作量
     */
    @PreAuthorize("@ss.hasPermi('ems:workload:edit')")
    @Log(title = "工作量", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReWorkload sysReWorkload)
    {
        return toAjax(sysReWorkloadService.updateSysReWorkload(sysReWorkload));
    }

    /**
     * 删除工作量
     */
    @PreAuthorize("@ss.hasPermi('ems:workload:remove')")
    @Log(title = "工作量", businessType = BusinessType.DELETE)
	@DeleteMapping("/{workloadIds}")
    public AjaxResult remove(@PathVariable String[] workloadIds)
    {
        return toAjax(sysReWorkloadService.deleteSysReWorkloadByWorkloadIds(workloadIds));
    }
}
