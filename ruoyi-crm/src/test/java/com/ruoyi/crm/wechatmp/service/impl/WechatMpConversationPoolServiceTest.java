package com.ruoyi.crm.wechatmp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConversationMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMessageMapper;
import com.ruoyi.crm.wechatmp.realtime.WechatMpInboxEventPublisher;
import com.ruoyi.crm.wechatmp.service.IWechatMpConversationService;
import com.ruoyi.framework.web.service.PermissionService;

@ExtendWith(MockitoExtension.class)
class WechatMpConversationPoolServiceTest
{
    @Mock
    private CrmWechatMpConversationMapper conversationMapper;

    @Mock
    private CrmWechatMpMessageMapper messageMapper;

    @Mock
    private CrmWechatMpConfigMapper configMapper;

    @Mock
    private CrmWechatMpFanMapper fanMapper;

    @Mock
    private IAssignableUserService assignableUserService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private ICrmCustomerIdentityService identityService;

    @Mock
    private WechatMpInboxEventPublisher inboxEventPublisher;

    @Mock
    private IWechatMpConversationService self;

    @InjectMocks
    private WechatMpConversationServiceImpl service;

    @BeforeEach
    void setUp()
    {
        ReflectionTestUtils.setField(service, "self", self);
        loginAs(1001L, "alice");
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void claim_success()
    {
        CrmWechatMpConversation locked = poolConversation(10L);
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);

        LeadAssignPatch patch = new LeadAssignPatch();
        patch.setUserId(1001L);
        patch.setOwner("Alice");
        AssignableResolveResult resolve = new AssignableResolveResult();
        resolve.setResolved(true);
        resolve.setLeadAssignPatch(patch);
        when(assignableUserService.resolve(isNull(), eq(1001L), eq("lead_claim"))).thenReturn(resolve);

        Map<String, Object> data = service.claim(10L);

        assertEquals(10L, data.get("id"));
        assertEquals("1001", data.get("userId"));
        assertEquals("Alice", data.get("owner"));
        verify(conversationMapper).updateAssignment(eq(10L), eq(1001L), eq("Alice"), any(Date.class));
    }

    @Test
    void claim_alreadyOwned_returns409()
    {
        CrmWechatMpConversation locked = poolConversation(10L);
        locked.setUserId(2002L);
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.claim(10L));
        assertEquals(HttpStatus.CONFLICT, ex.getCode().intValue());
        verify(conversationMapper, never()).updateAssignment(any(), any(), any(), any());
    }

    @Test
    void release_owner_clearsAssignment()
    {
        CrmWechatMpConversation locked = poolConversation(10L);
        locked.setUserId(1001L);
        locked.setOwner("Alice");
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);
        when(permissionService.hasPermi("crm:wechat_mp:conversations")).thenReturn(true);

        Map<String, Object> data = service.release(10L);

        assertNull(data.get("userId"));
        verify(conversationMapper).clearAssignment(10L);
    }

    @Test
    void release_forceRequiresAssign()
    {
        CrmWechatMpConversation locked = poolConversation(10L);
        locked.setUserId(2002L);
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);
        when(permissionService.hasPermi("crm:wechat_mp:assign")).thenReturn(true);
        when(self.countInTeamScope(any())).thenReturn(1);

        Map<String, Object> data = service.release(10L);

        assertNull(data.get("userId"));
        verify(conversationMapper).clearAssignment(10L);
    }

    @Test
    void markRead_clearsUnread()
    {
        CrmWechatMpConversation conv = poolConversation(10L);
        conv.setUnreadCount(3);
        when(conversationMapper.selectById(10L)).thenReturn(conv);

        Map<String, Object> data = service.markRead(10L);

        assertEquals(0, data.get("unreadCount"));
        verify(conversationMapper).clearUnread(10L);
    }

    @Test
    void assertOwnedByCurrentUser_rejectsPool()
    {
        CrmWechatMpConversation conv = poolConversation(10L);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.assertOwnedByCurrentUser(conv));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode().intValue());
    }

    @Test
    void assertOwnedByCurrentUser_acceptsOwner()
    {
        CrmWechatMpConversation conv = poolConversation(10L);
        conv.setUserId(1001L);
        service.assertOwnedByCurrentUser(conv);
    }

    @Test
    void messagingWindow_48h()
    {
        assertNull(WechatMpConversationServiceImpl.calcMessagingWindowRemaining(null));

        Date recent = new Date(System.currentTimeMillis() - 3600_000L);
        Long remaining = WechatMpConversationServiceImpl.calcMessagingWindowRemaining(recent);
        assertEquals(true, remaining != null && remaining > 0 && remaining <= 48 * 3600);

        Date expired = new Date(System.currentTimeMillis() - 49L * 3600_000L);
        assertEquals(0L, WechatMpConversationServiceImpl.calcMessagingWindowRemaining(expired));
    }

    @Test
    void linkContact_conflict409()
    {
        when(configMapper.selectByAppId("wx1")).thenReturn(new CrmWechatMpConfig());
        when(identityService.findContactByIdentity("wechat_mp_openid", "oid1")).thenReturn("other");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.linkContact("wx1", "oid1", "c1", false));
        assertEquals(HttpStatus.CONFLICT, ex.getCode().intValue());
        verify(identityService, never()).bindIdentity(any(), any(), any(), any());
    }

    @Test
    void previewOf_image()
    {
        assertEquals("[图片]", WechatMpConversationServiceImpl.previewOf("image", null));
        assertEquals("hi", WechatMpConversationServiceImpl.previewOf("text", "hi"));
    }

    private static CrmWechatMpConversation poolConversation(Long id)
    {
        CrmWechatMpConversation c = new CrmWechatMpConversation();
        c.setId(id);
        c.setAppId("wx1");
        c.setOpenId("oid1");
        return c;
    }

    private static void loginAs(Long userId, String name)
    {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setNickName(name);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUser(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
