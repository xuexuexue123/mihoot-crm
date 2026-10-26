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
import com.ruoyi.ems.domain.SysPmLog;
import com.ruoyi.ems.service.ISysPmLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目日志记录Controller
 * 
 * @author luohd
 * @date 2023-05-17
 */
@RestController
@RequestMapping("/ems/pmlog")
public class SysPmLogController extends BaseController
{
    @Autowired
    private ISysPmLogService sysPmLogService;

    /**
     * 查询项目日志记录列表
     */
    @PreAuthorize("@ss.hasPermi('ems:pmlog:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPmLog sysPmLog)
    {
        startPage();
        List<SysPmLog> list = sysPmLogService.selectSysPmLogList(sysPmLog);
        return getDataTable(list);
    }

    /**
     * 导出项目日志记录列表
     */
    @PreAuthorize("@ss.hasPermi('ems:pmlog:export')")
    @Log(title = "项目日志记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPmLog sysPmLog)
    {
        List<SysPmLog> list = sysPmLogService.selectSysPmLogList(sysPmLog);
        ExcelUtil<SysPmLog> util = new ExcelUtil<SysPmLog>(SysPmLog.class);
        util.exportExcel(response, list, "项目日志记录数据");
    }

    /**
     * 获取项目日志记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:pmlog:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return success(sysPmLogService.selectSysPmLogByLogId(logId));
    }

    /**
     * 新增项目日志记录
     */
    @PreAuthorize("@ss.hasPermi('ems:pmlog:add')")
    @Log(title = "项目日志记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysPmLog sysPmLog)
    {
        return toAjax(sysPmLogService.insertSysPmLog(sysPmLog));
    }

    /**
     * 修改项目日志记录
     */
    @PreAuthorize("@ss.hasPermi('ems:pmlog:edit')")
    @Log(title = "项目日志记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysPmLog sysPmLog)
    {
        return toAjax(sysPmLogService.updateSysPmLog(sysPmLog));
    }

    /**
     * 删除项目日志记录
     */
    @PreAuthorize("@ss.hasPermi('ems:pmlog:remove')")
    @Log(title = "项目日志记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(sysPmLogService.deleteSysPmLogByLogIds(logIds));
    }

    /**
     * 删除项目日志记录
     */
    @PreAuthorize("@ss.hasPermi('ems:pmlog:remove')")
    @Log(title = "项目日志记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/task/{taskIds}")
    public AjaxResult removeByTaskIds(@PathVariable String[] taskIds)
    {
        return toAjax(sysPmLogService.deleteSysPmLogByTaskIds(taskIds));
    }
}
