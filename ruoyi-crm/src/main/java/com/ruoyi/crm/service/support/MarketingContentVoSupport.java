package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.CrmMarketingContentPublish;
import com.ruoyi.crm.domain.vo.MarketingContentDetailVo;
import com.ruoyi.crm.domain.vo.MarketingContentPublishVo;

/**
 * 营销内容 VO 映射
 */
public final class MarketingContentVoSupport
{
    private MarketingContentVoSupport()
    {
    }

    public static MarketingContentDetailVo toDetailVo(CrmMarketingContent content,
            List<CrmMarketingContentPublish> publishes)
    {
        MarketingContentDetailVo vo = new MarketingContentDetailVo();
        if (content == null)
        {
            return vo;
        }
        vo.setId(content.getId());
        vo.setTitle(content.getTitle());
        vo.setAuthor(content.getAuthor());
        vo.setDigest(content.getDigest());
        vo.setCoverUrl(content.getCoverUrl());
        vo.setContentMd(content.getContentMd());
        vo.setContentHtml(content.getContentHtml());
        vo.setStatus(content.getStatus());
        vo.setWebsiteVisible(content.getWebsiteVisible());
        vo.setWebsitePublishedAt(content.getWebsitePublishedAt());
        vo.setCreateBy(content.getCreateBy());
        vo.setCreateTime(content.getCreateTime());
        vo.setUpdateBy(content.getUpdateBy());
        vo.setUpdateTime(content.getUpdateTime());
        vo.setPublishes(toPublishVoList(publishes));
        return vo;
    }

    public static List<MarketingContentPublishVo> toPublishVoList(List<CrmMarketingContentPublish> publishes)
    {
        List<MarketingContentPublishVo> list = new ArrayList<>();
        if (publishes == null)
        {
            return list;
        }
        for (CrmMarketingContentPublish p : publishes)
        {
            list.add(toPublishVo(p));
        }
        return list;
    }

    public static MarketingContentPublishVo toPublishVo(CrmMarketingContentPublish p)
    {
        if (p == null)
        {
            return null;
        }
        MarketingContentPublishVo vo = new MarketingContentPublishVo();
        vo.setId(p.getId());
        vo.setContentId(p.getContentId());
        vo.setChannel(p.getChannel());
        vo.setAppId(p.getAppId());
        vo.setMediaId(p.getMediaId());
        vo.setPublishId(p.getPublishId());
        vo.setArticleId(p.getArticleId());
        vo.setArticleUrl(p.getArticleUrl());
        vo.setThumbMediaId(p.getThumbMediaId());
        vo.setStatus(p.getStatus());
        vo.setErrorMsg(p.getErrorMsg());
        vo.setPublishedAt(p.getPublishedAt());
        vo.setCreateTime(p.getCreateTime());
        vo.setUpdateTime(p.getUpdateTime());
        return vo;
    }

    public static Date now()
    {
        return new Date();
    }
}
