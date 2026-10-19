package com.ruoyi.crm.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.crm.domain.dto.wecom.WecomMomentCancelTaskRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCommentsRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCommentsResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCreateTaskRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCreateTaskResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCustomerListRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentCustomerListResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentListRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentListResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentTaskRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomMomentTaskResponse;
import com.ruoyi.crm.service.ICrmWecomMaterialService;
import com.ruoyi.crm.service.ICrmWecomMomentService;

/**
 * 企业微信客户朋友圈代理服务实现。
 * 透传调用企微 API，不落库。
 */
@Service
public class CrmWecomMomentServiceImpl implements ICrmWecomMomentService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomMomentServiceImpl.class);

    private static final String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact";

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ICrmWecomMaterialService crmWecomMaterialService;

    private static final HttpHeaders JSON_HEADERS;

    static
    {
        JSON_HEADERS = new HttpHeaders();
        JSON_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public WecomMomentListResponse getMomentList(String accessToken, WecomMomentListRequest request)
    {
        String url = BASE_URL + "/get_moment_list?access_token=" + accessToken;
        return postForEntity(url, request, WecomMomentListResponse.class);
    }

    @Override
    public WecomMomentTaskResponse getMomentTask(String accessToken, WecomMomentTaskRequest request)
    {
        String url = BASE_URL + "/get_moment_task?access_token=" + accessToken;
        return postForEntity(url, request, WecomMomentTaskResponse.class);
    }

    @Override
    public WecomMomentCustomerListResponse getMomentCustomerList(String accessToken,
            WecomMomentCustomerListRequest request)
    {
        String url = BASE_URL + "/get_moment_customer_list?access_token=" + accessToken;
        return postForEntity(url, request, WecomMomentCustomerListResponse.class);
    }

    @Override
    public WecomMomentCustomerListResponse getMomentSendResult(String accessToken,
            WecomMomentCustomerListRequest request)
    {
        String url = BASE_URL + "/get_moment_send_result?access_token=" + accessToken;
        return postForEntity(url, request, WecomMomentCustomerListResponse.class);
    }

    @Override
    public WecomMomentCommentsResponse getMomentComments(String accessToken, WecomMomentCommentsRequest request)
    {
        String url = BASE_URL + "/get_moment_comments?access_token=" + accessToken;
        return postForEntity(url, request, WecomMomentCommentsResponse.class);
    }

    @Override
    public WecomMomentCreateTaskResponse addMomentTask(String accessToken, WecomMomentCreateTaskRequest request)
    {
        String url = BASE_URL + "/add_moment_task?access_token=" + accessToken;
        return postForEntity(url, request, WecomMomentCreateTaskResponse.class);
    }

    @Override
    public WecomMomentCreateTaskResponse getMomentTaskResult(String accessToken, String jobid)
    {
        String url = BASE_URL + "/get_moment_task_result?access_token=" + accessToken + "&jobid=" + jobid;
        ResponseEntity<WecomMomentCreateTaskResponse> response =
                restTemplate.getForEntity(url, WecomMomentCreateTaskResponse.class);
        WecomMomentCreateTaskResponse result = response.getBody();
        if (result != null && result.getErrcode() != null && result.getErrcode() != 0)
        {
            log.warn("企业微信 get_moment_task_result 返回错误: errcode={}, errmsg={}",
                    result.getErrcode(), result.getErrmsg());
        }
        return result;
    }

    @Override
    public WecomMomentCreateTaskResponse cancelMomentTask(String accessToken, WecomMomentCancelTaskRequest request)
    {
        String url = BASE_URL + "/cancel_moment_task?access_token=" + accessToken;
        return postForEntity(url, request, WecomMomentCreateTaskResponse.class);
    }

    @Override
    public Map<String, Object> uploadMomentMedia(String accessToken, String mediaType, MultipartFile file)
    {
        try
        {
            // 1. 写入素材库（attachment_type=1 朋友圈），状态 pending
            com.ruoyi.crm.domain.CrmWecomMaterial material =
                    crmWecomMaterialService.upload(file, file.getOriginalFilename(), mediaType, 1);
            // 2. 上传到企业微信获取 media_id
            material = crmWecomMaterialService.uploadToWecom(material.getId());

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("errcode", 0);
            result.put("errmsg", "ok");
            result.put("media_id", material.getMediaId());
            result.put("type", material.getMediaType());
            result.put("material_id", material.getId());
            return result;
        }
        catch (com.ruoyi.common.exception.ServiceException e)
        {
            log.error("上传朋友圈附件失败", e);
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("errcode", -1);
            error.put("errmsg", "上传失败: " + e.getMessage());
            return error;
        }
    }

    private <T> T postForEntity(String url, Object body, Class<T> responseType)
    {
        HttpEntity<Object> entity = new HttpEntity<>(body, JSON_HEADERS);
        ResponseEntity<T> response = restTemplate.postForEntity(url, entity, responseType);
        T result = response.getBody();
        logMomentError(result);
        return result;
    }

    private void logMomentError(Object result)
    {
        if (result instanceof WecomMomentListResponse)
        {
            WecomMomentListResponse r = (WecomMomentListResponse) result;
            if (r != null && r.getErrcode() != null && r.getErrcode() != 0)
            {
                log.warn("企业微信朋友圈 API 返回错误: errcode={}, errmsg={}", r.getErrcode(), r.getErrmsg());
            }
        }
        else if (result instanceof WecomMomentTaskResponse)
        {
            WecomMomentTaskResponse r = (WecomMomentTaskResponse) result;
            if (r != null && r.getErrcode() != null && r.getErrcode() != 0)
            {
                log.warn("企业微信朋友圈 API 返回错误: errcode={}, errmsg={}", r.getErrcode(), r.getErrmsg());
            }
        }
        else if (result instanceof WecomMomentCustomerListResponse)
        {
            WecomMomentCustomerListResponse r = (WecomMomentCustomerListResponse) result;
            if (r != null && r.getErrcode() != null && r.getErrcode() != 0)
            {
                log.warn("企业微信朋友圈 API 返回错误: errcode={}, errmsg={}", r.getErrcode(), r.getErrmsg());
            }
        }
        else if (result instanceof WecomMomentCommentsResponse)
        {
            WecomMomentCommentsResponse r = (WecomMomentCommentsResponse) result;
            if (r != null && r.getErrcode() != null && r.getErrcode() != 0)
            {
                log.warn("企业微信朋友圈 API 返回错误: errcode={}, errmsg={}", r.getErrcode(), r.getErrmsg());
            }
        }
        else if (result instanceof WecomMomentCreateTaskResponse)
        {
            WecomMomentCreateTaskResponse r = (WecomMomentCreateTaskResponse) result;
            if (r != null && r.getErrcode() != null && r.getErrcode() != 0)
            {
                log.warn("企业微信朋友圈 API 返回错误: errcode={}, errmsg={}", r.getErrcode(), r.getErrmsg());
            }
        }
    }
}
