package com.ruoyi.crm.service.impl;

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
import org.mockito.ArgumentCaptor;
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
import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWhatsappConversationMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmWhatsappConversationService;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxEventPublisher;
import com.ruoyi.framework.web.service.PermissionService;

@ExtendWith(MockitoExtension.class)
class CrmWhatsappConversationPoolServiceTest
{
    private static final String TENANT = "t1";

    @Mock
    private CrmWhatsappConversationMapper conversationMapper;

    @Mock
    private CrmWhatsappMessageMapper messageMapper;

    @Mock
    private CrmContactMapper contactMapper;

    @Mock
    private WhatsAppInboxEventPublisher inboxEventPublisher;

    @Mock
    private IAssignableUserService assignableUserService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private ICrmWhatsappConversationService self;

    @InjectMocks
    private CrmWhatsappConversationServiceImpl service;

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
    void claim_success_writesAssignmentAndPublishesSse()
    {
        CrmWhatsappConversation locked = poolConversation(10L);
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);

        LeadAssignPatch patch = new LeadAssignPatch();
        patch.setUserId(1001L);
        patch.setOwner("Alice");
        AssignableResolveResult resolve = new AssignableResolveResult();
        resolve.setResolved(true);
        resolve.setLeadAssignPatch(patch);
        when(assignableUserService.resolve(isNull(), eq(1001L), eq("lead_claim"))).thenReturn(resolve);

        Map<String, Object> data = service.claim(10L, TENANT);

        assertEquals(10L, data.get("id"));
        assertEquals(1001L, data.get("userId"));
        assertEquals("Alice", data.get("owner"));
        verify(conversationMapper).updateAssignment(eq(10L), eq(TENANT), eq(1001L), eq("Alice"), any(Date.class));
        verify(inboxEventPublisher).publishConversationUpdated(eq(TENANT), any());
    }

    @Test
    void claim_alreadyOwned_returns409()
    {
        CrmWhatsappConversation locked = poolConversation(10L);
        locked.setUserId(2002L);
        locked.setOwner("Bob");
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.claim(10L, TENANT));
        assertEquals(HttpStatus.CONFLICT, ex.getCode().intValue());
        verify(conversationMapper, never()).updateAssignment(any(), any(), any(), any(), any());
    }

    @Test
    void release_owner_clearsAssignment()
    {
        CrmWhatsappConversation locked = poolConversation(10L);
        locked.setUserId(1001L);
        locked.setOwner("Alice");
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);
        when(permissionService.hasPermi("crm:whatsapp:release")).thenReturn(true);

        Map<String, Object> data = service.release(10L, TENANT);

        assertNull(data.get("userId"));
        verify(conversationMapper).clearAssignment(10L, TENANT);
        verify(inboxEventPublisher).publishConversationUpdated(eq(TENANT), any());
    }

    @Test
    void release_managerForce_requiresTeamScope()
    {
        CrmWhatsappConversation locked = poolConversation(10L);
        locked.setUserId(2002L);
        locked.setOwner("Bob");
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);
        when(permissionService.hasPermi("crm:whatsapp:assign")).thenReturn(true);
        when(self.countInTeamScope(any())).thenReturn(1);

        Map<String, Object> data = service.release(10L, TENANT);

        assertNull(data.get("userId"));
        verify(conversationMapper).clearAssignment(10L, TENANT);
    }

    @Test
    void assign_fromPool_writesTarget()
    {
        CrmWhatsappConversation locked = poolConversation(10L);
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);

        LeadAssignPatch patch = new LeadAssignPatch();
        patch.setUserId(3003L);
        patch.setOwner("Carol");
        AssignableResolveResult resolve = new AssignableResolveResult();
        resolve.setResolved(true);
        resolve.setLeadAssignPatch(patch);
        when(assignableUserService.resolve(isNull(), eq(3003L), eq("lead_assign"))).thenReturn(resolve);

        Map<String, Object> data = service.assign(10L, TENANT, 3003L);

        assertEquals(3003L, data.get("userId"));
        assertEquals("Carol", data.get("owner"));
        verify(conversationMapper).updateAssignment(eq(10L), eq(TENANT), eq(3003L), eq("Carol"), any(Date.class));
    }

    @Test
    void linkLead_requiresOwner()
    {
        CrmWhatsappConversation locked = poolConversation(10L);
        locked.setUserId(1001L);
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);

        Map<String, Object> data = service.linkLead(10L, TENANT, "lead-1");
        assertEquals("lead-1", data.get("leadId"));
        verify(conversationMapper).updateLeadId(10L, TENANT, "lead-1");
    }

    @Test
    void linkLead_forbiddenForNonOwner()
    {
        CrmWhatsappConversation locked = poolConversation(10L);
        locked.setUserId(2002L);
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.linkLead(10L, TENANT, "lead-1"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode().intValue());
    }

    @Test
    void assertOwnedByCurrentUser_rejectsPool()
    {
        CrmWhatsappConversation conv = poolConversation(1L);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.assertOwnedByCurrentUser(conv));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode().intValue());
    }

    @Test
    void ssePartial_includesAssignmentFields()
    {
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        CrmWhatsappConversation locked = poolConversation(10L);
        when(conversationMapper.selectByIdForUpdate(10L)).thenReturn(locked);

        LeadAssignPatch patch = new LeadAssignPatch();
        patch.setUserId(1001L);
        patch.setOwner("Alice");
        AssignableResolveResult resolve = new AssignableResolveResult();
        resolve.setResolved(true);
        resolve.setLeadAssignPatch(patch);
        when(assignableUserService.resolve(isNull(), eq(1001L), eq("lead_claim"))).thenReturn(resolve);

        service.claim(10L, TENANT);

        verify(inboxEventPublisher).publishConversationUpdated(eq(TENANT), captor.capture());
        @SuppressWarnings("unchecked")
        Map<String, Object> envelope = captor.getValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> conversation = (Map<String, Object>) envelope.get("conversation");
        assertEquals(1001L, conversation.get("userId"));
        assertEquals("Alice", conversation.get("owner"));
    }

    private static CrmWhatsappConversation poolConversation(Long id)
    {
        CrmWhatsappConversation c = new CrmWhatsappConversation();
        c.setId(id);
        c.setTenantId(TENANT);
        c.setWaId("8613800138001");
        c.setStatus("active");
        c.setUserId(null);
        return c;
    }

    private static void loginAs(Long userId, String nick)
    {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUserName(nick);
        user.setNickName(nick);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, java.util.Collections.emptyList()));
    }
}
