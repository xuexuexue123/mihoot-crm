package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.wecom.WecomExternalContactProfileResolver;
import com.ruoyi.crm.wecom.WecomIdentityResolutionSupport;
import com.ruoyi.system.service.ISysDictTypeService;

@ExtendWith(MockitoExtension.class)
class CrmWecomSidebarResolveServiceTest
{
    private static final String EXTERNAL_USERID = "wm-ext-1";
    private static final String SN_USERID = "staff-1";
    private static final String CONTACT_ID = "contact-1";

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
    private ISysDictTypeService dictTypeService;

    @InjectMocks
    private CrmWecomSidebarResolveServiceImpl resolveService;

    @BeforeEach
    void setUpLoginUser()
    {
        SysUser user = new SysUser();
        user.setSnUserid(SN_USERID);
        LoginUser loginUser = new LoginUser(1L, 1L, user, Collections.emptySet());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearLoginUser()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void resolve_returnsForbiddenWhenNotFollower()
    {
        String raw = "{\"external_contact\":{\"external_userid\":\"wm-ext-1\",\"name\":\"张三\"},"
                + "\"follow_user\":[{\"userid\":\"other-staff\",\"remark\":\"张总\"}]}";
        WecomExternalContactProfile profile = profileWithRaw(raw);
        when(profileResolver.fetch(EXTERNAL_USERID)).thenReturn(Optional.of(profile));
        when(wecomExternalContactMapper.selectByExternalAndFollow(EXTERNAL_USERID, SN_USERID)).thenReturn(null);

        WecomSidebarResolveResultDto result = resolveService.resolve(EXTERNAL_USERID);

        assertEquals("forbidden", result.getCrmStatus());
        assertEquals(EXTERNAL_USERID, result.getWecom().getExternalUserid());
        assertNull(result.getCrm());
        assertNull(result.getContactId());
    }

    @Test
    void resolve_returnsUnlinkedWhenFollowerWithoutContact()
    {
        String raw = "{\"external_contact\":{\"external_userid\":\"wm-ext-1\",\"name\":\"张三\",\"avatar\":\"http://a\"},"
                + "\"follow_user\":[{\"userid\":\"staff-1\",\"remark\":\"张总\","
                + "\"tags\":[{\"group_name\":\"来源\",\"tag_name\":\"大众点评\"}]}]}";
        when(profileResolver.fetch(EXTERNAL_USERID)).thenReturn(Optional.of(profileWithRaw(raw)));
        when(wecomExternalContactMapper.selectByExternalAndFollow(EXTERNAL_USERID, SN_USERID))
                .thenReturn(localRow(null));
        when(identityResolutionSupport.resolveContactId(EXTERNAL_USERID, null)).thenReturn(null);

        WecomSidebarResolveResultDto result = resolveService.resolve(EXTERNAL_USERID);

        assertEquals("unlinked", result.getCrmStatus());
        assertEquals("张三", result.getWecom().getName());
        assertEquals("张总", result.getWecom().getRemark());
        assertEquals(1, result.getWecom().getCorpTags().size());
        assertNull(result.getCrm());
    }

    @Test
    void resolve_returnsLinkedContactWithCrmSummary()
    {
        String raw = "{\"external_contact\":{\"external_userid\":\"wm-ext-1\",\"name\":\"张三\"},"
                + "\"follow_user\":[{\"userid\":\"staff-1\",\"remark\":\"张总\"}]}";
        when(profileResolver.fetch(EXTERNAL_USERID)).thenReturn(Optional.of(profileWithRaw(raw)));
        when(wecomExternalContactMapper.selectByExternalAndFollow(EXTERNAL_USERID, SN_USERID))
                .thenReturn(localRow(CONTACT_ID));
        when(identityResolutionSupport.resolveContactId(EXTERNAL_USERID, null)).thenReturn(CONTACT_ID);

        CrmContact contact = new CrmContact();
        contact.setContactId(CONTACT_ID);
        contact.setName("张三 CRM");
        contact.setMobilePhone("13800138000");
        contact.setTag("[{\"id\":\"10001\",\"text\":\"VIP\"}]");
        when(crmContactMapper.selectCrmContactByContactId(CONTACT_ID)).thenReturn(contact);
        when(dictTypeService.selectDictDataByType("crm_opty_stage_closed_won")).thenReturn(Collections.emptyList());
        when(crmOptyMapper.selectLatestActiveByContactId(eq(CONTACT_ID), any())).thenReturn(null);

        WecomSidebarResolveResultDto result = resolveService.resolve(EXTERNAL_USERID);

        assertEquals("linked_contact", result.getCrmStatus());
        assertEquals(CONTACT_ID, result.getContactId());
        assertNotNull(result.getCrm());
        assertEquals("张三 CRM", result.getCrm().getDisplayName());
        assertEquals("13800138000", result.getCrm().getPhone());
        assertEquals(1, result.getCrm().getTags().size());
        assertEquals("VIP", result.getCrm().getTags().get(0));
    }

    private static WecomExternalContactProfile profileWithRaw(String raw)
    {
        WecomExternalContactProfile profile = new WecomExternalContactProfile();
        profile.setExternalUserid(EXTERNAL_USERID);
        profile.setName("张三");
        profile.setRawJson(raw);
        return profile;
    }

    private static CrmWecomExternalContact localRow(String contactId)
    {
        CrmWecomExternalContact row = new CrmWecomExternalContact();
        row.setExternalUserid(EXTERNAL_USERID);
        row.setFollowUserid(SN_USERID);
        row.setCrmContactId(contactId);
        return row;
    }
}
