package com.ruoyi.crm.feishu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.feishu.FeishuTokenService;
import com.ruoyi.crm.feishu.domain.CrmFeishuConfig;
import com.ruoyi.crm.feishu.dto.FeishuConfigSaveRequest;
import com.ruoyi.crm.feishu.mapper.CrmFeishuConfigMapper;

@ExtendWith(MockitoExtension.class)
class CrmFeishuConfigServiceImplTest
{
    @Mock
    private CrmFeishuConfigMapper configMapper;

    @Mock
    private FeishuTokenService feishuTokenService;

    @InjectMocks
    private CrmFeishuConfigServiceImpl service;

    @Test
    void getConfigMasked_returnsNullWhenEmpty()
    {
        when(configMapper.selectSingleton()).thenReturn(null);
        assertNull(service.getConfigMasked());
    }

    @Test
    void getConfigMasked_neverExposesSecret() throws Exception
    {
        CrmFeishuConfig cfg = new CrmFeishuConfig();
        cfg.setId(1L);
        cfg.setAppId("cli_xxx");
        cfg.setName("demo");
        cfg.setAppSecretEnc(AESUtil.encrypt("plain-secret"));
        cfg.setConnected(true);
        cfg.setUpdateTime(new Date());
        when(configMapper.selectSingleton()).thenReturn(cfg);

        Map<String, Object> masked = service.getConfigMasked();

        assertEquals("cli_xxx", masked.get("appId"));
        assertEquals("demo", masked.get("name"));
        assertEquals(true, masked.get("secretConfigured"));
        assertEquals(true, masked.get("connected"));
        assertFalse(masked.containsKey("secret"));
        assertFalse(masked.containsKey("appSecretEnc"));
        assertFalse(masked.containsKey("app_secret_enc"));
        assertFalse(masked.containsKey("accessToken"));
    }

    @Test
    void saveOrUpdate_rejectsFirstSaveWithoutSecret()
    {
        when(configMapper.selectSingleton()).thenReturn(null);
        FeishuConfigSaveRequest body = new FeishuConfigSaveRequest();
        body.setAppId("cli_xxx");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.saveOrUpdate(body));
        assertEquals("首次保存必须提供 secret", ex.getMessage());
        verify(configMapper, never()).insert(any());
    }

    @Test
    void saveOrUpdate_insertsWhenProbeSucceeds() throws Exception
    {
        when(configMapper.selectSingleton()).thenReturn(null);
        when(feishuTokenService.getTenantAccessToken(eq("cli_xxx"), eq("s3cret"), eq(true)))
                .thenReturn(new FeishuTokenService.TokenResult("t-token", System.currentTimeMillis() + 3600_000));

        FeishuConfigSaveRequest body = new FeishuConfigSaveRequest();
        body.setAppId("cli_xxx");
        body.setSecret("s3cret");
        body.setName("SevenCRM Feishu");

        Map<String, Object> masked = service.saveOrUpdate(body);

        ArgumentCaptor<CrmFeishuConfig> captor = ArgumentCaptor.forClass(CrmFeishuConfig.class);
        verify(configMapper).insert(captor.capture());
        CrmFeishuConfig saved = captor.getValue();
        assertEquals("cli_xxx", saved.getAppId());
        assertEquals("SevenCRM Feishu", saved.getName());
        assertTrue(Boolean.TRUE.equals(saved.getConnected()));
        assertNull(saved.getLastError());
        assertEquals("s3cret", AESUtil.decrypt(saved.getAppSecretEnc()));
        assertEquals(true, masked.get("connected"));
        assertEquals(true, masked.get("secretConfigured"));
    }

    @Test
    void saveOrUpdate_keepsSecretAndRecordsErrorWhenProbeFails() throws Exception
    {
        CrmFeishuConfig existing = new CrmFeishuConfig();
        existing.setId(9L);
        existing.setAppId("cli_old");
        existing.setAppSecretEnc(AESUtil.encrypt("old-secret"));
        existing.setConnected(true);
        when(configMapper.selectSingleton()).thenReturn(existing);
        when(feishuTokenService.getTenantAccessToken(eq("cli_new"), eq("old-secret"), eq(true)))
                .thenThrow(new ServiceException("invalid app_secret"));

        FeishuConfigSaveRequest body = new FeishuConfigSaveRequest();
        body.setAppId("cli_new");

        Map<String, Object> masked = service.saveOrUpdate(body);

        ArgumentCaptor<CrmFeishuConfig> captor = ArgumentCaptor.forClass(CrmFeishuConfig.class);
        verify(configMapper).update(captor.capture());
        verify(configMapper, never()).insert(any());
        CrmFeishuConfig saved = captor.getValue();
        assertEquals(Long.valueOf(9L), saved.getId());
        assertEquals("cli_new", saved.getAppId());
        assertEquals("old-secret", AESUtil.decrypt(saved.getAppSecretEnc()));
        assertFalse(Boolean.TRUE.equals(saved.getConnected()));
        assertTrue(saved.getLastError().contains("invalid app_secret"));
        assertEquals(false, masked.get("connected"));
        verify(feishuTokenService).invalidate("cli_old");
    }

    @Test
    void testConnection_updatesConnected()
    {
        CrmFeishuConfig cfg = new CrmFeishuConfig();
        cfg.setId(1L);
        cfg.setAppId("cli_xxx");
        try
        {
            cfg.setAppSecretEnc(AESUtil.encrypt("secret"));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        cfg.setConnected(false);
        when(configMapper.selectSingleton()).thenReturn(cfg);
        when(feishuTokenService.forceRefresh(cfg))
                .thenReturn(new FeishuTokenService.TokenResult("t-ok", System.currentTimeMillis() + 3600_000));

        Map<String, Object> data = service.testConnection();
        assertEquals(true, data.get("connected"));
        verify(configMapper).update(any(CrmFeishuConfig.class));
    }

    @Test
    void requireConnected_rejectsWhenNotConnected()
    {
        CrmFeishuConfig cfg = new CrmFeishuConfig();
        cfg.setAppId("cli_xxx");
        cfg.setAppSecretEnc("enc");
        cfg.setConnected(false);
        cfg.setLastError("boom");
        when(configMapper.selectSingleton()).thenReturn(cfg);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.requireConnected());
        assertTrue(ex.getMessage().contains("未连通"));
    }

    @Test
    void delete_throwsWhenMissing()
    {
        when(configMapper.selectSingleton()).thenReturn(null);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.delete());
        assertEquals("飞书配置不存在", ex.getMessage());
        verify(configMapper, never()).deleteSingleton();
    }

    @Test
    void delete_removesSingletonAndInvalidatesToken()
    {
        CrmFeishuConfig existing = new CrmFeishuConfig();
        existing.setId(1L);
        existing.setAppId("cli_xxx");
        when(configMapper.selectSingleton()).thenReturn(existing);

        service.delete();

        verify(configMapper).deleteSingleton();
        verify(feishuTokenService).invalidate("cli_xxx");
        verify(feishuTokenService, never()).getTenantAccessToken(anyString(), anyString(), anyBoolean());
    }
}
