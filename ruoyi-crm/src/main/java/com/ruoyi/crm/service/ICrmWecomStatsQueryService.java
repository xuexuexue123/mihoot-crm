package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsBackfillResult;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsMemberRow;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorMemberAggregation;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsResponse;

/**
 * 企业微信统计数据本地快照查询
 */
public interface ICrmWecomStatsQueryService
{
    WecomBehaviorDataResponse queryBehaviorFromSnapshot(WecomBehaviorDataRequest request);

    WecomBehaviorMemberAggregation queryBehaviorByMember(WecomBehaviorDataRequest request);

    WecomGroupChatStatsResponse queryGroupchatFromSnapshot(WecomGroupChatStatsRequest request);

    List<CrmWecomStatsMemberRow> listHistoricalMembers();
}
