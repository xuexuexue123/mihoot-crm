package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ruoyi.crm.domain.dto.SemanticProfileSaveRequest;
import com.ruoyi.crm.domain.vo.SemanticProfileVO;
import com.ruoyi.crm.service.ICrmSemanticProfileService;

/**
 * 租户语义层配置
 */
@RestController
@RequestMapping("/crm/semanticprofile")
public class CrmSemanticProfileController extends BaseController
{
    @Autowired
    private ICrmSemanticProfileService crmSemanticProfileService;

    @PreAuthorize("@ss.hasPermi('crm:semanticprofile:query')")
    @GetMapping("/current")
    public AjaxResult getCurrent(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        SemanticProfileVO data = crmSemanticProfileService.getCurrent(tenant);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:semanticprofile:edit')")
    @Log(title = "语义层配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult upsert(@RequestBody SemanticProfileSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmSemanticProfileService.upsert(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:semanticprofile:edit')")
    @Log(title = "语义层配置", businessType = BusinessType.DELETE)
    @DeleteMapping
    public AjaxResult deleteCurrent(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmSemanticProfileService.deleteCurrent(tenant);
        return success();
    }
}
