package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;

@ExtendWith(MockitoExtension.class)
class WecomExternalContactProfileResolverTest
{
    @Mock
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Mock
    private WecomAccessTokenService wecomAccessTokenService;

    @Mock
    private WecomTencentApiClient wecomTencentApiClient;

    @InjectMocks
    private WecomExternalContactProfileResolver resolver;

    @Test
    void fetch_mapsUnionIdFromApiResponse()
    {
        CrmWecomConfig cfg = new CrmWecomConfig();
        cfg.setCorpId("ww_test");
        when(crmWecomConfigMapper.selectFirst()).thenReturn(cfg);
        when(wecomAccessTokenService.getAgentAccessToken(cfg)).thenReturn("token");

        JSONObject res = new JSONObject();
        JSONObject ec = new JSONObject();
        ec.put("external_userid", "wm_abc");
        ec.put("name", "张三");
        ec.put("unionid", "o_union_001");
        res.put("errcode", 0);
        res.put("external_contact", ec);
        when(wecomTencentApiClient.externalContactGet(eq("token"), eq("wm_abc"))).thenReturn(res);

        Optional<WecomExternalContactProfile> profile = resolver.fetch("wm_abc");

        assertTrue(profile.isPresent());
        assertEquals("o_union_001", profile.get().getUnionId());
        assertEquals("张三", profile.get().getName());
    }

    @Test
    void fetch_returnsEmptyOnApiError()
    {
        CrmWecomConfig cfg = new CrmWecomConfig();
        when(crmWecomConfigMapper.selectFirst()).thenReturn(cfg);
        when(wecomAccessTokenService.getAgentAccessToken(cfg)).thenReturn("token");

        JSONObject res = new JSONObject();
        res.put("errcode", 84061);
        res.put("errmsg", "not external contact");
        when(wecomTencentApiClient.externalContactGet(any(), any())).thenReturn(res);

        assertFalse(resolver.fetch("wm_bad").isPresent());
    }

    @Test
    void parseResponse_handlesMissingUnionId()
    {
        JSONObject res = new JSONObject();
        JSONObject ec = new JSONObject();
        ec.put("name", "李四");
        res.put("external_contact", ec);

        WecomExternalContactProfile profile = WecomExternalContactProfileResolver.parseResponse("wm_x", res);

        assertEquals("李四", profile.getName());
        assertEquals(null, profile.getUnionId());
    }
}
