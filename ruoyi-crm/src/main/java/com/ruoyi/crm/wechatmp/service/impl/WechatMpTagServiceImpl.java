package com.ruoyi.crm.wechatmp.service.impl;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagBatchRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagCreateRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagUpdateRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpTagService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

/**
 * 微信公众号标签管理服务实现 —— 实时透传微信 API
 *
 * @author luohd
 * @date 2026-07-17
 */
@Service
public class WechatMpTagServiceImpl implements IWechatMpTagService
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpTagServiceImpl.class);

    private static final String WX_API_BASE = "https://api.weixin.qq.com/cgi-bin";

    private static final int MAX_BATCH_OPENID = 50;

    @Autowired
    private WechatMpTokenService tokenService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public AjaxResult listTags(String appId)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        String url = WX_API_BASE + "/tags/get?access_token=" + token;

        try
        {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            JSONObject wxResp = JSON.parseObject(resp.getBody());

            if (wxResp.containsKey("errcode"))
            {
                int errcode = wxResp.getIntValue("errcode");
                if (errcode != 0)
                {
                    log.error("getTags failed: errcode={}, errmsg={}", errcode, wxResp.getString("errmsg"));
                    return AjaxResult.error("获取标签列表失败: " + wxResp.getString("errmsg"));
                }
            }

            JSONArray tags = wxResp.getJSONArray("tags");
            return AjaxResult.success(tags != null ? tags : Collections.emptyList());
        }
        catch (Exception e)
        {
            log.error("getTags exception: {}", e.getMessage(), e);
            return AjaxResult.error("获取标签列表失败: " + e.getMessage());
        }
    }

    @Override
    public AjaxResult createTag(String appId, WechatMpTagCreateRequest request)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }
        if (request == null || !StringUtils.hasText(request.getName()))
        {
            return AjaxResult.error("标签名不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        String url = WX_API_BASE + "/tags/create?access_token=" + token;

        JSONObject tagObj = new JSONObject();
        tagObj.put("name", request.getName());

        JSONObject body = new JSONObject();
        body.put("tag", tagObj);

        return postJson(url, body, "创建标签失败");
    }

    @Override
    public AjaxResult updateTag(String appId, WechatMpTagUpdateRequest request)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }
        if (request == null || request.getId() == null || !StringUtils.hasText(request.getName()))
        {
            return AjaxResult.error("标签ID和名称不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        String url = WX_API_BASE + "/tags/update?access_token=" + token;

        JSONObject tagObj = new JSONObject();
        tagObj.put("id", request.getId());
        tagObj.put("name", request.getName());

        JSONObject body = new JSONObject();
        body.put("tag", tagObj);

        return postJson(url, body, "编辑标签失败");
    }

    @Override
    public AjaxResult deleteTag(String appId, Long tagId)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }
        if (tagId == null)
        {
            return AjaxResult.error("tagId 不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        String url = WX_API_BASE + "/tags/delete?access_token=" + token;

        JSONObject tagObj = new JSONObject();
        tagObj.put("id", tagId);

        JSONObject body = new JSONObject();
        body.put("tag", tagObj);

        return postJson(url, body, "删除标签失败");
    }

    @Override
    public AjaxResult batchTagging(String appId, WechatMpTagBatchRequest request)
    {
        return doBatchTag(appId, request, "batchtagging", "批量打标失败");
    }

    @Override
    public AjaxResult batchUntagging(String appId, WechatMpTagBatchRequest request)
    {
        return doBatchTag(appId, request, "batchuntagging", "批量取消标签失败");
    }

    @Override
    public AjaxResult getUserTags(String appId, String openId)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(openId))
        {
            return AjaxResult.error("appId 和 openId 不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        String url = WX_API_BASE + "/tags/getidlist?access_token=" + token;

        JSONObject body = new JSONObject();
        body.put("openid", openId);

        JSONObject wxResp = postJsonAndGetBody(url, body, "获取用户标签失败");
        if (wxResp == null)
        {
            return AjaxResult.error("获取用户标签失败");
        }

        JSONArray tagidList = wxResp.getJSONArray("tagid_list");
        return AjaxResult.success(tagidList != null ? tagidList : Collections.emptyList());
    }

    @Override
    public AjaxResult getTagFans(String appId, Long tagId, String nextOpenId)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }
        if (tagId == null)
        {
            return AjaxResult.error("tagId 不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        String url = WX_API_BASE + "/user/tag/get?access_token=" + token;

        JSONObject body = new JSONObject();
        body.put("tagid", tagId);
        body.put("next_openid", StringUtils.hasText(nextOpenId) ? nextOpenId : "");

        JSONObject wxResp = postJsonAndGetBody(url, body, "获取标签粉丝失败");
        if (wxResp == null)
        {
            return AjaxResult.error("获取标签粉丝失败");
        }

        return AjaxResult.success(wxResp);
    }

    // ---- private helpers ----

    private AjaxResult doBatchTag(String appId, WechatMpTagBatchRequest request, String action, String errorPrefix)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }
        if (request == null || request.getOpenidList() == null || request.getOpenidList().isEmpty())
        {
            return AjaxResult.error("openid列表不能为空");
        }
        if (request.getOpenidList().size() > MAX_BATCH_OPENID)
        {
            return AjaxResult.error("单次最多为50个用户打标签");
        }
        if (request.getTagId() == null)
        {
            return AjaxResult.error("标签ID不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        String url = WX_API_BASE + "/tags/members/" + action + "?access_token=" + token;

        JSONObject body = new JSONObject();
        body.put("openid_list", request.getOpenidList());
        body.put("tagid", request.getTagId());

        return postJson(url, body, errorPrefix);
    }

    /**
     * 调用微信 POST JSON API，解析 errcode 并返回通用 AjaxResult
     */
    private AjaxResult postJson(String url, JSONObject body, String errorPrefix)
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);

            log.debug("WX tag API request: url={}, body={}", url, body);

            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JSONObject wxResp = JSON.parseObject(resp.getBody());

            log.debug("WX tag API response: {}", wxResp);

            int errcode = wxResp.getIntValue("errcode");
            if (errcode != 0)
            {
                String errmsg = wxResp.getString("errmsg");
                log.error("WX tag API failed: errcode={}, errmsg={}, url={}", errcode, errmsg, url);
                return AjaxResult.error(errorPrefix + ": " + errmsg);
            }

            String respData = wxResp.getString("errmsg");
            return AjaxResult.success(respData != null ? respData : "ok");
        }
        catch (Exception e)
        {
            log.error("WX tag API exception: url={}, {}", url, e.getMessage(), e);
            return AjaxResult.error(errorPrefix + ": " + e.getMessage());
        }
    }

    /**
     * 调用微信 POST JSON API，解析 errcode 并返回原始 JSONObject
     */
    private JSONObject postJsonAndGetBody(String url, JSONObject body, String errorPrefix)
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);

            log.debug("WX tag API request: url={}, body={}", url, body);

            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JSONObject wxResp = JSON.parseObject(resp.getBody());

            log.debug("WX tag API response: {}", wxResp);

            int errcode = wxResp.getIntValue("errcode");
            if (errcode != 0)
            {
                String errmsg = wxResp.getString("errmsg");
                log.error("WX tag API failed: errcode={}, errmsg={}, url={}", errcode, errmsg, url);
                return null;
            }

            return wxResp;
        }
        catch (Exception e)
        {
            log.error("WX tag API exception: url={}, {}", url, e.getMessage(), e);
            return null;
        }
    }
}
