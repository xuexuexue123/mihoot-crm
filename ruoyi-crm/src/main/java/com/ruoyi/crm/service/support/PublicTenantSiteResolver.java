package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.tenant.util.TenantWebsiteSlugUtils;

/**
 * 租户公开站点 slug 解析（主库）
 */
@Component
public class PublicTenantSiteResolver
{
    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private TenantWebsiteSlugUtils tenantWebsiteSlugUtils;

    public PublicTenantSiteVo resolveActiveSite(String slug)
    {
        if (!TenantWebsiteSlugUtils.isValidFormat(slug) || tenantWebsiteSlugUtils.isReserved(slug))
        {
            throw new ServiceException("Invalid slug", HttpStatus.BAD_REQUEST);
        }
        PublicTenantSiteVo site = masterTenantService.selectPublicByWebsiteSlug(slug);
        if (site == null)
        {
            throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
        }
        if (!"active".equals(site.getWebsiteStatus()))
        {
            throw new ServiceException("Site suspended", HttpStatus.FORBIDDEN);
        }
        return site;
    }
}
