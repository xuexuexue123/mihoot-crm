package com.ruoyi.web.controller.publicapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.tenant.util.TenantWebsiteSlugUtils;

/**
 * 租户公开子域名站点元数据（Phase 1，仅查主库）
 */
@RestController
@RequestMapping("/api/public/tenant-sites")
public class PublicTenantSiteController extends BaseController
{
    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private TenantWebsiteSlugUtils tenantWebsiteSlugUtils;

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping("/{slug}")
    public AjaxResult getBySlug(@PathVariable String slug)
    {
        if (!TenantWebsiteSlugUtils.isValidFormat(slug) || tenantWebsiteSlugUtils.isReserved(slug))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "Invalid slug");
        }
        PublicTenantSiteVo site = masterTenantService.selectPublicByWebsiteSlug(slug);
        if (site == null)
        {
            return AjaxResult.error(HttpStatus.NOT_FOUND, "Not found");
        }
        if (!"active".equals(site.getWebsiteStatus()))
        {
            return AjaxResult.error(HttpStatus.FORBIDDEN, "Site suspended");
        }
        return AjaxResult.success(site);
    }
}
