package com.ruoyi.crm.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatDetailResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatListRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatListResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsResponse;
import com.ruoyi.crm.service.ICrmWecomGroupChatService;

/**
 * 企业微信「群聊」查询服务实现。
 * 直接调用企业微信 API（statistic_group_by_day / groupchat/list / groupchat/get），不涉及本地数据库。
 */
@Service
public class CrmWecomGroupChatServiceImpl implements ICrmWecomGroupChatService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomGroupChatServiceImpl.class);

    private static final String GROUPCHAT_STATS_URL =
            "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/statistic_group_by_day";

    private static final String GROUPCHAT_LIST_URL =
            "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/list";

    private static final String GROUPCHAT_GET_URL =
            "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/get";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WecomGroupChatStatsResponse queryGroupChatStats(String accessToken, WecomGroupChatStatsRequest request)
    {
        String url = GROUPCHAT_STATS_URL + "?access_token=" + accessToken;

        Map<String, Object> ownerFilter = new HashMap<>();
        WecomGroupChatStatsRequest.OwnerFilter of = request.getOwnerFilter();
        ownerFilter.put("userid_list", of.getUseridList());

        Map<String, Object> body = new HashMap<>();
        body.put("owner_filter", ownerFilter);
        body.put("day_begin_time", request.getDayBeginTime());
        body.put("day_end_time", request.getDayEndTime());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<WecomGroupChatStatsResponse> response =
                restTemplate.postForEntity(url, entity, WecomGroupChatStatsResponse.class);
        WecomGroupChatStatsResponse result = response.getBody();

        if (result != null && result.getErrcode() != null && result.getErrcode() != 0)
        {
            log.warn("企业微信 statistic_group_by_day 返回错误: errcode={}, errmsg={}",
                    result.getErrcode(), result.getErrmsg());
        }

        return result;
    }

    @Override
    public WecomGroupChatStatsResponse queryGroupChatStatsForOwner(String accessToken, String ownerUserid,
            long dayBeginTime, long dayEndTime)
    {
        WecomGroupChatStatsRequest request = new WecomGroupChatStatsRequest();
        WecomGroupChatStatsRequest.OwnerFilter ownerFilter = new WecomGroupChatStatsRequest.OwnerFilter();
        ownerFilter.setUseridList(Collections.singletonList(ownerUserid));
        request.setOwnerFilter(ownerFilter);
        request.setDayBeginTime(dayBeginTime);
        request.setDayEndTime(dayEndTime);
        return queryGroupChatStats(accessToken, request);
    }

    @Override
    public WecomGroupChatListResponse listGroupChats(String accessToken, WecomGroupChatListRequest request)
    {
        String url = GROUPCHAT_LIST_URL + "?access_token=" + accessToken;

        Map<String, Object> body = new HashMap<>();
        if (request.getStatusFilter() != null)
        {
            body.put("status_filter", request.getStatusFilter());
        }
        if (request.getOwnerFilter() != null && request.getOwnerFilter().getUseridList() != null)
        {
            Map<String, Object> ownerFilter = new HashMap<>();
            ownerFilter.put("userid_list", request.getOwnerFilter().getUseridList());
            body.put("owner_filter", ownerFilter);
        }
        if (request.getCursor() != null)
        {
            body.put("cursor", request.getCursor());
        }
        body.put("limit", request.getLimit());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<WecomGroupChatListResponse> response =
                restTemplate.postForEntity(url, entity, WecomGroupChatListResponse.class);
        WecomGroupChatListResponse result = response.getBody();

        if (result != null && result.getErrcode() != null && result.getErrcode() != 0)
        {
            log.warn("企业微信 groupchat/list 返回错误: errcode={}, errmsg={}",
                    result.getErrcode(), result.getErrmsg());
        }
        log.info("企业微信 groupchat/list 请求参数: {}", body);
        if (result != null && result.getGroupChatList() != null && !result.getGroupChatList().isEmpty())
        {
            log.info("企业微信 groupchat/list 返回结果: chatId={}, status={}, nextCursor={}",
                    result.getGroupChatList().get(0).getChatId(),
                    result.getGroupChatList().get(0).getStatus(),
                    result.getNextCursor());
        }

        return result;
    }

    @Override
    public WecomGroupChatDetailResponse getGroupChat(String accessToken, String chatId, Integer needName)
    {
        String url = GROUPCHAT_GET_URL + "?access_token=" + accessToken;

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("need_name", needName != null ? needName : 1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<WecomGroupChatDetailResponse> response =
                restTemplate.postForEntity(url, entity, WecomGroupChatDetailResponse.class);
        WecomGroupChatDetailResponse result = response.getBody();

        if (result != null && result.getErrcode() != null && result.getErrcode() != 0)
        {
            log.warn("企业微信 groupchat/get 返回错误: errcode={}, errmsg={}",
                    result.getErrcode(), result.getErrmsg());
        }

        return result;
    }
}
