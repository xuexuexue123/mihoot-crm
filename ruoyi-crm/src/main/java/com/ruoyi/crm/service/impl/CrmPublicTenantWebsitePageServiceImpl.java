package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmTenantWebsitePage;
import com.ruoyi.crm.domain.vo.PublicTenantWebsitePageVo;
import com.ruoyi.crm.domain.vo.TenantWebsitePageSummaryVo;
import com.ruoyi.crm.mapper.CrmTenantWebsitePageMapper;
import com.ruoyi.crm.service.ICrmPublicTenantWebsitePageService;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.TenantWebsitePageVoSupport;
import com.ruoyi.crm.service.support.TenantWebsitePathUtils;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebsitePreviewTokenService;
import com.ruoyi.crm.service.support.WebsitePreviewTokenService.PreviewClaims;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@Service
public class CrmPublicTenantWebsitePageServiceImpl implements ICrmPublicTenantWebsitePageService
{
    @Autowired
    private PublicTenantSiteResolver publicTenantSiteResolver;

    @Autowired
    private TenantWebsitePathUtils pathUtils;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private CrmTenantWebsitePageMapper pageMapper;

    @Autowired
    private WebsitePreviewTokenService previewTokenService;

    @Override
    public PublicTenantWebsitePageVo getPublishedByPath(String slug, String path)
    {
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(slug);
        String normalizedPath = pathUtils.normalizePath(path);
        pathUtils.validatePathQuery(normalizedPath);

        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            CrmTenantWebsitePage page = pageMapper.selectPublishedByPath(normalizedPath);
            if (page == null)
            {
                throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
            }
            return toPublicVo(page, page.getPublishedSchema());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public List<TenantWebsitePageSummaryVo> listPublished(String slug)
    {
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(slug);
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            return pageMapper.selectPublishedSummaryList();
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public PublicTenantWebsitePageVo getPreview(String slug, String path, String token)
    {
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(slug);
        String normalizedPath = pathUtils.normalizePath(path);
        pathUtils.validatePathQuery(normalizedPath);

        PreviewClaims claims = previewTokenService.verify(token, slug, normalizedPath, site.getTenantName());

        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            CrmTenantWebsitePage page = pageMapper.selectById(claims.getPageId());
            if (page == null || !page.getPath().equals(normalizedPath))
            {
                throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
            }
            if (StringUtils.isBlank(page.getDraftSchema()))
            {
                throw new ServiceException("Not found", HttpStatus.NOT_FOUND);
            }
            return toPublicVo(page, page.getDraftSchema());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private PublicTenantWebsitePageVo toPublicVo(CrmTenantWebsitePage page, String schemaJson)
    {
        PublicTenantWebsitePageVo vo = new PublicTenantWebsitePageVo();
        vo.setPath(page.getPath());
        vo.setTitle(page.getTitle());
        vo.setSchema(TenantWebsitePageVoSupport.parseSchema(schemaJson));
        vo.setPublishedAt(TenantWebsitePageVoSupport.toIso8601(page.getPublishedAt()));
        return vo;
    }
}
