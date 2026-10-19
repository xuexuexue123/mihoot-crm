package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.CreateTenantWebsitePageRequest;
import com.ruoyi.crm.domain.dto.SaveTenantWebsitePageDraftRequest;
import com.ruoyi.crm.service.ICrmTenantWebsitePageService;

@RestController
@RequestMapping("/crm/tenant-website/pages")
public class CrmTenantWebsitePageController extends BaseController
{
    @Autowired
    private ICrmTenantWebsitePageService tenantWebsitePageService;

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:list')")
    @GetMapping
    public AjaxResult list()
    {
        return AjaxResult.success(tenantWebsitePageService.listSummaries());
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable String id)
    {
        return AjaxResult.success(tenantWebsitePageService.getDetail(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:add')")
    @Log(title = "官网页面", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody CreateTenantWebsitePageRequest request)
    {
        return new AjaxResult(HttpStatus.CREATED, "success", tenantWebsitePageService.create(request));
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:edit')")
    @Log(title = "官网页面草稿", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult saveDraft(@PathVariable String id, @RequestBody SaveTenantWebsitePageDraftRequest request)
    {
        tenantWebsitePageService.saveDraft(id, request);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:publish')")
    @Log(title = "官网页面发布", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/publish")
    public AjaxResult publish(@PathVariable String id)
    {
        return AjaxResult.success(tenantWebsitePageService.publish(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:publish')")
    @Log(title = "官网页面取消发布", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/unpublish")
    public AjaxResult unpublish(@PathVariable String id)
    {
        return AjaxResult.success(tenantWebsitePageService.unpublish(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:publish')")
    @PostMapping("/{id}/preview-token")
    public AjaxResult previewToken(@PathVariable String id)
    {
        return AjaxResult.success(tenantWebsitePageService.issuePreviewToken(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:tenantwebsite:remove')")
    @Log(title = "官网页面", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable String id)
    {
        tenantWebsitePageService.delete(id);
        return AjaxResult.success();
    }
}
