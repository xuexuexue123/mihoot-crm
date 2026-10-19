package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppMediaFetchResult;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsAppClient;
import com.ruoyi.crm.whatsapp.WhatsAppMediaPersistBffClient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CrmWhatsappInboundMediaPersistServiceTest
{
    @Mock
    private CrmWhatsappMessageMapper messageMapper;

    @Mock
    private CrmWhatsappConfigMapper configMapper;

    @Mock
    private WhatsAppClient whatsappClient;

    @Mock
    private TokenEncryptor tokenEncryptor;

    @Mock
    private WhatsAppMediaPersistBffClient mediaPersistBffClient;

    @Mock
    private Executor whatsappMediaTaskExecutor;

    @InjectMocks
    private CrmWhatsappInboundMediaPersistService service;

    @BeforeEach
    void wireExecutor()
    {
        ReflectionTestUtils.setField(service, "whatsappMediaTaskExecutor", whatsappMediaTaskExecutor);
        doAnswer(inv -> {
            ((Runnable) inv.getArgument(0)).run();
            return null;
        }).when(whatsappMediaTaskExecutor).execute(any(Runnable.class));
    }

    @Test
    void isDurableMediaUrl_detectsHttps()
    {
        assertTrue(CrmWhatsappInboundMediaPersistService.isDurableMediaUrl("https://oss.example/a.jpg"));
        assertFalse(CrmWhatsappInboundMediaPersistService.isDurableMediaUrl("/crm/whatsapp/media/mid"));
        assertFalse(CrmWhatsappInboundMediaPersistService.isDurableMediaUrl(null));
    }

    @Test
    void isPersistableMediaType()
    {
        assertTrue(CrmWhatsappInboundMediaPersistService.isPersistableMediaType("image"));
        assertTrue(CrmWhatsappInboundMediaPersistService.isPersistableMediaType("DOCUMENT"));
        assertFalse(CrmWhatsappInboundMediaPersistService.isPersistableMediaType("text"));
    }

    @Test
    void schedulePersist_skipsWhenBffNotReady()
    {
        when(mediaPersistBffClient.isReady()).thenReturn(false);
        service.schedulePersist("t1", 10L, "mid", "a.jpg", "image/jpeg");
        verify(whatsappMediaTaskExecutor, never()).execute(any());
    }

    @Test
    void persist_idempotentWhenAlreadyDurable()
    {
        CrmWhatsappMessage row = new CrmWhatsappMessage();
        row.setId(10L);
        row.setMediaUrl("https://oss.example/x.jpg");
        when(messageMapper.selectById("t1", 10L)).thenReturn(row);

        service.persist("t1", 10L, "mid", "a.jpg", "image/jpeg");

        verify(whatsappClient, never()).downloadMedia(anyString(), anyString());
        verify(mediaPersistBffClient, never()).upload(anyString(), any(), anyString(), anyString());
        verify(messageMapper, never()).updateMediaUrl(anyString(), any(), anyString());
    }

    @Test
    void persist_downloadsUploadsViaBffAndUpdates()
    {
        CrmWhatsappMessage row = new CrmWhatsappMessage();
        row.setId(10L);
        row.setMediaId("mid");
        row.setMediaMimeType("image/jpeg");
        when(messageMapper.selectById("t1", 10L)).thenReturn(row);

        CrmWhatsappConfig config = new CrmWhatsappConfig();
        config.setAccessTokenEnc("enc");
        when(configMapper.selectByTenantId("t1")).thenReturn(config);
        when(tokenEncryptor.decrypt("enc")).thenReturn("token");
        when(whatsappClient.downloadMedia("mid", "token"))
                .thenReturn(WhatsAppMediaFetchResult.success("image/jpeg", new byte[] {1, 2, 3}));
        when(mediaPersistBffClient.upload(eq("t1"), any(), eq("image/jpeg"), eq("a.jpg")))
                .thenReturn("https://oss.example/durable.jpg");
        when(messageMapper.updateMediaUrl("t1", 10L, "https://oss.example/durable.jpg")).thenReturn(1);

        service.persist("t1", 10L, "mid", "a.jpg", "image/jpeg");

        verify(messageMapper).updateMediaUrl("t1", 10L, "https://oss.example/durable.jpg");
    }
}
