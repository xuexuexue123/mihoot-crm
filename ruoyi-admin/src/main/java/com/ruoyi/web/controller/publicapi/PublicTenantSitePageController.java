package com.ruoyi.web.controller.publicapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.ICrmPublicTenantWebsitePageService;

@RestController
@RequestMapping("/api/public/tenant-sites/{slug}/pages")
public class PublicTenantSitePageController extends BaseController
{
    @Autowired
    private ICrmPublicTenantWebsitePageService publicTenantWebsitePageService;

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping("/by-path")
    public AjaxResult getByPath(@PathVariable String slug, @RequestParam(required = false) String path)
    {
        String resolvedPath = StringUtils.isBlank(path) ? "/" : path;
        return AjaxResult.success(publicTenantWebsitePageService.getPublishedByPath(slug, resolvedPath));
    }

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping
    public AjaxResult listPublished(@PathVariable String slug)
    {
        return AjaxResult.success(publicTenantWebsitePageService.listPublished(slug));
    }

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping("/preview")
    public AjaxResult preview(
            @PathVariable String slug,
            @RequestParam String path,
            @RequestParam String token)
    {
        return AjaxResult.success(publicTenantWebsitePageService.getPreview(slug, path, token));
    }
}
