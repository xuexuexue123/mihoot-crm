package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.vo.PublicTenantWebsitePageVo;
import com.ruoyi.crm.domain.vo.TenantWebsitePageSummaryVo;

public interface ICrmPublicTenantWebsitePageService
{
    PublicTenantWebsitePageVo getPublishedByPath(String slug, String path);

    List<TenantWebsitePageSummaryVo> listPublished(String slug);

    PublicTenantWebsitePageVo getPreview(String slug, String path, String token);
}
