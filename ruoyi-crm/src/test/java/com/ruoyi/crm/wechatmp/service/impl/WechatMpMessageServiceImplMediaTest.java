package com.ruoyi.crm.wechatmp.service.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.WechatMpMediaFetchResult;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.audience.FanAudienceRuleValidator;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WechatMpMessageServiceImplMediaTest
{
    private static final String APP_ID = "wxTestApp";
    private static final String MEDIA_ID = "f_media123";

    @Mock
    private WechatMpTokenService tokenService;

    @Mock
    private WechatMpTencentApiClient tencentApiClient;

    @Mock
    private FanAudienceRuleValidator fanAudienceRuleValidator;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private WechatMpMessageServiceImpl messageService;

    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() throws IOException
    {
        output = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStream()
        {
            @Override
            public void write(int b)
            {
                output.write(b);
            }

            @Override
            public boolean isReady()
            {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener listener)
            {
            }
        });
    }

    @Test
    void streamTemporaryMedia_rejectsUnknownAppId()
    {
        doThrow(new ServiceException("appId 不属于当前租户", HttpStatus.FORBIDDEN))
                .when(fanAudienceRuleValidator).validateAppId(APP_ID);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> messageService.streamTemporaryMedia(APP_ID, MEDIA_ID, response));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }

    @Test
    void streamTemporaryMedia_refreshesTokenOn40001()
            throws IOException
    {
        byte[] imageBytes = new byte[] { 1, 2, 3 };
        when(tokenService.getAccessToken(APP_ID)).thenReturn("token1");
        when(tokenService.refreshAccessToken(APP_ID)).thenReturn("token2");
        when(tencentApiClient.getTemporaryMedia("token1", MEDIA_ID))
                .thenReturn(WechatMpMediaFetchResult.error(40001, "invalid credential"));
        when(tencentApiClient.getTemporaryMedia("token2", MEDIA_ID))
                .thenReturn(WechatMpMediaFetchResult.success("image/jpeg", imageBytes));

        messageService.streamTemporaryMedia(APP_ID, MEDIA_ID, response);

        verify(tokenService).refreshAccessToken(APP_ID);
        verify(response).setContentType("image/jpeg");
        verify(response).setHeader("Cache-Control", "private, max-age=3600");
        assertArrayEquals(imageBytes, output.toByteArray());
    }

    @Test
    void streamTemporaryMedia_invalidMediaId_returns404()
    {
        when(tokenService.getAccessToken(APP_ID)).thenReturn("token1");
        when(tencentApiClient.getTemporaryMedia(eq("token1"), anyString()))
                .thenReturn(WechatMpMediaFetchResult.error(40007, "invalid media_id"));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> messageService.streamTemporaryMedia(APP_ID, MEDIA_ID, response));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
    }

    @Test
    void streamTemporaryMedia_otherWechatError_returns502()
    {
        when(tokenService.getAccessToken(APP_ID)).thenReturn("token1");
        when(tencentApiClient.getTemporaryMedia(eq("token1"), anyString()))
                .thenReturn(WechatMpMediaFetchResult.error(45009, "reach max api frequency"));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> messageService.streamTemporaryMedia(APP_ID, MEDIA_ID, response));
        assertEquals(502, ex.getCode());
    }
}
