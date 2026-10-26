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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmWecomGroupReminder;
import com.ruoyi.crm.domain.CrmWecomGroupReminderRule;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupReminderReportRequest;
import com.ruoyi.crm.service.wecom.contactgroup.WecomGroupReminderService;

/**
 * 企微群提醒规则与投递队列
 */
@RestController
@RequestMapping("/crm/wecom")
public class CrmWecomGroupReminderController extends BaseController
{
    @Autowired
    private WecomGroupReminderService reminderService;

    @PreAuthorize("@ss.hasPermi('crm:wecom:reminderrule:query')")
    @GetMapping("/group-reminder-rule/list")
    public TableDataInfo listRules(CrmWecomGroupReminderRule query)
    {
        startPage();
        List<CrmWecomGroupReminderRule> list = reminderService.listRules(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:reminderrule:query')")
    @GetMapping("/group-reminder-rule/{ruleId}")
    public AjaxResult getRule(@PathVariable String ruleId)
    {
        return AjaxResult.success(reminderService.getRule(ruleId));
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:reminderrule:add')")
    @Log(title = "群提醒规则", businessType = BusinessType.INSERT)
    @PostMapping("/group-reminder-rule")
    public AjaxResult addRule(@RequestBody CrmWecomGroupReminderRule body)
    {
        body.setCreateBy(SecurityUtils.getUsername());
        body.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(reminderService.insertRule(body));
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:reminderrule:edit')")
    @Log(title = "群提醒规则", businessType = BusinessType.UPDATE)
    @PutMapping("/group-reminder-rule")
    public AjaxResult editRule(@RequestBody CrmWecomGroupReminderRule body)
    {
        body.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(reminderService.updateRule(body));
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:reminderrule:remove')")
    @Log(title = "群提醒规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/group-reminder-rule/{ruleId}")
    public AjaxResult removeRule(@PathVariable String ruleId)
    {
        return toAjax(reminderService.deleteRule(ruleId));
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:reminder:list')")
    @GetMapping("/group-reminder/list")
    public TableDataInfo listReminders(CrmWecomGroupReminder query)
    {
        startPage();
        List<CrmWecomGroupReminder> list = reminderService.listReminders(query);
        return getDataTable(list);
    }

    /** Internal: 调试拉取（非 MVP 主投递；主路径为 StaffNotify Job） */
    @GetMapping("/group-reminder/pending")
    public AjaxResult pending(@RequestParam(required = false) Integer limit)
    {
        return AjaxResult.success(reminderService.pollPendingAcrossTenants(limit));
    }

    /** Internal: aibot report */
    @PostMapping("/group-reminder/report")
    public AjaxResult report(@RequestBody WecomGroupReminderReportRequest body)
    {
        reminderService.report(body);
        return AjaxResult.success();
    }

    /**
     * H5 发送页上下文（OAuth JWT）；细权在 Service（owner 或 list 权限）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/group-reminder/{id}/send-context")
    public AjaxResult sendContext(@PathVariable String id)
    {
        return AjaxResult.success(reminderService.getSendContext(id));
    }
}
