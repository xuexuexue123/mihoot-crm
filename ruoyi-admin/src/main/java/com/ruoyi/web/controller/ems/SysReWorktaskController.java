package com.ruoyi.web.controller.ems;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.ems.domain.SysReBoard;
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
import com.ruoyi.ems.domain.SysReWorktask;
import com.ruoyi.ems.service.ISysReWorktaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 任务Controller
 * 
 * @author luohd
 * @date 2023-04-07
 */
@RestController
@RequestMapping("/ems/worktask")
public class SysReWorktaskController extends BaseController
{
    @Autowired
    private ISysReWorktaskService sysReWorktaskService;

    /**
     * 查询任务列表
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReWorktask sysReWorktask)
    {
        startPage();
        List<SysReWorktask> list = sysReWorktaskService.selectSysReWorktaskList(sysReWorktask);
        return getDataTable(list);
    }

    /**
     * 查询任务列表
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:list')")
    @GetMapping("/assignee/list")
    public TableDataInfo listByAssignee(SysReWorktask sysReWorktask)
    {
        startPage();
        String assignee = sysReWorktask.getAssignee();
        List<SysReWorktask> list = sysReWorktaskService.selectSysReWorktaskListByAssignee(assignee);
        return getDataTable(list);
    }

    /**
     * 查询任务列表
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:list')")
    @GetMapping("/assignee/wip")
    public TableDataInfo listByAssigneeWIP(SysReWorktask sysReWorktask)
    {
        startPage();
        String assignee = sysReWorktask.getAssignee();
        List<SysReWorktask> list = sysReWorktaskService.selectSysReWorktaskListByAssigneeWIP(assignee);
        return getDataTable(list);
    }

    /**
     * 查询任务列表
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:list')")
    @GetMapping("/assignee/comp")
    public TableDataInfo listByAssigneeComp(SysReWorktask sysReWorktask)
    {
        startPage();
        String assignee = sysReWorktask.getAssignee();
        List<SysReWorktask> list = sysReWorktaskService.selectSysReWorktaskListByAssigneeComp(assignee);
        return getDataTable(list);
    }

    /**
     * 导出任务列表
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:export')")
    @Log(title = "任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReWorktask sysReWorktask)
    {
        List<SysReWorktask> list = sysReWorktaskService.selectSysReWorktaskList(sysReWorktask);
        ExcelUtil<SysReWorktask> util = new ExcelUtil<SysReWorktask>(SysReWorktask.class);
        util.exportExcel(response, list, "任务数据");
    }

    /**
     * 获取任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") String taskId)
    {
        return success(sysReWorktaskService.selectSysReWorktaskByTaskId(taskId));
    }

    /**
     * 新增任务
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:add')")
    @Log(title = "任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReWorktask sysReWorktask)
    {
        return toAjax(sysReWorktaskService.insertSysReWorktask(sysReWorktask));
    }

    /**
     * 修改任务
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:edit')")
    @Log(title = "任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReWorktask sysReWorktask)
    {
        return toAjax(sysReWorktaskService.updateSysReWorktask(sysReWorktask));
    }

    /**
     * 删除任务
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:remove')")
    @Log(title = "任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable String[] taskIds)
    {
        return toAjax(sysReWorktaskService.deleteSysReWorktaskByTaskIds(taskIds));
    }


    /**
     * 删除任务
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:remove')")
    @Log(title = "任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/board/{boardId}")
    public AjaxResult removeByBoardId(@PathVariable String boardId)
    {
        return toAjax(sysReWorktaskService.deleteSysReWorktaskByBoardId(boardId));
    }

    /**
     * 对传入的worktask进行重新排序
     * @param sysReWorktasks
     * @return
     */
    @PreAuthorize("@ss.hasPermi('ems:worktask:edit')")
    @Log(title = "项目阶段", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/reorder")
    public AjaxResult reorderSysReWorktask(@RequestBody SysReWorktask[] sysReWorktasks) {
        for (int i = 0; i < sysReWorktasks.length; i++) {
            SysReWorktask worktask = sysReWorktasks[i];
            sysReWorktaskService.updateSysReWorktask(worktask);
        }
        return AjaxResult.success("Reorder successfully");
    }
}
