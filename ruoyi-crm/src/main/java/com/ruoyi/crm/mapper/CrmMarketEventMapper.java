package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMarketEvent;

/**
 * 市场活动 Mapper
 */
public interface CrmMarketEventMapper
{
    CrmMarketEvent selectCrmMarketEventById(String id);

    List<CrmMarketEvent> selectCrmMarketEventList(CrmMarketEvent query);

    int insertCrmMarketEvent(CrmMarketEvent row);

    int updateCrmMarketEvent(CrmMarketEvent row);

    int logicalDeleteById(@Param("id") String id, @Param("updateBy") String updateBy);
}
