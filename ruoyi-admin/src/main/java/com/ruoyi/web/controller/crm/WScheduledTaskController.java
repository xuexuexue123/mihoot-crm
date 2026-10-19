package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.WScheduledTask;
import com.ruoyi.crm.domain.dto.ScheduledTaskClaimDueRequest;
import com.ruoyi.crm.service.IWScheduledTaskService;
import com.ruoyi.crm.service.ScheduledTaskClaimDueService;
import com.ruoyi.framework.security.intranet.IntranetRequestAccess;

/**
 * 工作流定时任务 CRUD + claim-due
 */
@RestController
@RequestMapping("/crm/wscheduledtask")
public class WScheduledTaskController extends BaseController
{
    @Autowired
    private IWScheduledTaskService wScheduledTaskService;

    @Autowired
    private ScheduledTaskClaimDueService scheduledTaskClaimDueService;

    @Autowired
    private IntranetRequestAccess intranetRequestAccess;

    @PreAuthorize("@ss.hasPermi('crm:wscheduledtask:list')")
    @GetMapping("/list")
    public TableDataInfo list(WScheduledTask query)
    {
        startPage();
        List<WScheduledTask> list = wScheduledTaskService.selectWScheduledTaskList(query);
        return getDataTable(list);
    }

    /**
     * Inngest 到期抢占（内网免 Bearer；跨租户）
     */
    @Anonymous
    @PostMapping("/claim-due")
    public AjaxResult claimDue(@RequestBody ScheduledTaskClaimDueRequest request, HttpServletRequest httpRequest)
    {
        intranetRequestAccess.requireTrusted(httpRequest);
        String slot = request != null ? request.getSlot() : null;
        return success(scheduledTaskClaimDueService.claimDue(slot));
    }

    @PreAuthorize("@ss.hasPermi('crm:wscheduledtask:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wScheduledTaskService.selectWScheduledTaskById(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:wscheduledtask:add')")
    @Log(title = "工作流定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WScheduledTask task)
    {
        return toAjax(wScheduledTaskService.insertWScheduledTask(task));
    }

    @PreAuthorize("@ss.hasPermi('crm:wscheduledtask:edit')")
    @Log(title = "工作流定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WScheduledTask task)
    {
        return toAjax(wScheduledTaskService.updateWScheduledTask(task));
    }

    @PreAuthorize("@ss.hasPermi('crm:wscheduledtask:remove')")
    @Log(title = "工作流定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") String id)
    {
        return toAjax(wScheduledTaskService.deleteWScheduledTaskById(id));
    }
}
