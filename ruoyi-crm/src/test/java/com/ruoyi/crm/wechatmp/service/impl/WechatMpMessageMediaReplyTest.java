package com.ruoyi.crm.wechatmp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.audience.FanAudienceRuleValidator;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageReplyRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMessageMapper;
import com.ruoyi.crm.wechatmp.realtime.WechatMpInboxEventPublisher;
import com.ruoyi.crm.wechatmp.service.IWechatMpConversationService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.framework.web.service.PermissionService;

@ExtendWith(MockitoExtension.class)
class WechatMpMessageMediaReplyTest
{
    @Mock
    private CrmWechatMpMessageMapper messageMapper;

    @Mock
    private CrmWechatMpConfigMapper configMapper;

    @Mock
    private WechatMpTokenService tokenService;

    @Mock
    private WechatMpTencentApiClient tencentApiClient;

    @Mock
    private ActivityEventProducer activityEventProducer;

    @Mock
    private ICrmCustomerIdentityService identityService;

    @Mock
    private FanAudienceRuleValidator fanAudienceRuleValidator;

    @Mock
    private IWechatMpConversationService conversationService;

    @Mock
    private WechatMpInboxEventPublisher inboxEventPublisher;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private WechatMpMessageServiceImpl service;

    @BeforeEach
    void setUp()
    {
        SysUser user = new SysUser();
        user.setUserId(1001L);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1001L);
        loginUser.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()));
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void resolveMediaId_plainAndJson()
    {
        assertEquals("mid1", WechatMpMessageServiceImpl.resolveMediaIdFromContent("mid1"));
        assertEquals("mid2", WechatMpMessageServiceImpl.resolveMediaIdFromContent("{\"media_id\":\"mid2\"}"));
        assertEquals("mid3", WechatMpMessageServiceImpl.resolveMediaIdFromContent("{\"mediaId\":\"mid3\"}"));
    }

    @Test
    void upload_fileType_rejected()
    {
        MockMultipartFile file = new MockMultipartFile("file", "a.pdf", "application/pdf", new byte[] { 1 });
        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.uploadTemporaryMedia("wx1", "file", file));
        assertEquals(true, ex.getMessage().contains("MP_CAPABILITY_NOT_SUPPORTED"));
    }

    @Test
    void reply_image_buildsMediaBody()
    {
        when(permissionService.hasPermi("crm:wechat_mp:conversations")).thenReturn(false);

        CrmWechatMpMessage last = new CrmWechatMpMessage();
        last.setCreateTime(new Date());
        when(messageMapper.selectLastInbound("wx1", "oid1")).thenReturn(last);

        CrmWechatMpConfig cfg = new CrmWechatMpConfig();
        cfg.setAccountType("service");
        when(configMapper.selectByAppId("wx1")).thenReturn(cfg);
        when(tokenService.getAccessToken("wx1")).thenReturn("tok");
        JSONObject ok = new JSONObject();
        ok.put("errcode", 0);
        when(tencentApiClient.sendCustomMessage(eq("tok"), any())).thenReturn(ok);

        WechatMpMessageReplyRequest req = new WechatMpMessageReplyRequest();
        req.setAppId("wx1");
        req.setOpenId("oid1");
        req.setMsgType("image");
        req.setContent("MEDIA_ABC");

        service.reply(req);

        ArgumentCaptor<JSONObject> bodyCap = ArgumentCaptor.forClass(JSONObject.class);
        verify(tencentApiClient).sendCustomMessage(eq("tok"), bodyCap.capture());
        JSONObject body = bodyCap.getValue();
        assertEquals("image", body.getString("msgtype"));
        assertEquals("MEDIA_ABC", body.getJSONObject("image").getString("media_id"));
        verify(conversationService).upsertOnOutbound(eq("wx1"), eq("oid1"), eq("image"), eq(null), any());
    }
}
