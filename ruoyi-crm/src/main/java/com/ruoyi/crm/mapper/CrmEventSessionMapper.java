package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEventSession;

public interface CrmEventSessionMapper
{
    CrmEventSession selectBySessionId(@Param("tenantId") String tenantId, @Param("sessionId") String sessionId);

    CrmEventSession selectBySessionIdForUpdate(@Param("tenantId") String tenantId, @Param("sessionId") String sessionId);

    List<CrmEventSession> selectByEventId(@Param("tenantId") String tenantId, @Param("eventId") String eventId,
            @Param("status") String status);

    int countActiveByEventId(@Param("tenantId") String tenantId, @Param("eventId") String eventId);

    int insert(CrmEventSession session);

    int update(CrmEventSession session);

    int softDelete(@Param("tenantId") String tenantId, @Param("sessionId") String sessionId,
            @Param("updateBy") String updateBy);

    int deleteByEventIds(@Param("eventIds") String[] eventIds);
}
