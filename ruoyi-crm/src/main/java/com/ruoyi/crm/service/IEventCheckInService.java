package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.dto.EventCheckInQuery;
import com.ruoyi.crm.domain.dto.EventCheckInRequest;
import com.ruoyi.crm.domain.vo.EventCheckInLookupRowVo;
import com.ruoyi.crm.domain.vo.EventCheckInResultVo;
import com.ruoyi.crm.domain.vo.EventCheckInRowVo;
import com.ruoyi.crm.domain.vo.EventCheckInSummaryVo;
import com.ruoyi.crm.domain.vo.OutreachEventTicketVo;

public interface IEventCheckInService
{
    EventCheckInResultVo checkIn(String tenantId, String eventId, EventCheckInRequest request, String operator);

    EventCheckInResultVo insertCheckInIfAbsent(String tenantId, String eventId, String sessionId,
            String audienceKey, String registrationId, String displayName, String method, String operator);

    EventCheckInSummaryVo getSummary(String tenantId, String eventId, String sessionId);

    List<EventCheckInLookupRowVo> lookup(String tenantId, String eventId, String sessionId, String keyword);

    List<EventCheckInRowVo> listCheckIns(String tenantId, String eventId, EventCheckInQuery query);

    OutreachEventTicketVo getTicketInfo(String ticketToken);
}
