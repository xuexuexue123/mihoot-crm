package com.ruoyi.crm.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventPerformanceSnapshot;
import com.ruoyi.crm.service.support.EventTrendDailyRow;
import com.ruoyi.crm.service.support.WecomChannelContactRow;

/**
 * 活动效果与 ROI 聚合查询
 */
public interface EventPerformanceMapper
{
    List<String> selectAttributedContactIds(@Param("eventId") String eventId);

    List<String> selectH5AttributedContactIds(@Param("eventId") String eventId);

    List<String> selectMpAttributedContactIds(@Param("eventId") String eventId);

    List<WecomChannelContactRow> selectWecomContactsByEvent(@Param("eventId") String eventId);

    List<EventTrendDailyRow> selectTrendTouchDaily(@Param("eventId") String eventId,
            @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    List<EventTrendDailyRow> selectTrendH5RegistrationDaily(@Param("eventId") String eventId,
            @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    List<EventTrendDailyRow> selectTrendLeadReleasedDaily(@Param("eventId") String eventId,
            @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    List<EventTrendDailyRow> selectTrendContactConvertedDaily(@Param("eventId") String eventId,
            @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    List<CrmEvent> selectEventsForPerformanceList(@Param("stage") String stage,
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo,
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countEventsForPerformanceList(@Param("stage") String stage,
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo);

    List<CrmEventPerformanceSnapshot> selectSnapshotsForList(@Param("stage") String stage,
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo,
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countSnapshotsForList(@Param("stage") String stage,
            @Param("dateFrom") String dateFrom,
            @Param("dateTo") String dateTo);

    int upsertPerformanceSnapshot(CrmEventPerformanceSnapshot snapshot);

    List<String> selectAllEventIds();

    BigDecimal selectWindowBookedClv(@Param("contactId") String contactId,
            @Param("windowStart") Date windowStart, @Param("windowEnd") Date windowEnd);

    BigDecimal selectWindowInflightClv(@Param("contactId") String contactId,
            @Param("windowStart") Date windowStart, @Param("windowEnd") Date windowEnd,
            @Param("closedWonStages") List<String> closedWonStages);

    BigDecimal selectWindowRefundClv(@Param("contactId") String contactId,
            @Param("windowStart") Date windowStart, @Param("windowEnd") Date windowEnd);

    Date selectContactConversionTime(@Param("eventId") String eventId, @Param("contactId") String contactId);
}
