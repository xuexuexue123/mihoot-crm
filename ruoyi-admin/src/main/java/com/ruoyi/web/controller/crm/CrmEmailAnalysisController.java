package com.ruoyi.web.controller.crm;

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
import com.ruoyi.crm.domain.dto.EmailAnalysisUpsertDto;
import com.ruoyi.crm.domain.vo.EmailAnalysisVo;
import com.ruoyi.crm.service.ICrmEmailAnalysisService;

/**
 * 邮件 AI 分析结果
 */
@RestController
@RequestMapping("/crm/emailanalysis")
public class CrmEmailAnalysisController extends BaseController
{
    @Autowired
    private ICrmEmailAnalysisService crmEmailAnalysisService;

    @PreAuthorize("@ss.hasPermi('crm:emailanalysis:query')")
    @GetMapping("/latest")
    public AjaxResult getLatest(
            @RequestParam String messageExternalId,
            @RequestParam String locale,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String externalName,
            @RequestParam(required = false) String bodyHash,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        EmailAnalysisVo data = crmEmailAnalysisService.getLatest(
                tenant, messageExternalId, locale, provider, externalName, bodyHash);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:emailanalysis:edit')")
    @Log(title = "邮件AI分析", businessType = BusinessType.UPDATE)
    @PostMapping
    public AjaxResult upsert(@RequestBody EmailAnalysisUpsertDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        EmailAnalysisVo data = crmEmailAnalysisService.upsert(tenant, body);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:emailanalysis:remove')")
    @Log(title = "邮件AI分析", businessType = BusinessType.DELETE)
    @DeleteMapping("/{analysisId}")
    public AjaxResult remove(@PathVariable String analysisId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmEmailAnalysisService.remove(tenant, analysisId);
        return success();
    }
}
