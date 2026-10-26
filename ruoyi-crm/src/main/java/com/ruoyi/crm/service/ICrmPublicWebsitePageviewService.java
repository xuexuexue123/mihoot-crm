package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.WebsitePageviewRequest;
import com.ruoyi.crm.domain.vo.WebsitePageviewResultVo;

/**
 * 官网公开 Pageview 实时上报
 */
public interface ICrmPublicWebsitePageviewService
{
    WebsitePageviewResultVo acceptPageview(WebsitePageviewRequest request);
}
