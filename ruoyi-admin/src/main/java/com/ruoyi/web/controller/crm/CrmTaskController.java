package com.ruoyi.web.controller.crm;

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
import com.ruoyi.crm.domain.CrmTask;
import com.ruoyi.crm.service.ICrmTaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 任务Controller
 * 
 * @author luohd
 * @date 2024-01-21
 */
@RestController
@RequestMapping("/crm/task")
public class CrmTaskController extends BaseController
{
    @Autowired
    private ICrmTaskService crmTaskService;

    /**
     * 查询任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmTask crmTask)
    {
        startPage();
        List<CrmTask> list = crmTaskService.selectCrmTaskList(crmTask);
        return getDataTable(list);
    }

    /**
     * 选择不等于指定状态的任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:list')")
    @GetMapping("/uncompeletedlist")
    public TableDataInfo uncompeletedlist(CrmTask crmTask)
    {
        startPage();
        List<CrmTask> list = crmTaskService.selectCrmTaskListUncompleted(crmTask);
        return getDataTable(list);
    }

    /**
     * 选择本周不等于指定状态的任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:list')")
    @GetMapping("/uncompeletedlistweekly")
    public TableDataInfo uncompeletedlistweekly(CrmTask crmTask)
    {
        startPage();
        List<CrmTask> list = crmTaskService.selectCrmTaskListUncompletedWeekly(crmTask);
        return getDataTable(list);
    }

    /**
     * 选择下周不等于指定状态的任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:list')")
    @GetMapping("/uncompeletedlistnextweekly")
    public TableDataInfo uncompeletedlistnextweekly(CrmTask crmTask)
    {
        startPage();
        List<CrmTask> list = crmTaskService.selectCrmTaskListUncompletedNextWeekly(crmTask);
        return getDataTable(list);
    }
    /**
     * 选择本月不等于指定状态的任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:list')")
    @GetMapping("/uncompeletedlistmonthly")
    public TableDataInfo uncompeletedlistmonthly(CrmTask crmTask)
    {
        startPage();
        List<CrmTask> list = crmTaskService.selectCrmTaskListUncompletedMonthly(crmTask);
        return getDataTable(list);
    }

    /**
     * 选择所有关联的DD任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:list')")
    @GetMapping("/listdd")
    public TableDataInfo listdd(CrmTask crmTask)
    {
        startPage();
        List<CrmTask> list = crmTaskService.selectCrmTaskListDD(crmTask);
        return getDataTable(list);
    }


    /**
     * 选择本月等于指定状态的任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:list')")
    @GetMapping("/compeletedlistmonthly")
    public TableDataInfo compeletedlistmonthly(CrmTask crmTask)
    {
        startPage();
        List<CrmTask> list = crmTaskService.selectCrmTaskListCompletedMonthly(crmTask);
        return getDataTable(list);
    }

    /**
     * 选择本月等于指定状态的任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:list')")
    @GetMapping("/relatedtasks/{relatedToids}")
    public TableDataInfo relatedtasks(@PathVariable String[] relatedToids)
    {
//        startPage();
        List<CrmTask> list = crmTaskService.selectCrmTaskListByTaskIds(relatedToids);
        return getDataTable(list);
    }

    /**
     * 导出任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:export')")
    @Log(title = "任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmTask crmTask)
    {
        List<CrmTask> list = crmTaskService.selectCrmTaskList(crmTask);
        ExcelUtil<CrmTask> util = new ExcelUtil<CrmTask>(CrmTask.class);
        util.exportExcel(response, list, "任务数据");
    }

    /**
     * 导出所有非系统任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:export')")
    @Log(title = "任务", businessType = BusinessType.EXPORT)
    @PostMapping("/exportnotsystem")
    public void exportnotsystem(HttpServletResponse response, CrmTask crmTask)
    {
        List<CrmTask> list = crmTaskService.selectCrmTaskListNotsystem(crmTask);
        ExcelUtil<CrmTask> util = new ExcelUtil<CrmTask>(CrmTask.class);
        util.exportExcel(response, list, "任务数据");
    }


    /**
     * 导出所有非系统任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:task:export')")
    @Log(title = "任务", businessType = BusinessType.EXPORT)
    @PostMapping("/exportdd")
    public void exportdd(HttpServletResponse response, CrmTask crmTask)
    {
        List<CrmTask> list = crmTaskService.selectCrmTaskListDD(crmTask);
        ExcelUtil<CrmTask> util = new ExcelUtil<CrmTask>(CrmTask.class);
        util.exportExcel(response, list, "任务数据");
    }
    /**
     * 获取任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:task:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") String taskId)
    {
        return success(crmTaskService.selectCrmTaskByTaskId(taskId));
    }

    /**
     * 新增任务
     */
    @PreAuthorize("@ss.hasPermi('crm:task:add')")
    @Log(title = "任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmTask crmTask)
    {
        return toAjax(crmTaskService.insertCrmTask(crmTask));
    }

    /**
     * 修改任务
     */
    @PreAuthorize("@ss.hasPermi('crm:task:edit')")
    @Log(title = "任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmTask crmTask)
    {
        return toAjax(crmTaskService.updateCrmTask(crmTask));
    }

    /**
     * 删除任务
     */
    @PreAuthorize("@ss.hasPermi('crm:task:remove')")
    @Log(title = "任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable String[] taskIds)
    {
        return toAjax(crmTaskService.deleteCrmTaskByTaskIds(taskIds));
    }
}
