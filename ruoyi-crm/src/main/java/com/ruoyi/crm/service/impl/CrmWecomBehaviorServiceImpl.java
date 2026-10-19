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

import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataResponse;
import com.ruoyi.crm.service.ICrmWecomBehaviorService;

/**
 * 企业微信「联系客户统计」行为数据查询服务实现。
 * 直接调用企业微信 get_user_behavior_data API，不涉及本地数据库。
 */
@Service
public class CrmWecomBehaviorServiceImpl implements ICrmWecomBehaviorService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomBehaviorServiceImpl.class);

    private static final String BEHAVIOR_DATA_URL =
            "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_user_behavior_data";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WecomBehaviorDataResponse queryBehaviorData(String accessToken, WecomBehaviorDataRequest request)
    {
        String url = BEHAVIOR_DATA_URL + "?access_token=" + accessToken;

        Map<String, Object> body = new HashMap<>();
        body.put("start_time", request.getStartTime());
        body.put("end_time", request.getEndTime());
        if (request.getUserid() != null && !request.getUserid().isEmpty())
        {
            body.put("userid", request.getUserid());
        }
        if (request.getPartyid() != null && !request.getPartyid().isEmpty())
        {
            body.put("partyid", request.getPartyid());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<WecomBehaviorDataResponse> response =
                restTemplate.postForEntity(url, entity, WecomBehaviorDataResponse.class);
        WecomBehaviorDataResponse result = response.getBody();

        if (result != null && result.getErrcode() != null && result.getErrcode() != 0)
        {
            log.warn("企业微信 get_user_behavior_data 返回错误: errcode={}, errmsg={}",
                    result.getErrcode(), result.getErrmsg());
        }

        return result;
    }

    @Override
    public WecomBehaviorDataResponse queryBehaviorDataForUser(String accessToken, String userid, long startTime,
            long endTime)
    {
        WecomBehaviorDataRequest request = new WecomBehaviorDataRequest();
        request.setUserid(Collections.singletonList(userid));
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        return queryBehaviorData(accessToken, request);
    }
}
