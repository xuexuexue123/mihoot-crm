package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMarketingContentPublish;

public interface CrmMarketingContentPublishMapper
{
    List<CrmMarketingContentPublish> selectByContentId(@Param("contentId") String contentId);

    CrmMarketingContentPublish selectByContentChannelApp(
            @Param("contentId") String contentId,
            @Param("channel") String channel,
            @Param("appId") String appId);

    int insert(CrmMarketingContentPublish publish);

    int update(CrmMarketingContentPublish publish);
}
