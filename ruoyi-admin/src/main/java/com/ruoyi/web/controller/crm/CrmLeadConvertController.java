package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.dto.LeadConvertConfigUpsertDto;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateRequest;
import com.ruoyi.crm.domain.dto.LeadConvertRequest;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;
import com.ruoyi.crm.service.ICrmLeadConvertService;

/**
 * Lead 转 Contact 原子 API
 */
@RestController
@RequestMapping("/crm/lead")
public class CrmLeadConvertController extends BaseController
{
    @Autowired
    private ICrmLeadConvertService leadConvertService;

    @Autowired
    private ICrmLeadConvertConfigService leadConvertConfigService;

    @PreAuthorize("@ss.hasPermi('crm:lead:convert')")
    @Log(title = "线索转化", businessType = BusinessType.UPDATE)
    @PostMapping("/{leadId}/convert")
    public AjaxResult convert(@PathVariable("leadId") String leadId,
            @RequestBody LeadConvertRequest request)
    {
        return success(leadConvertService.convert(leadId, request, SecurityUtils.getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:convert') or @ss.hasPermi('crm:lead:query')")
    @PostMapping("/{leadId}/convert/evaluate")
    public AjaxResult evaluate(@PathVariable("leadId") String leadId,
            @RequestBody LeadConvertEvaluateRequest request)
    {
        return success(leadConvertService.evaluate(leadId, request));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:convert:config')")
    @GetMapping("/convert/config")
    public AjaxResult getConfig(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(leadConvertConfigService.getConfig(tenant));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:convert:config')")
    @Log(title = "线索转化配置", businessType = BusinessType.UPDATE)
    @PutMapping("/convert/config")
    public AjaxResult upsertConfig(@RequestBody LeadConvertConfigUpsertDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(leadConvertConfigService.upsert(tenant, body));
    }
}
