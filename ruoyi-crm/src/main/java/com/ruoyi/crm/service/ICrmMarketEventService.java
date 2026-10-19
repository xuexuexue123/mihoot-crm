package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmMarketEvent;
import com.ruoyi.crm.domain.dto.MarketEventBatchRequest;
import com.ruoyi.crm.domain.dto.MarketEventBatchResult;

/**
 * 市场活动 Service
 */
public interface ICrmMarketEventService
{
    CrmMarketEvent selectCrmMarketEventById(String id);

    List<CrmMarketEvent> selectCrmMarketEventList(CrmMarketEvent query);

    CrmMarketEvent insertCrmMarketEvent(CrmMarketEvent row);

    CrmMarketEvent updateCrmMarketEvent(CrmMarketEvent row);

    int deleteCrmMarketEventById(String id);

    MarketEventBatchResult batch(MarketEventBatchRequest request);
}
