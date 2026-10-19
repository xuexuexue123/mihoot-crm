package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatDetailResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarGroupResolveResultDto;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.service.ICrmWecomGroupChatService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomExternalContactProfileResolver;
import com.ruoyi.crm.wecom.WecomIdentityResolutionSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.system.service.ISysDictTypeService;

@ExtendWith(MockitoExtension.class)
class CrmWecomSidebarGroupResolveServiceTest
{
    private static final String TENANT = "tenant3";
    private static final String CHAT_ID = "wrXXXXXXXX";
    private static final String CONTACT_ID = "contact-1";
    private static final String EXTERNAL_USERID = "wm-ext-1";
    private static final String SN_USERID = "staff-1";

    @Mock
    private WecomExternalContactProfileResolver profileResolver;

    @Mock
    private WecomIdentityResolutionSupport identityResolutionSupport;

    @Mock
    private CrmWecomExternalContactMapper wecomExternalContactMapper;

    @Mock
    private CrmContactMapper crmContactMapper;

    @Mock
    private CrmOptyMapper crmOptyMapper;

    @Mock
    private CrmWecomContactGroupMapper contactGroupMapper;

    @Mock
    private CrmWecomConfigMapper wecomConfigMapper;

    @Mock
    private WecomAccessTokenService wecomAccessTokenService;

    @Mock
    private ICrmWecomGroupChatService groupChatService;

    @Mock
    private ISysDictTypeService dictTypeService;

    @InjectMocks
    private CrmWecomSidebarResolveServiceImpl resolveService;

    @BeforeEach
    void setUp()
    {
        TenantDataSourceSupport.bindTenantKey(TENANT);
        SysUser user = new SysUser();
        user.setSnUserid(SN_USERID);
        LoginUser loginUser = new LoginUser(1L, 1L, user, Collections.emptySet());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown()
    {
        DynamicDataSourceContextHolder.clearDataSourceKey();
        SecurityContextHolder.clearContext();
    }

    @Test
    void resolveGroup_blankChatId_throws400()
    {
        ServiceException ex = assertThrows(ServiceException.class, () -> resolveService.resolveGroup(" "));
        assertEquals(Integer.valueOf(400), ex.getCode());
    }

    @Test
    void resolveGroup_unlinked_triesGroupDetail()
    {
        when(contactGroupMapper.selectListByChatId(TENANT, CHAT_ID)).thenReturn(Collections.emptyList());
        stubGroupChatOk("临时客户群", "lisi", EXTERNAL_USERID);

        WecomSidebarGroupResolveResultDto result = resolveService.resolveGroup(CHAT_ID);

        assertEquals(WecomSidebarGroupResolveResultDto.STATUS_UNLINKED_GROUP, result.getCrmStatus());
        assertEquals(CHAT_ID, result.getChatId());
        assertNull(result.getContactId());
        assertNotNull(result.getGroup());
        assertEquals("临时客户群", result.getGroup().getName());
        assertEquals(Integer.valueOf(2), result.getGroup().getMemberCount());
        verify(crmContactMapper, never()).selectCrmContactByContactId(anyString());
    }

    @Test
    void resolveGroup_duplicateBinding()
    {
        when(contactGroupMapper.selectListByChatId(TENANT, CHAT_ID))
                .thenReturn(Arrays.asList(binding("g1"), binding("g2")));

        WecomSidebarGroupResolveResultDto result = resolveService.resolveGroup(CHAT_ID);

        assertEquals(WecomSidebarGroupResolveResultDto.STATUS_DUPLICATE_BINDING, result.getCrmStatus());
        assertNull(result.getContactId());
        assertNull(result.getWecom());
        assertNull(result.getCrm());
        verify(groupChatService, never()).getGroupChat(anyString(), anyString(), anyInt());
    }

    @Test
    void resolveGroup_linked_consistencyOk()
    {
        when(contactGroupMapper.selectListByChatId(TENANT, CHAT_ID))
                .thenReturn(Collections.singletonList(binding("g1")));
        when(crmContactMapper.selectCrmContactByContactId(CONTACT_ID)).thenReturn(contactWithWecom());
        stubWecomCard();
        stubGroupChatOk("张三-服务群", "zhangsan", EXTERNAL_USERID);

        WecomSidebarGroupResolveResultDto result = resolveService.resolveGroup(CHAT_ID);

        assertEquals(WecomSidebarGroupResolveResultDto.STATUS_LINKED, result.getCrmStatus());
        assertEquals(CONTACT_ID, result.getContactId());
        assertEquals(WecomSidebarGroupResolveResultDto.CONSISTENCY_OK, result.getConsistency().getStatus());
        assertEquals(EXTERNAL_USERID, result.getWecom().getExternalUserid());
        assertEquals("张三 CRM", result.getCrm().getDisplayName());
        assertEquals("张三-服务群", result.getGroup().getName());
    }

    @Test
    void resolveGroup_linked_consistencyMismatch()
    {
        when(contactGroupMapper.selectListByChatId(TENANT, CHAT_ID))
                .thenReturn(Collections.singletonList(binding("g1")));
        when(crmContactMapper.selectCrmContactByContactId(CONTACT_ID)).thenReturn(contactWithWecom());
        stubWecomCard();
        stubGroupChatOk("群", "owner", "wm-other");

        WecomSidebarGroupResolveResultDto result = resolveService.resolveGroup(CHAT_ID);

        assertEquals(WecomSidebarGroupResolveResultDto.STATUS_LINKED, result.getCrmStatus());
        assertEquals(WecomSidebarGroupResolveResultDto.CONSISTENCY_MISMATCH,
                result.getConsistency().getStatus());
        assertNotNull(result.getCrm());
    }

    @Test
    void resolveGroup_linked_noWecomIdentity()
    {
        when(contactGroupMapper.selectListByChatId(TENANT, CHAT_ID))
                .thenReturn(Collections.singletonList(binding("g1")));
        CrmContact contact = new CrmContact();
        contact.setContactId(CONTACT_ID);
        contact.setName("无企微");
        contact.setWecomExternalUserid(null);
        when(crmContactMapper.selectCrmContactByContactId(CONTACT_ID)).thenReturn(contact);
        when(dictTypeService.selectDictDataByType("crm_opty_stage_closed_won")).thenReturn(Collections.emptyList());
        when(crmOptyMapper.selectLatestActiveByContactId(eq(CONTACT_ID), any())).thenReturn(null);

        WecomSidebarGroupResolveResultDto result = resolveService.resolveGroup(CHAT_ID);

        assertEquals(WecomSidebarGroupResolveResultDto.CONSISTENCY_NO_WECOM_IDENTITY,
                result.getConsistency().getStatus());
        assertEquals("无企微", result.getCrm().getDisplayName());
        verify(groupChatService, never()).getGroupChat(anyString(), anyString(), anyInt());
    }

    @Test
    void resolveGroup_linked_groupDetailUnavailable_stillHasContactSummary()
    {
        when(contactGroupMapper.selectListByChatId(TENANT, CHAT_ID))
                .thenReturn(Collections.singletonList(binding("g1")));
        when(crmContactMapper.selectCrmContactByContactId(CONTACT_ID)).thenReturn(contactWithWecom());
        stubWecomCard();
        when(wecomConfigMapper.selectFirst()).thenReturn(null);

        WecomSidebarGroupResolveResultDto result = resolveService.resolveGroup(CHAT_ID);

        assertEquals(WecomSidebarGroupResolveResultDto.STATUS_LINKED, result.getCrmStatus());
        assertEquals(WecomSidebarGroupResolveResultDto.CONSISTENCY_GROUP_DETAIL_UNAVAILABLE,
                result.getConsistency().getStatus());
        assertNotNull(result.getCrm());
        assertEquals("张三 CRM", result.getCrm().getDisplayName());
        assertNull(result.getGroup());
    }

    private void stubWecomCard()
    {
        WecomExternalContactProfile profile = new WecomExternalContactProfile();
        profile.setExternalUserid(EXTERNAL_USERID);
        profile.setName("张三");
        when(profileResolver.fetch(EXTERNAL_USERID)).thenReturn(Optional.of(profile));
        when(wecomExternalContactMapper.selectByExternalAndFollow(EXTERNAL_USERID, SN_USERID))
                .thenReturn(localRow());
        when(dictTypeService.selectDictDataByType("crm_opty_stage_closed_won")).thenReturn(Collections.emptyList());
        when(crmOptyMapper.selectLatestActiveByContactId(eq(CONTACT_ID), any())).thenReturn(null);
    }

    private void stubGroupChatOk(String name, String owner, String externalMemberUserid)
    {
        CrmWecomConfig cfg = new CrmWecomConfig();
        cfg.setCorpId("corp-1");
        when(wecomConfigMapper.selectFirst()).thenReturn(cfg);
        when(wecomAccessTokenService.getAgentAccessToken(cfg)).thenReturn("token");

        WecomGroupChatDetailResponse.GroupChatMember staff = new WecomGroupChatDetailResponse.GroupChatMember();
        staff.setType(1);
        staff.setUserid(owner);
        WecomGroupChatDetailResponse.GroupChatMember ext = new WecomGroupChatDetailResponse.GroupChatMember();
        ext.setType(2);
        ext.setUserid(externalMemberUserid);

        WecomGroupChatDetailResponse.WecomGroupChatDetail detail =
                new WecomGroupChatDetailResponse.WecomGroupChatDetail();
        detail.setName(name);
        detail.setOwner(owner);
        detail.setMemberList(Arrays.asList(staff, ext));

        WecomGroupChatDetailResponse resp = new WecomGroupChatDetailResponse();
        resp.setErrcode(0);
        resp.setGroupChat(detail);
        when(groupChatService.getGroupChat("token", CHAT_ID, 0)).thenReturn(resp);
    }

    private static CrmWecomContactGroup binding(String id)
    {
        CrmWecomContactGroup g = new CrmWecomContactGroup();
        g.setId(id);
        g.setContactId(CONTACT_ID);
        g.setChatId(CHAT_ID);
        g.setTenantId(TENANT);
        return g;
    }

    private static CrmContact contactWithWecom()
    {
        CrmContact contact = new CrmContact();
        contact.setContactId(CONTACT_ID);
        contact.setName("张三 CRM");
        contact.setMobilePhone("13800138000");
        contact.setWecomExternalUserid(EXTERNAL_USERID);
        return contact;
    }

    private static CrmWecomExternalContact localRow()
    {
        CrmWecomExternalContact row = new CrmWecomExternalContact();
        row.setExternalUserid(EXTERNAL_USERID);
        row.setFollowUserid(SN_USERID);
        row.setCrmContactId(CONTACT_ID);
        return row;
    }
}
