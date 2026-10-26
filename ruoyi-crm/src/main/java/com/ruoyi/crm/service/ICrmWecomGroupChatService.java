package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatDetailResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatListRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatListResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsResponse;

/**
 * 企业微信群聊查询服务（数据统计、列表、详情）。
 */
public interface ICrmWecomGroupChatService
{
    /**
     * 调用企业微信 statistic_group_by_day API 查询群聊数据统计。
     *
     * @param accessToken 企业微信 agent access_token
     * @param request     前端传入的查询请求（owner_filter / day_begin_time / day_end_time）
     * @return 企微 API 原始响应（包含 errcode / errmsg / items）
     */
    WecomGroupChatStatsResponse queryGroupChatStats(String accessToken, WecomGroupChatStatsRequest request);

    /**
     * 单群主群聊统计查询（快照任务用）
     */
    WecomGroupChatStatsResponse queryGroupChatStatsForOwner(String accessToken, String ownerUserid,
            long dayBeginTime, long dayEndTime);

    /**
     * 调用企业微信 groupchat/list API 查询客户群列表（cursor 分页）。
     *
     * @param accessToken 企业微信 agent access_token
     * @param request     前端传入的查询请求（status_filter / owner_filter / cursor / limit）
     * @return 企微 API 原始响应（包含 errcode / errmsg / group_chat_list / next_cursor）
     */
    WecomGroupChatListResponse listGroupChats(String accessToken, WecomGroupChatListRequest request);

    /**
     * 调用企业微信 groupchat/get API 查询客户群详情。
     *
     * @param accessToken 企业微信 agent access_token
     * @param chatId      客户群 ID
     * @param needName    是否需要返回群成员名字，1=需要，0=不需要
     * @return 企微 API 原始响应（包含 errcode / errmsg / group_chat）
     */
    WecomGroupChatDetailResponse getGroupChat(String accessToken, String chatId, Integer needName);
}
