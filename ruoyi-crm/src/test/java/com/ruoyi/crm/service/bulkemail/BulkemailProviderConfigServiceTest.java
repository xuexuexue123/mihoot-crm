package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmBulkemailProviderConfig;
import com.ruoyi.crm.domain.dto.BulkemailProviderConfigSaveRequest;
import com.ruoyi.crm.mapper.CrmBulkemailProviderConfigMapper;

@ExtendWith(MockitoExtension.class)
class BulkemailProviderConfigServiceTest
{
    @Mock
    private CrmBulkemailProviderConfigMapper providerConfigMapper;

    @InjectMocks
    private BulkemailProviderConfigServiceImpl service;

    @Test
    void getMasked_emptyReturnsDisconnected()
    {
        when(providerConfigMapper.selectFirst()).thenReturn(null);

        Map<String, Object> masked = service.getMasked();

        assertFalse((Boolean) masked.get("connected"));
        assertFalse((Boolean) masked.get("apiKeyConfigured"));
    }

    @Test
    void getMasked_activeConfigConnected()
    {
        CrmBulkemailProviderConfig cfg = activeConfig();
        when(providerConfigMapper.selectFirst()).thenReturn(cfg);

        Map<String, Object> masked = service.getMasked();

        assertTrue((Boolean) masked.get("connected"));
        assertTrue((Boolean) masked.get("apiKeyConfigured"));
        assertEquals("mailgun", masked.get("provider"));
    }

    @Test
    void saveOrUpdate_firstSaveRequiresApiKey()
    {
        when(providerConfigMapper.selectFirst()).thenReturn(null);

        BulkemailProviderConfigSaveRequest body = validSaveRequest();
        body.setApiKey(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.saveOrUpdate(body, "admin"));
        assertEquals("首次保存必须提供 apiKey", ex.getMessage());
    }

    @Test
    void saveOrUpdate_blankApiKeyPreservesExisting()
    {
        CrmBulkemailProviderConfig existing = activeConfig();
        existing.setId(1L);
        existing.setApiKeyEnc("enc-existing");
        when(providerConfigMapper.selectFirst()).thenReturn(existing);

        BulkemailProviderConfigSaveRequest body = validSaveRequest();
        body.setApiKey("");

        service.saveOrUpdate(body, "admin");

        verify(providerConfigMapper).update(any(CrmBulkemailProviderConfig.class));
    }

    @Test
    void requireActive_missingConfigThrows409()
    {
        when(providerConfigMapper.selectFirst()).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.requireActive());
        assertEquals(BulkemailProviderConfigServiceImpl.PROVIDER_NOT_CONFIGURED, ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getCode());
    }

    @Test
    void requireActive_disabledThrows409()
    {
        CrmBulkemailProviderConfig cfg = activeConfig();
        cfg.setStatus(BulkemailProviderConfigServiceImpl.STATUS_DISABLED);
        when(providerConfigMapper.selectFirst()).thenReturn(cfg);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.requireActive());
        assertEquals(BulkemailProviderConfigServiceImpl.PROVIDER_NOT_CONFIGURED, ex.getMessage());
    }

    private static BulkemailProviderConfigSaveRequest validSaveRequest()
    {
        BulkemailProviderConfigSaveRequest body = new BulkemailProviderConfigSaveRequest();
        body.setProvider("mailgun");
        body.setDomain("mg.example.com");
        body.setFromEmail("marketing@mg.example.com");
        body.setFromName("Example Clinic");
        body.setRegion("us");
        body.setApiKey("key-test");
        body.setStatus("active");
        return body;
    }

    private static CrmBulkemailProviderConfig activeConfig()
    {
        CrmBulkemailProviderConfig cfg = new CrmBulkemailProviderConfig();
        cfg.setProvider(BulkemailProviderConfigServiceImpl.PROVIDER_MAILGUN);
        cfg.setDomain("mg.example.com");
        cfg.setFromEmail("marketing@mg.example.com");
        cfg.setFromName("Example Clinic");
        cfg.setRegion("us");
        cfg.setApiKeyEnc("enc");
        cfg.setStatus(BulkemailProviderConfigServiceImpl.STATUS_ACTIVE);
        return cfg;
    }
}
