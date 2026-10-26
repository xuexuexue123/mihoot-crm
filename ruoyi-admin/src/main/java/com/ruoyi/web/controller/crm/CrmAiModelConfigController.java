package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.AiModelConfigUpsertDto;
import com.ruoyi.crm.domain.vo.AiModelConfigVo;
import com.ruoyi.crm.service.ICrmAiModelConfigService;

/**
 * 租户 AI 模型配置
 */
@RestController
@RequestMapping("/crm/aimodelconfig")
public class CrmAiModelConfigController extends BaseController
{
    @Autowired
    private ICrmAiModelConfigService crmAiModelConfigService;

    @PreAuthorize("@ss.hasPermi('crm:aimodelconfig:query')")
    @GetMapping("/current")
    public AjaxResult getCurrent(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        AiModelConfigVo data = crmAiModelConfigService.getCurrent(tenant);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:aimodelconfig:edit')")
    @Log(title = "AI模型配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult upsert(@RequestBody AiModelConfigUpsertDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        AiModelConfigVo data = crmAiModelConfigService.upsert(tenant, body);
        return success(data);
    }
}
