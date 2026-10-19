package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.AiSkillUpsertDto;
import com.ruoyi.crm.domain.vo.AiSkillVo;
import com.ruoyi.crm.service.ICrmAiSkillService;

/**
 * AI Skill 配置 API
 */
@RestController
@RequestMapping("/crm/ai/skill")
public class CrmAiSkillController extends BaseController
{
    @Autowired
    private ICrmAiSkillService crmAiSkillService;

    @PreAuthorize("@ss.hasPermi('crm:ai:skill:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "skillType", required = false) String skillType,
            @RequestParam(value = "trackKey", required = false) String trackKey)
    {
        List<AiSkillVo> data = crmAiSkillService.list(tenant, domain, skillType, trackKey);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:ai:skill:edit')")
    @Log(title = "AI Skill", businessType = BusinessType.UPDATE)
    @PostMapping
    public AjaxResult upsert(@RequestBody AiSkillUpsertDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        AiSkillVo data = crmAiSkillService.upsert(tenant, body);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:ai:skill:edit')")
    @Log(title = "AI Skill", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmAiSkillService.delete(tenant, id);
        return success();
    }
}
