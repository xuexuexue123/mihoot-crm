package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEventCheckIn;
import com.ruoyi.crm.domain.dto.EventCheckInQuery;
import com.ruoyi.crm.domain.vo.EventCheckInRowVo;
import com.ruoyi.crm.service.support.EventCheckInAggRow;

public interface CrmEventCheckInMapper
{
    CrmEventCheckIn selectBySlot(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("sessionId") String sessionId, @Param("audienceKey") String audienceKey);

    int countDistinctBySession(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("sessionId") String sessionId);

    int countDistinctByEvent(@Param("tenantId") String tenantId, @Param("eventId") String eventId);

    int countByMethod(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("sessionId") String sessionId, @Param("method") String method);

    int countAudienceOnly(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("sessionId") String sessionId);

    int insert(CrmEventCheckIn checkIn);

    List<EventCheckInRowVo> selectCheckInRows(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("query") EventCheckInQuery query);

    List<EventCheckInAggRow> selectCheckInAggByEvent(
            @Param("tenantId") String tenantId, @Param("eventId") String eventId);

    int deleteByEventIds(@Param("eventIds") String[] eventIds);
}
