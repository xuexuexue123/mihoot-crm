package com.ruoyi.crm.wechatmp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.dto.WechatMpUserProfile;

@ExtendWith(MockitoExtension.class)
class WechatMpUserProfileResolverTest
{
    @Mock
    private WechatMpTokenService tokenService;

    @Mock
    private WechatMpTencentApiClient tencentApiClient;

    @InjectMocks
    private WechatMpUserProfileResolver resolver;

    @BeforeEach
    void setUp()
    {
        when(tokenService.getAccessToken("wxApp")).thenReturn("token");
    }

    @Test
    void resolve_mapsUnionIdFromApiResponse()
    {
        JSONObject json = new JSONObject();
        json.put("openid", "o-test");
        json.put("unionid", "u-test");
        json.put("subscribe", 1);
        json.put("subscribe_time", 1782868236L);
        json.put("subscribe_scene", "ADD_SCENE_SEARCH");
        when(tencentApiClient.getUserInfo("token", "o-test")).thenReturn(json);

        Optional<WechatMpUserProfile> profile = resolver.resolve("wxApp", "o-test");

        assertTrue(profile.isPresent());
        assertEquals("u-test", profile.get().getUnionId());
        assertEquals("ADD_SCENE_SEARCH", profile.get().getSubscribeScene());
    }

    @Test
    void resolve_returnsEmptyOnApiError()
    {
        JSONObject json = new JSONObject();
        json.put("errcode", 40003);
        json.put("errmsg", "invalid openid");
        when(tencentApiClient.getUserInfo("token", "o-bad")).thenReturn(json);

        Optional<WechatMpUserProfile> profile = resolver.resolve("wxApp", "o-bad");

        assertFalse(profile.isPresent());
    }

    @Test
    void resolve_retriesAfterTokenInvalid()
    {
        JSONObject invalid = new JSONObject();
        invalid.put("errcode", 40001);
        JSONObject ok = new JSONObject();
        ok.put("openid", "o-test");
        ok.put("unionid", "u-retry");
        when(tencentApiClient.getUserInfo(eq("token"), any())).thenReturn(invalid);
        when(tencentApiClient.isTokenInvalid(invalid)).thenReturn(true);
        when(tokenService.refreshAccessToken("wxApp")).thenReturn("token2");
        when(tencentApiClient.getUserInfo("token2", "o-test")).thenReturn(ok);

        Optional<WechatMpUserProfile> profile = resolver.resolve("wxApp", "o-test");

        assertTrue(profile.isPresent());
        assertEquals("u-retry", profile.get().getUnionId());
        verify(tokenService).invalidateCache("wxApp");
    }
}
