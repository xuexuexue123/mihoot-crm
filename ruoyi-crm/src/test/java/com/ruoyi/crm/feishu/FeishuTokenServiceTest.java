package com.ruoyi.crm.feishu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.feishu.domain.CrmFeishuConfig;

@ExtendWith(MockitoExtension.class)
class FeishuTokenServiceTest
{
    @Mock
    private FeishuOpenApiClient apiClient;

    private FeishuTokenService tokenService;

    @BeforeEach
    void setUp()
    {
        tokenService = new FeishuTokenService(apiClient);
    }

    @Test
    void getTenantAccessToken_cachesUntilInvalidated()
    {
        JSONObject res = new JSONObject();
        res.put("code", 0);
        res.put("tenant_access_token", "t-1");
        res.put("expire", 7200);
        when(apiClient.getTenantAccessToken("cli_a", "sec")).thenReturn(res);

        FeishuTokenService.TokenResult first = tokenService.getTenantAccessToken("cli_a", "sec", false);
        FeishuTokenService.TokenResult second = tokenService.getTenantAccessToken("cli_a", "sec", false);

        assertEquals("t-1", first.accessToken);
        assertEquals("t-1", second.accessToken);
        verify(apiClient, times(1)).getTenantAccessToken("cli_a", "sec");
        assertTrue(first.expireAtEpochMs > System.currentTimeMillis());
    }

    @Test
    void invalidate_forcesRefresh()
    {
        JSONObject res1 = new JSONObject();
        res1.put("code", 0);
        res1.put("tenant_access_token", "t-1");
        res1.put("expire", 7200);
        JSONObject res2 = new JSONObject();
        res2.put("code", 0);
        res2.put("tenant_access_token", "t-2");
        res2.put("expire", 7200);
        when(apiClient.getTenantAccessToken("cli_a", "sec")).thenReturn(res1, res2);

        assertEquals("t-1", tokenService.getTenantAccessToken("cli_a", "sec", false).accessToken);
        tokenService.invalidate("cli_a");
        assertEquals("t-2", tokenService.getTenantAccessToken("cli_a", "sec", false).accessToken);
        verify(apiClient, times(2)).getTenantAccessToken("cli_a", "sec");
    }

    @Test
    void forceRefresh_bypassesCache()
    {
        JSONObject res1 = new JSONObject();
        res1.put("code", 0);
        res1.put("tenant_access_token", "t-1");
        res1.put("expire", 7200);
        JSONObject res2 = new JSONObject();
        res2.put("code", 0);
        res2.put("tenant_access_token", "t-2");
        res2.put("expire", 7200);
        when(apiClient.getTenantAccessToken(eq("cli_a"), eq("sec"))).thenReturn(res1, res2);

        CrmFeishuConfig cfg = new CrmFeishuConfig();
        cfg.setAppId("cli_a");
        try
        {
            cfg.setAppSecretEnc(AESUtil.encrypt("sec"));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        String first = tokenService.getTenantAccessToken(cfg);
        FeishuTokenService.TokenResult forced = tokenService.forceRefresh(cfg);
        assertEquals("t-1", first);
        assertEquals("t-2", forced.accessToken);
        assertNotEquals(first, forced.accessToken);
        verify(apiClient, times(2)).getTenantAccessToken("cli_a", "sec");
    }

    @Test
    void getTenantAccessToken_mapsFeishuError()
    {
        JSONObject res = new JSONObject();
        res.put("code", 10014);
        res.put("msg", "app secret invalid");
        when(apiClient.getTenantAccessToken("cli_bad", "bad")).thenReturn(res);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> tokenService.getTenantAccessToken("cli_bad", "bad", true));
        assertTrue(ex.getMessage().contains("10014"));
        assertTrue(ex.getMessage().contains("app secret invalid"));
    }
}
