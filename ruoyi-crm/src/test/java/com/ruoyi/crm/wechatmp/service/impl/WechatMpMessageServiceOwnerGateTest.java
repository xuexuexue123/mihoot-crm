package com.ruoyi.crm.wechatmp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class WechatMpMessageServiceOwnerGateTest
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
        loginAs(1001L);
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void reply_withConversationsPerm_rejectsNonOwner()
    {
        when(permissionService.hasPermi("crm:wechat_mp:conversations")).thenReturn(true);
        CrmWechatMpConversation conv = new CrmWechatMpConversation();
        conv.setUserId(2002L);
        when(conversationService.findByAppOpenId("wx1", "oid1")).thenReturn(conv);
        org.mockito.Mockito.doThrow(new ServiceException("仅会话负责人可发送消息", HttpStatus.FORBIDDEN))
                .when(conversationService).assertOwnedByCurrentUser(conv);

        WechatMpMessageReplyRequest req = new WechatMpMessageReplyRequest();
        req.setAppId("wx1");
        req.setOpenId("oid1");
        req.setContent("hi");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.reply(req));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode().intValue());
        verify(tencentApiClient, never()).sendCustomMessage(anyString(), any());
    }

    @Test
    void reply_marketingOnly_skipsOwnerGate()
    {
        when(permissionService.hasPermi("crm:wechat_mp:conversations")).thenReturn(false);

        CrmWechatMpMessage last = new CrmWechatMpMessage();
        last.setCreateTime(new java.util.Date());
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
        req.setContent("hi");

        service.reply(req);

        verify(conversationService, never()).assertOwnedByCurrentUser(any());
        verify(tencentApiClient).sendCustomMessage(eq("tok"), any());
        verify(conversationService).upsertOnOutbound(eq("wx1"), eq("oid1"), eq("text"), eq("hi"), any());
    }

    private static void loginAs(Long userId)
    {
        SysUser user = new SysUser();
        user.setUserId(userId);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()));
    }
}
