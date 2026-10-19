package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppMediaFetchResult;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsAppClient;

@ExtendWith(MockitoExtension.class)
class CrmWhatsappMediaServiceImplTest
{
    @Mock
    private CrmWhatsappConfigMapper configMapper;

    @Mock
    private CrmWhatsappMessageMapper messageMapper;

    @Mock
    private WhatsAppClient whatsappClient;

    @Mock
    private TokenEncryptor tokenEncryptor;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CrmWhatsappMediaServiceImpl mediaService;

    @Test
    void fetchMedia_ok()
    {
        CrmWhatsappMessage owned = new CrmWhatsappMessage();
        owned.setMediaId("mid1");
        owned.setMediaMimeType("image/jpeg");
        owned.setFilename("photo.jpg");
        when(messageMapper.selectByMediaId("tenant3", "mid1")).thenReturn(owned);

        CrmWhatsappConfig config = new CrmWhatsappConfig();
        config.setAccessTokenEnc("enc");
        when(configMapper.selectByTenantId("tenant3")).thenReturn(config);
        when(tokenEncryptor.decrypt("enc")).thenReturn("token");
        when(whatsappClient.downloadMedia("mid1", "token"))
                .thenReturn(WhatsAppMediaFetchResult.success("image/jpeg", new byte[] {1, 2, 3}));

        WhatsAppMediaFetchResult result = mediaService.fetchMedia("tenant3", "mid1");
        assertEquals(true, result.isSuccess());
        assertEquals(3, result.getBody().length);
        assertEquals("photo.jpg", result.getFilename());
    }

    @Test
    void fetchMedia_prefersDurableUrl_skipsMeta()
    {
        ReflectionTestUtils.setField(mediaService, "restTemplate", restTemplate);

        CrmWhatsappMessage owned = new CrmWhatsappMessage();
        owned.setTenantId("tenant3");
        owned.setMediaId("mid1");
        owned.setMediaUrl("https://oss.example/photo.jpg");
        owned.setMediaMimeType("image/jpeg");
        owned.setFilename("photo.jpg");
        when(messageMapper.selectByMediaId("tenant3", "mid1")).thenReturn(owned);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        ResponseEntity<byte[]> response = new ResponseEntity<>(
                new byte[] {7, 8}, headers, HttpStatus.OK);
        when(restTemplate.exchange(eq("https://oss.example/photo.jpg"), eq(HttpMethod.GET),
                eq(null), eq(byte[].class))).thenReturn(response);

        WhatsAppMediaFetchResult result = mediaService.fetchMedia("tenant3", "mid1");
        assertEquals(true, result.isSuccess());
        assertEquals(2, result.getBody().length);
        verify(whatsappClient, never()).downloadMedia(anyString(), anyString());
        verify(configMapper, never()).selectByTenantId(anyString());
    }

    @Test
    void fetchMedia_notOwned_404()
    {
        when(messageMapper.selectByMediaId("tenant3", "midx")).thenReturn(null);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> mediaService.fetchMedia("tenant3", "midx"));
        assertEquals(404, ex.getCode().intValue());
        verify(whatsappClient, never()).downloadMedia(anyString(), anyString());
    }

    @Test
    void fetchMedia_invalidId()
    {
        assertThrows(ServiceException.class, () -> mediaService.fetchMedia("t", "../x"));
        verify(messageMapper, never()).selectByMediaId(anyString(), anyString());
    }

    @Test
    void resolveDisplayMediaUrl_prefersStoredUrl()
    {
        CrmWhatsappMessage msg = new CrmWhatsappMessage();
        msg.setMediaUrl("https://cdn.example/a.jpg");
        msg.setMediaId("mid");
        assertEquals("https://cdn.example/a.jpg",
                CrmWhatsappConversationServiceImpl.resolveDisplayMediaUrl(msg));
    }

    @Test
    void resolveDisplayMediaUrl_buildsProxyPath()
    {
        CrmWhatsappMessage msg = new CrmWhatsappMessage();
        msg.setMediaId("12345");
        assertEquals("/crm/whatsapp/media/12345",
                CrmWhatsappConversationServiceImpl.resolveDisplayMediaUrl(msg));
    }

    @Test
    void resolveDisplayMediaUrl_nullWhenEmpty()
    {
        assertNull(CrmWhatsappConversationServiceImpl.resolveDisplayMediaUrl(new CrmWhatsappMessage()));
    }
}
