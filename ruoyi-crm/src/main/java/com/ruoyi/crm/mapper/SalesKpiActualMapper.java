package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.dto.SalesKpiActualRow;
import com.ruoyi.crm.domain.dto.SalesKpiOptyCloseRow;

/**
 * KPI 实际值聚合查询。
 */
public interface SalesKpiActualMapper
{
    List<SalesKpiActualRow> sumContractAmount(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    List<SalesKpiActualRow> countWonContracts(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    List<SalesKpiOptyCloseRow> selectClosedOpties(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    List<SalesKpiActualRow> countFollowedCustomers(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("eventTypes") List<String> eventTypes);

    List<SalesKpiActualRow> countMetCustomers(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("eventTypes") List<String> eventTypes,
            @Param("excludedEventTypes") List<String> excludedEventTypes);

    List<SalesKpiActualRow> countAssignedCustomers(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    List<SalesKpiActualRow> countFollowups(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("eventTypes") List<String> eventTypes);

    List<SalesKpiActualRow> countOpties(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    List<SalesKpiActualRow> sumOptyAmount(@Param("ownerIds") List<String> ownerIds,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
