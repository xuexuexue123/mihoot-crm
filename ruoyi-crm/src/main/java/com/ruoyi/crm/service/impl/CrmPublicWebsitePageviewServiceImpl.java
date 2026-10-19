package com.ruoyi.crm.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.WebsitePageviewRequest;
import com.ruoyi.crm.domain.vo.WebsitePageviewResultVo;
import com.ruoyi.crm.service.ICrmPublicWebsitePageviewService;
import com.ruoyi.crm.service.support.PublicTenantSiteResolver;
import com.ruoyi.crm.service.support.WaitResumeTenantBootstrap;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.tenant.domain.vo.PublicTenantSiteVo;

@Service
public class CrmPublicWebsitePageviewServiceImpl implements ICrmPublicWebsitePageviewService
{
    @Autowired
    private PublicTenantSiteResolver publicTenantSiteResolver;

    @Autowired
    private WaitResumeTenantBootstrap waitResumeTenantBootstrap;

    @Autowired
    private WebsiteVisitorSessionService visitorSessionService;

    @Override
    public WebsitePageviewResultVo acceptPageview(WebsitePageviewRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(request.getTenantSlug()))
        {
            throw new ServiceException("tenantSlug 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(request.getSessionId()))
        {
            throw new ServiceException("sessionId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(request.getPageUrl()))
        {
            throw new ServiceException("pageUrl 不能为空", HttpStatus.BAD_REQUEST);
        }

        PublicTenantSiteVo site = publicTenantSiteResolver.resolveActiveSite(request.getTenantSlug().trim());
        Date viewedAt = WebsiteVisitorSessionService.parseViewedAt(request.getViewedAt());

        try
        {
            waitResumeTenantBootstrap.bindTenantDataSource(site.getTenantName());
            boolean[] result = visitorSessionService.recordPageview(
                    request.getSessionId().trim(),
                    request.getPageUrl().trim(),
                    request.getPageDescription(),
                    request.getReferrer(),
                    viewedAt);
            return new WebsitePageviewResultVo(result[0], result[1]);
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }
}
