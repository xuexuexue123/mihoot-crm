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
import com.ruoyi.crm.domain.dto.FollowupRuleSaveRequest;
import com.ruoyi.crm.domain.vo.FollowupRuleEnrollmentVo;
import com.ruoyi.crm.domain.vo.FollowupRuleVo;
import com.ruoyi.crm.service.ICrmFollowupRuleService;

/**
 * 回访规则
 */
@RestController
@RequestMapping("/crm/followuprule")
public class CrmFollowupRuleController extends BaseController
{
    @Autowired
    private ICrmFollowupRuleService crmFollowupRuleService;

    @PreAuthorize("@ss.hasPermi('crm:followuprule:query')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(value = "triggerType", required = false) String triggerType,
            @RequestParam(value = "enabled", required = false) String enabled)
    {
        startPage();
        List<FollowupRuleVo> list = crmFollowupRuleService.selectList(tenant, triggerType, enabled);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:followuprule:query')")
    @GetMapping("/enabled-catalog-keys")
    public AjaxResult enabledCatalogKeys(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(crmFollowupRuleService.selectEnabledCatalogKeys(tenant));
    }

    @PreAuthorize("@ss.hasPermi('crm:followuprule:query')")
    @GetMapping("/enrollment/list")
    public TableDataInfo enrollmentList(@RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(value = "ruleId", required = false) String ruleId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "subjectId", required = false) String subjectId)
    {
        startPage();
        List<FollowupRuleEnrollmentVo> list = crmFollowupRuleService.selectEnrollmentList(tenant, ruleId, status,
                subjectId);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:followuprule:query')")
    @GetMapping("/{ruleId}")
    public AjaxResult getById(@PathVariable("ruleId") String ruleId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(crmFollowupRuleService.getById(tenant, ruleId));
    }

    @PreAuthorize("@ss.hasPermi('crm:followuprule:add')")
    @Log(title = "回访规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FollowupRuleSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmFollowupRuleService.add(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:followuprule:edit')")
    @Log(title = "回访规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody FollowupRuleSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmFollowupRuleService.update(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:followuprule:remove')")
    @Log(title = "回访规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ruleId}")
    public AjaxResult remove(@PathVariable("ruleId") String ruleId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmFollowupRuleService.remove(tenant, ruleId);
        return success();
    }
}
