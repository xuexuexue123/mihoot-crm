package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.dto.StandupActivityRow;
import com.ruoyi.crm.domain.dto.StandupBookingAgendaRow;
import com.ruoyi.crm.domain.dto.StandupBookingCountRow;
import com.ruoyi.crm.domain.dto.StandupContractWinRow;
import com.ruoyi.crm.domain.dto.StandupIdleLeadRow;
import com.ruoyi.crm.domain.dto.StandupStaleOptyRow;

/**
 * 晨会报告专用查询。
 */
public interface StandupMapper
{
    List<StandupBookingCountRow> countBookingsByOwner(@Param("ownerIds") List<String> ownerIds,
            @Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd,
            @Param("includeCancelled") Boolean includeCancelled);

    List<StandupBookingAgendaRow> selectAgendaBookings(@Param("ownerIds") List<String> ownerIds,
            @Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd,
            @Param("includeCancelled") Boolean includeCancelled,
            @Param("userId") Long userId);

    List<StandupContractWinRow> selectWins(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<StandupIdleLeadRow> selectIdleLeads(@Param("ownerIds") List<String> ownerIds,
            @Param("idleHours") int idleHours, @Param("limit") int limit);

    List<StandupIdleLeadRow> selectPoolImminentLeads(@Param("ownerIds") List<String> ownerIds,
            @Param("idleHours") int idleHours, @Param("imminentHours") int imminentHours,
            @Param("limit") int limit);

    List<StandupStaleOptyRow> selectStaleOpties(@Param("ownerIds") List<String> ownerIds,
            @Param("staleDays") int staleDays, @Param("limit") int limit);

    List<StandupActivityRow> selectYesterdayActivities(@Param("ownerId") String ownerId,
            @Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd,
            @Param("limit") int limit);
}
