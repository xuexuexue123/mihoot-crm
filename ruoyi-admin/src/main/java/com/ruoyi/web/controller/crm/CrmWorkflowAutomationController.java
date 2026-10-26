package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.WorkflowAutomationSaveRequest;
import com.ruoyi.crm.domain.vo.WorkflowAutomationVO;
import com.ruoyi.crm.service.ICrmWorkflowAutomationService;

/**
 * 工作流自动化配置
 */
@RestController
@RequestMapping("/crm/workflowautomation")
public class CrmWorkflowAutomationController extends BaseController
{
    @Autowired
    private ICrmWorkflowAutomationService crmWorkflowAutomationService;

    @PreAuthorize("@ss.hasPermi('crm:workflowautomation:query')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        startPage();
        List<WorkflowAutomationVO> list = crmWorkflowAutomationService.selectWorkflowAutomationList(tenant);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:workflowautomation:query')")
    @GetMapping("/key/{automationKey}")
    public AjaxResult getByKey(@PathVariable("automationKey") String automationKey,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        WorkflowAutomationVO data = crmWorkflowAutomationService.getByKey(tenant, automationKey);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:workflowautomation:add')")
    @Log(title = "工作流自动化配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WorkflowAutomationSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmWorkflowAutomationService.add(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:workflowautomation:edit')")
    @Log(title = "工作流自动化配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult upsert(@RequestBody WorkflowAutomationSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmWorkflowAutomationService.upsert(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:workflowautomation:remove')")
    @Log(title = "工作流自动化配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/key/{automationKey}")
    public AjaxResult remove(@PathVariable("automationKey") String automationKey,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmWorkflowAutomationService.removeByKey(tenant, automationKey);
        return success();
    }
}
