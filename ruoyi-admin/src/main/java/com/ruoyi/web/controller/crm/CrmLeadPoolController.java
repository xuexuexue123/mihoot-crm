package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.domain.dto.CrmLeadConvertedQuery;
import com.ruoyi.crm.domain.dto.CrmLeadPoolQuery;
import com.ruoyi.crm.domain.dto.LeadAssignRequest;
import com.ruoyi.crm.domain.dto.LeadBatchAssignRequest;
import com.ruoyi.crm.domain.dto.LeadReleaseRequest;
import com.ruoyi.crm.service.ICrmLeadPoolConfigService;
import com.ruoyi.crm.service.ICrmLeadPoolService;

@RestController
@RequestMapping("/crm/lead")
public class CrmLeadPoolController extends BaseController
{
    @Autowired
    private ICrmLeadPoolService leadPoolService;

    @Autowired
    private ICrmLeadPoolConfigService poolConfigService;

    @Autowired
    private ObjectMapper objectMapper;

    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/pool")
    public TableDataInfo pool(CrmLeadPoolQuery query)
    {
        startPage();
        return getDataTable(leadPoolService.selectPoolList(query));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/convertedlist")
    public TableDataInfo convertedlist(CrmLeadConvertedQuery query)
    {
        startPage();
        return getDataTable(leadPoolService.selectConvertedList(query));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:claim')")
    @Log(title = "线索领取", businessType = BusinessType.UPDATE)
    @PostMapping("/{leadId}/claim")
    public AjaxResult claim(@PathVariable String leadId)
    {
        CrmLead lead = leadPoolService.claim(leadId);
        return success(lead);
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:release')")
    @Log(title = "线索释放", businessType = BusinessType.UPDATE)
    @PostMapping("/{leadId}/release")
    public AjaxResult release(@PathVariable String leadId, @RequestBody(required = false) LeadReleaseRequest request)
    {
        return success(leadPoolService.release(leadId, request, false));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:reclaim')")
    @Log(title = "线索强制回收", businessType = BusinessType.UPDATE)
    @PostMapping("/{leadId}/reclaim")
    public AjaxResult reclaim(@PathVariable String leadId, @RequestBody(required = false) LeadReleaseRequest request)
    {
        return success(leadPoolService.release(leadId, request, true));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:assign')")
    @Log(title = "线索指派", businessType = BusinessType.UPDATE)
    @PostMapping("/{leadId}/assign")
    public AjaxResult assign(@PathVariable String leadId, @RequestBody LeadAssignRequest request)
    {
        return success(leadPoolService.assign(leadId, request));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:assign')")
    @Log(title = "线索批量指派", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-assign")
    public AjaxResult batchAssign(@RequestBody LeadBatchAssignRequest request)
    {
        return success(leadPoolService.batchAssign(request));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/pool/config")
    public AjaxResult poolConfig()
    {
        return success(poolConfigService.getConfig());
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:assign')")
    @Log(title = "公海配置", businessType = BusinessType.UPDATE)
    @PutMapping("/pool/config")
    public AjaxResult savePoolConfig(@RequestBody JsonNode body)
    {
        CrmLeadPoolConfig config = body == null || body.isNull()
                ? null
                : objectMapper.convertValue(body, CrmLeadPoolConfig.class);
        boolean qualifyTemplateIdPresent = body != null && body.has("qualifyTemplateId");
        return success(poolConfigService.saveConfig(config, qualifyTemplateIdPresent));
    }
}
