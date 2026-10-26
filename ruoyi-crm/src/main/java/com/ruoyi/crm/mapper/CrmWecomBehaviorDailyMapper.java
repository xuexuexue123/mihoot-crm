package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomBehaviorDaily;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataItem;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorMemberRow;
import com.ruoyi.crm.mapper.CrmWecomStatsMemberNameRow;

public interface CrmWecomBehaviorDailyMapper
{
    int upsertBehaviorDaily(CrmWecomBehaviorDaily row);

    List<WecomBehaviorDataItem> aggregateBehaviorByStatTime(
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("userids") List<String> userids);

    List<WecomBehaviorMemberRow> aggregateBehaviorByMember(
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("userids") List<String> userids);

    List<CrmWecomStatsMemberNameRow> selectDistinctMemberNames();
}
