package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.dto.MarketingContentQuery;
import com.ruoyi.crm.domain.vo.MarketingContentListVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteContentCardVo;

public interface CrmMarketingContentMapper
{
    List<MarketingContentListVo> selectList(MarketingContentQuery query);

    CrmMarketingContent selectById(@Param("id") String id);

    List<PublicWebsiteContentCardVo> selectPublicWebsiteVisible(@Param("limit") int limit);

    CrmMarketingContent selectPublicById(@Param("id") String id);

    int insert(CrmMarketingContent content);

    int update(CrmMarketingContent content);

    int deleteById(@Param("id") String id);
}
