package com.ruoyi.crm.wechatminiapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import com.ruoyi.crm.wechatminiapp.domain.CrmWechatMiniappConfig;
import com.ruoyi.crm.wechatminiapp.dto.WechatMiniappConfigSaveRequest;
import com.ruoyi.crm.wechatminiapp.mapper.CrmWechatMiniappConfigMapper;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

@ExtendWith(MockitoExtension.class)
class CrmWechatMiniappConfigServiceImplTest
{
    @Mock
    private CrmWechatMiniappConfigMapper configMapper;

    @Mock
    private WechatMpTokenService wechatMpTokenService;

    @InjectMocks
    private CrmWechatMiniappConfigServiceImpl service;

    @Test
    void getConfigMasked_returnsNullWhenEmpty()
    {
        when(configMapper.selectSingleton()).thenReturn(null);
        assertNull(service.getConfigMasked());
    }

    @Test
    void getConfigMasked_neverExposesSecret() throws Exception
    {
        CrmWechatMiniappConfig cfg = new CrmWechatMiniappConfig();
        cfg.setId(1L);
        cfg.setAppId("wx123");
        cfg.setName("demo");
        cfg.setAppSecretEnc(AESUtil.encrypt("plain-secret"));
        cfg.setConnected(true);
        cfg.setUpdateTime(new Date());
        when(configMapper.selectSingleton()).thenReturn(cfg);

        Map<String, Object> masked = service.getConfigMasked();

        assertEquals("wx123", masked.get("appId"));
        assertEquals("demo", masked.get("name"));
        assertEquals(true, masked.get("secretConfigured"));
        assertEquals(true, masked.get("connected"));
        assertFalse(masked.containsKey("secret"));
        assertFalse(masked.containsKey("appSecret"));
        assertFalse(masked.containsKey("appSecretEnc"));
        assertFalse(masked.containsKey("app_secret_enc"));
    }

    @Test
    void getConfigRuntime_returnsNullWhenEmpty()
    {
        when(configMapper.selectSingleton()).thenReturn(null);
        assertNull(service.getConfigRuntime());
    }

    @Test
    void getConfigRuntime_returnsPlainSecret() throws Exception
    {
        CrmWechatMiniappConfig cfg = new CrmWechatMiniappConfig();
        cfg.setAppId("wx123");
        cfg.setName("越南出海");
        cfg.setAppSecretEnc(AESUtil.encrypt("plain-secret"));
        cfg.setConnected(true);
        when(configMapper.selectSingleton()).thenReturn(cfg);

        Map<String, Object> runtime = service.getConfigRuntime();

        assertEquals("wx123", runtime.get("appId"));
        assertEquals("越南出海", runtime.get("name"));
        assertEquals("plain-secret", runtime.get("appSecret"));
        assertEquals(true, runtime.get("connected"));
        assertFalse(runtime.containsKey("secretConfigured"));
        assertFalse(runtime.containsKey("appSecretEnc"));
    }

    @Test
    void saveOrUpdate_rejectsFirstSaveWithoutSecret()
    {
        when(configMapper.selectSingleton()).thenReturn(null);
        WechatMiniappConfigSaveRequest body = new WechatMiniappConfigSaveRequest();
        body.setAppId("wx123");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.saveOrUpdate(body));
        assertEquals("首次保存必须提供 secret", ex.getMessage());
        verify(configMapper, never()).insert(any());
    }

    @Test
    void saveOrUpdate_insertsWhenProbeSucceeds() throws Exception
    {
        when(configMapper.selectSingleton()).thenReturn(null);
        when(wechatMpTokenService.getAccessToken(eq("wx123"), eq("s3cret"))).thenReturn("ACCESS");

        WechatMiniappConfigSaveRequest body = new WechatMiniappConfigSaveRequest();
        body.setAppId("wx123");
        body.setSecret("s3cret");
        body.setName("越南出海");

        service.saveOrUpdate(body);

        ArgumentCaptor<CrmWechatMiniappConfig> captor = ArgumentCaptor.forClass(CrmWechatMiniappConfig.class);
        verify(configMapper).insert(captor.capture());
        CrmWechatMiniappConfig saved = captor.getValue();
        assertEquals("wx123", saved.getAppId());
        assertEquals("越南出海", saved.getName());
        assertTrue(Boolean.TRUE.equals(saved.getConnected()));
        assertNull(saved.getLastError());
        assertEquals("s3cret", AESUtil.decrypt(saved.getAppSecretEnc()));
    }

    @Test
    void saveOrUpdate_keepsSecretAndRecordsErrorWhenProbeFails() throws Exception
    {
        CrmWechatMiniappConfig existing = new CrmWechatMiniappConfig();
        existing.setId(9L);
        existing.setAppId("wx-old");
        existing.setAppSecretEnc(AESUtil.encrypt("old-secret"));
        existing.setConnected(true);
        when(configMapper.selectSingleton()).thenReturn(existing);
        when(wechatMpTokenService.getAccessToken(eq("wx-new"), eq("old-secret")))
                .thenThrow(new ServiceException("invalid appsecret"));

        WechatMiniappConfigSaveRequest body = new WechatMiniappConfigSaveRequest();
        body.setAppId("wx-new");
        // secret omitted → keep old ciphertext

        service.saveOrUpdate(body);

        ArgumentCaptor<CrmWechatMiniappConfig> captor = ArgumentCaptor.forClass(CrmWechatMiniappConfig.class);
        verify(configMapper).update(captor.capture());
        verify(configMapper, never()).insert(any());
        CrmWechatMiniappConfig saved = captor.getValue();
        assertEquals(Long.valueOf(9L), saved.getId());
        assertEquals("wx-new", saved.getAppId());
        assertEquals("old-secret", AESUtil.decrypt(saved.getAppSecretEnc()));
        assertFalse(Boolean.TRUE.equals(saved.getConnected()));
        assertTrue(saved.getLastError().contains("invalid appsecret"));
    }

    @Test
    void delete_throwsWhenMissing()
    {
        when(configMapper.selectSingleton()).thenReturn(null);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.delete());
        assertEquals("小程序配置不存在", ex.getMessage());
        verify(configMapper, never()).deleteSingleton();
    }

    @Test
    void delete_removesSingleton()
    {
        CrmWechatMiniappConfig existing = new CrmWechatMiniappConfig();
        existing.setId(1L);
        when(configMapper.selectSingleton()).thenReturn(existing);

        service.delete();

        verify(configMapper).deleteSingleton();
        verify(wechatMpTokenService, never()).getAccessToken(anyString(), anyString());
    }
}
