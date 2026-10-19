package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.vo.ContextSummaryVo;
import com.ruoyi.crm.service.ICrmContextSummaryService;

/**
 * 实体关联摘要（AI Assistant context preload）
 */
@RestController
@RequestMapping("/crm/context")
public class CrmContextSummaryController extends BaseController
{
    @Autowired
    private ICrmContextSummaryService crmContextSummaryService;

    @PreAuthorize("@ss.hasPermi('crm:context:query')")
    @GetMapping("/{entityType}/{entityId}/summary")
    public AjaxResult summary(@PathVariable("entityType") String entityType,
            @PathVariable("entityId") String entityId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        ContextSummaryVo data = crmContextSummaryService.getSummary(tenant, entityType, entityId);
        return success(data);
    }
}
