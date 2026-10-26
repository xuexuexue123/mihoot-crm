package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomGroupchatDaily;
import com.ruoyi.crm.domain.dto.wecom.GroupChatDayItem;

public interface CrmWecomGroupchatDailyMapper
{
    int upsertGroupchatDaily(CrmWecomGroupchatDaily row);

    List<GroupChatDayItem> aggregateGroupchatByStatTime(
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("ownerUserids") List<String> ownerUserids);

    List<CrmWecomStatsMemberNameRow> selectDistinctOwnerNames();
}
