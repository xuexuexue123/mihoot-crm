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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmRecallPolicy;
import com.ruoyi.crm.domain.CrmRecallSchedule;
import com.ruoyi.crm.domain.dto.RecallPolicySaveRequest;
import com.ruoyi.crm.service.ICrmRecallPolicyService;

/**
 * 复诊策略 / 日程
 */
@RestController
@RequestMapping("/crm")
public class CrmRecallPolicyController extends BaseController
{
    @Autowired
    private ICrmRecallPolicyService crmRecallPolicyService;

    @PreAuthorize("@ss.hasPermi('crm:recallpolicy:query')")
    @GetMapping("/recallpolicy/list")
    public TableDataInfo list(@RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(value = "enabled", required = false) String enabled)
    {
        startPage();
        List<CrmRecallPolicy> list = crmRecallPolicyService.selectList(tenant, enabled);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:recallpolicy:query')")
    @GetMapping("/recallpolicy/{policyId}")
    public AjaxResult getById(@PathVariable("policyId") String policyId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(crmRecallPolicyService.getById(tenant, policyId));
    }

    @PreAuthorize("@ss.hasPermi('crm:recallpolicy:add')")
    @Log(title = "复诊策略", businessType = BusinessType.INSERT)
    @PostMapping("/recallpolicy")
    public AjaxResult add(@RequestBody RecallPolicySaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmRecallPolicyService.add(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:recallpolicy:edit')")
    @Log(title = "复诊策略", businessType = BusinessType.UPDATE)
    @PutMapping("/recallpolicy")
    public AjaxResult update(@RequestBody RecallPolicySaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmRecallPolicyService.update(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:recallpolicy:remove')")
    @Log(title = "复诊策略", businessType = BusinessType.DELETE)
    @DeleteMapping("/recallpolicy/{policyId}")
    public AjaxResult remove(@PathVariable("policyId") String policyId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmRecallPolicyService.remove(tenant, policyId);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:recallpolicy:query')")
    @GetMapping("/recallschedule/list")
    public TableDataInfo scheduleList(@RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(value = "contactId", required = false) String contactId,
            @RequestParam(value = "status", required = false) String status)
    {
        startPage();
        List<CrmRecallSchedule> list = crmRecallPolicyService.selectScheduleList(tenant, contactId, status);
        return getDataTable(list);
    }
}
