package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.vo.PublicWebsiteContentDetailVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteContentListVo;

public interface ICrmPublicTenantWebsiteContentService
{
    PublicWebsiteContentListVo listContents(String slug, Integer limit);

    PublicWebsiteContentDetailVo getContent(String slug, String contentId);
}
