package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.dto.EventAudienceQuery;
import com.ruoyi.crm.domain.vo.EventAudienceOpenIdResultVo;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.domain.vo.EventAudienceSummaryVo;

/**
 * 活动受众聚合
 */
public interface IEventAudienceService
{
    EventAudienceSummaryVo getSummary(String tenantId, String eventId);

    List<EventAudienceRowVo> listAudience(String tenantId, String eventId, EventAudienceQuery query);

    long countAudience(String tenantId, String eventId, EventAudienceQuery query);

    EventAudienceOpenIdResultVo resolveMpOpenIds(String tenantId, String eventId);

    int countByAudienceStatus(String tenantId, String eventId, String audienceStatus);

    Map<String, java.util.Date> resolveFirstTouchByContactId(String tenantId, String eventId);

    EventAudienceRowVo findByAudienceKey(String tenantId, String eventId, String audienceKey);

    EventAudienceRowVo findByLeadId(String tenantId, String eventId, String leadId);
}
