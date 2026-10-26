package com.ruoyi.crm.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.vo.PublicWebsiteContentCardVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteContentDetailVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteContentListVo;
import com.ruoyi.crm.mapper.CrmMarketingContentMapper;
import com.ruoyi.crm.service.ICrmPublicTenantWebsiteContentService;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@Service
public class CrmPublicTenantWebsiteContentServiceImpl implements ICrmPublicTenantWebsiteContentService
{
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    @Autowired
    private PublicTenantSiteResolver publicTenantSiteResolver;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private CrmMarketingContentMapper contentMapper;

    @Override
    public PublicWebsiteContentListVo listContents(String slug, Integer limit)
    {
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(slug);
        int effectiveLimit = normalizeLimit(limit);
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            List<PublicWebsiteContentCardVo> items = contentMapper.selectPublicWebsiteVisible(effectiveLimit);
            PublicWebsiteContentListVo vo = new PublicWebsiteContentListVo();
            vo.setItems(items != null ? items : Collections.emptyList());
            return vo;
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public PublicWebsiteContentDetailVo getContent(String slug, String contentId)
    {
        if (StringUtils.isEmpty(contentId))
        {
            throw new ServiceException("content_not_found", HttpStatus.NOT_FOUND);
        }
        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(slug);
        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            CrmMarketingContent content = contentMapper.selectPublicById(contentId.trim());
            if (content == null)
            {
                throw new ServiceException("content_not_found", HttpStatus.NOT_FOUND);
            }
            return toDetailVo(content);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private static PublicWebsiteContentDetailVo toDetailVo(CrmMarketingContent content)
    {
        PublicWebsiteContentDetailVo vo = new PublicWebsiteContentDetailVo();
        vo.setId(content.getId());
        vo.setTitle(content.getTitle());
        vo.setAuthor(content.getAuthor());
        vo.setDigest(content.getDigest());
        vo.setCoverUrl(content.getCoverUrl());
        vo.setContentMd(content.getContentMd());
        vo.setContentHtml(content.getContentHtml());
        vo.setWebsitePublishedAt(content.getWebsitePublishedAt());
        vo.setUpdateTime(content.getUpdateTime());
        return vo;
    }

    private static int normalizeLimit(Integer limit)
    {
        if (limit == null || limit <= 0)
        {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
