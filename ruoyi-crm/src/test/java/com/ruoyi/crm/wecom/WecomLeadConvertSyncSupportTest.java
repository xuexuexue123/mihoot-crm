package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmConversionHistory;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmSubjectMap;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.service.ICrmSubjectMapService;

@ExtendWith(MockitoExtension.class)
class WecomLeadConvertSyncSupportTest
{
    @Mock
    private CrmLeadMapper crmLeadMapper;

    @Mock
    private CrmContactMapper crmContactMapper;

    @Mock
    private CrmWecomExternalContactMapper wecomExternalContactMapper;

    @Mock
    private WecomExternalContactProfileResolver profileResolver;

    @Mock
    private WecomIdentityResolutionSupport identityResolutionSupport;

    @Mock
    private WecomContactConvertSyncSupport wecomContactConvertSyncSupport;

    @Mock
    private ICrmSubjectMapService subjectMapService;

    @InjectMocks
    private WecomLeadConvertSyncSupport support;

    private CrmLead wecomLead;
    private CrmContact contact;

    @BeforeEach
    void setUp()
    {
        wecomLead = new CrmLead();
        wecomLead.setLeadId("lead-1");
        wecomLead.setLeadSource("wecom");
        wecomLead.setWecomExternalUserid("wm_ext_1");
        wecomLead.setWecomFollowUserid("staff1");
        wecomLead.setLeadName("企微客户");
        wecomLead.setContactName("张总");

        contact = new CrmContact();
        contact.setContactId("contact-1");
        contact.setName("用户填写姓名");
    }

    @Test
    void isLeadToContactConversion_matchesContactType()
    {
        CrmConversionHistory history = new CrmConversionHistory();
        history.setConversionType("Contact");
        history.setLeadId("lead-1");
        history.setTargetId("contact-1");
        assertTrue(WecomLeadConvertSyncSupport.isLeadToContactConversion(history));

        history.setConversionType("Marketing Event");
        assertFalse(WecomLeadConvertSyncSupport.isLeadToContactConversion(history));
    }

    @Test
    void isNewContactLink_detectsFirstLink()
    {
        CrmLead before = new CrmLead();
        before.setContactId(null);
        CrmLead after = new CrmLead();
        after.setContactId("contact-1");
        assertTrue(WecomLeadConvertSyncSupport.isNewContactLink(before, after));

        before.setContactId("existing");
        assertFalse(WecomLeadConvertSyncSupport.isNewContactLink(before, after));
    }

    @Test
    void syncAfterLeadConverted_nonWecomLead_skipsIdentityBind()
    {
        CrmLead plainLead = new CrmLead();
        plainLead.setLeadId("lead-2");
        plainLead.setLeadSource("web");
        when(crmLeadMapper.selectCrmLeadByLeadId("lead-2")).thenReturn(plainLead);
        when(crmContactMapper.selectCrmContactByContactId("contact-1")).thenReturn(contact);

        support.syncAfterLeadConverted("lead-2", "contact-1");

        verify(identityResolutionSupport, never()).bindWecomIdentities(any(), any(), any(), any());
        verify(subjectMapService).upsert(any(CrmSubjectMap.class));
    }

    @Test
    void syncAfterLeadConverted_wecomLead_bindsIdentitiesAndPreservesContactName()
    {
        String rawJson = "{\"external_contact\":{\"external_userid\":\"wm_ext_1\",\"name\":\"昵称\","
                + "\"unionid\":\"o_union_001\",\"avatar\":\"https://a.example/x.png\",\"gender\":1,\"type\":1}}";
        CrmWecomExternalContact extRow = new CrmWecomExternalContact();
        extRow.setExternalUserid("wm_ext_1");
        extRow.setRawJson(rawJson);

        when(crmLeadMapper.selectCrmLeadByLeadId("lead-1")).thenReturn(wecomLead);
        when(crmContactMapper.selectCrmContactByContactId("contact-1")).thenReturn(contact);

        when(wecomExternalContactMapper.selectByExternalUserid("wm_ext_1"))
                .thenReturn(Collections.singletonList(extRow));

        WecomExternalContactProfile profile = new WecomExternalContactProfile();
        profile.setUnionId("o_union_001");
        profile.setRawJson(rawJson);
        when(profileResolver.fetch("wm_ext_1")).thenReturn(java.util.Optional.of(profile));

        support.syncAfterLeadConverted("lead-1", "contact-1");

        verify(identityResolutionSupport).bindWecomIdentities(
                eq("wm_ext_1"),
                eq("o_union_001"),
                eq("contact-1"),
                eq(WecomIdentityResolutionSupport.SOURCE_LEAD_CONVERT));
        verify(crmContactMapper).updateCrmContact(any(CrmContact.class));
        verify(wecomContactConvertSyncSupport).syncAfterLeadConverted(eq(wecomLead), any(CrmContact.class));
        verify(subjectMapService).upsert(any(CrmSubjectMap.class));
    }

    @Test
    void syncAfterLeadConverted_idempotentSecondCallDoesNotThrow()
    {
        when(crmLeadMapper.selectCrmLeadByLeadId("lead-1")).thenReturn(wecomLead);
        when(crmContactMapper.selectCrmContactByContactId("contact-1")).thenReturn(contact);
        when(wecomExternalContactMapper.selectByExternalUserid("wm_ext_1")).thenReturn(Collections.emptyList());
        when(profileResolver.fetch("wm_ext_1")).thenReturn(java.util.Optional.empty());

        support.syncAfterLeadConverted("lead-1", "contact-1");
        support.syncAfterLeadConverted("lead-1", "contact-1");

        verify(identityResolutionSupport, org.mockito.Mockito.times(2)).bindWecomIdentities(
                eq("wm_ext_1"),
                nullable(String.class),
                eq("contact-1"),
                eq(WecomIdentityResolutionSupport.SOURCE_LEAD_CONVERT));
    }
}
