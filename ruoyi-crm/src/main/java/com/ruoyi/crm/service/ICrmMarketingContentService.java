package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.dto.MarketingContentPublishRefreshRequest;
import com.ruoyi.crm.domain.dto.MarketingContentPublishRequest;
import com.ruoyi.crm.domain.dto.MarketingContentQuery;
import com.ruoyi.crm.domain.vo.MarketingContentDetailVo;
import com.ruoyi.crm.domain.vo.MarketingContentListVo;
import com.ruoyi.crm.domain.vo.MarketingContentPublishResultVo;
import com.ruoyi.crm.domain.vo.MarketingContentPublishVo;

public interface ICrmMarketingContentService
{
    List<MarketingContentListVo> selectList(MarketingContentQuery query);

    MarketingContentDetailVo getDetail(String id);

    MarketingContentDetailVo create(CrmMarketingContent request);

    MarketingContentDetailVo update(CrmMarketingContent request);

    void delete(String id);

    MarketingContentPublishResultVo publish(String id, MarketingContentPublishRequest request);

    MarketingContentPublishVo refreshPublish(String id, MarketingContentPublishRefreshRequest request);

    List<MarketingContentPublishVo> listPublishes(String id);

    MarketingContentDetailVo publishToWebsite(String id);

    MarketingContentDetailVo unpublishFromWebsite(String id);
}
