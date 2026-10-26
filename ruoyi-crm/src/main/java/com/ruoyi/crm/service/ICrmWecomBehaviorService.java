package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataResponse;

/**
 * 企业微信「联系客户统计」行为数据查询服务。
 */
public interface ICrmWecomBehaviorService
{
    /**
     * 调用企业微信 get_user_behavior_data API 查询成员联系客户的行为统计数据。
     *
     * @param accessToken 企业微信 agent access_token
     * @param request     前端传入的查询请求（userid / partyid / start_time / end_time）
     * @return 企微 API 原始响应（包含 errcode / errmsg / behavior_data）
     */
    WecomBehaviorDataResponse queryBehaviorData(String accessToken, WecomBehaviorDataRequest request);

    /**
     * 单成员行为数据查询（快照任务用）
     */
    WecomBehaviorDataResponse queryBehaviorDataForUser(String accessToken, String userid, long startTime, long endTime);
}
