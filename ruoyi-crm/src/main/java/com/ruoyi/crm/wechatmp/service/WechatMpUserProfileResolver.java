package com.ruoyi.crm.wechatmp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.dto.WechatMpUserProfile;

import lombok.extern.slf4j.Slf4j;

/**
 * 封装 GET /cgi-bin/user/info，供 subscribe/SCAN enrich 与 unmatched 重试共用。
 */
@Slf4j
@Service
public class WechatMpUserProfileResolver
{
    public static final String UNION_ID_SOURCE_API = "wechat_api";
    public static final String UNION_ID_SOURCE_XML = "xml_event";

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    public Optional<WechatMpUserProfile> resolve(String appId, String openId)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(openId))
        {
            return Optional.empty();
        }
        try
        {
            JSONObject json = fetchUserInfo(appId, openId, false);
            if (json == null)
            {
                return Optional.empty();
            }
            if (json.containsKey("errcode") && json.getIntValue("errcode") != 0)
            {
                log.warn("getUserInfo failed appId={} openId={} errcode={} errmsg={}",
                        appId, openId, json.getIntValue("errcode"), json.getString("errmsg"));
                return Optional.empty();
            }
            return Optional.of(mapToProfile(json, openId));
        }
        catch (Exception e)
        {
            log.warn("getUserInfo exception appId={} openId={}: {}", appId, openId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 从 user/info 拉取 unionId；若记录尚无 union_id 则写回 unmatched 表。
     *
     * @return 通过 unionId 查到的 contactId，或 null
     */
    public Optional<String> fetchUnionIdFromApi(String appId, String openId)
    {
        Optional<WechatMpUserProfile> profile = resolve(appId, openId);
        if (!profile.isPresent() || !StringUtils.hasText(profile.get().getUnionId()))
        {
            return Optional.empty();
        }
        return Optional.of(profile.get().getUnionId());
    }

    private JSONObject fetchUserInfo(String appId, String openId, boolean retried)
    {
        String accessToken = tokenService.getAccessToken(appId);
        JSONObject json = tencentApiClient.getUserInfo(accessToken, openId);
        if (json != null && tencentApiClient.isTokenInvalid(json) && !retried)
        {
            tokenService.invalidateCache(appId);
            accessToken = tokenService.refreshAccessToken(appId);
            json = tencentApiClient.getUserInfo(accessToken, openId);
        }
        return json;
    }

    private WechatMpUserProfile mapToProfile(JSONObject json, String openId)
    {
        WechatMpUserProfile profile = new WechatMpUserProfile();
        profile.setOpenId(StringUtils.hasText(json.getString("openid")) ? json.getString("openid") : openId);
        profile.setUnionId(json.getString("unionid"));
        if (json.containsKey("subscribe"))
        {
            profile.setSubscribe(json.getIntValue("subscribe") == 1);
        }
        if (json.getLong("subscribe_time") != null)
        {
            profile.setSubscribeTime(json.getLongValue("subscribe_time"));
        }
        profile.setSubscribeScene(json.getString("subscribe_scene"));
        profile.setNickname(json.getString("nickname"));
        return profile;
    }
}
