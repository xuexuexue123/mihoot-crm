package com.ruoyi.crm.wechatmp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.support.MpLeadIdentitySupport;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.mapper.ActivityUnmatchedMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;

@ExtendWith(MockitoExtension.class)
class MpLeadConvertSyncSupportTest
{
    private static final String LEAD_ID = "lead-1";
    private static final String CONTACT_ID = "contact-1";
    private static final String OPEN_ID = "o_mp_open_1";
    private static final String APP_ID = "wx_app_1";
    private static final String UNION_ID = "o_union_mp_1";

    @Mock
    private CrmLeadMapper crmLeadMapper;

    @Mock
    private CrmContactMapper crmContactMapper;

    @Mock
    private CrmWechatMpFanMapper fanMapper;

    @Mock
    private ActivityUnmatchedMapper unmatchedMapper;

    @Mock
    private WechatMpIdentityResolutionSupport mpIdentityResolutionSupport;

    @Mock
    private WechatMpUserProfileResolver userProfileResolver;

    @InjectMocks
    private MpLeadConvertSyncSupport support;

    private CrmLead lead;
    private CrmContact contact;

    @BeforeEach
    void setUp()
    {
        lead = new CrmLead();
        lead.setLeadId(LEAD_ID);
        lead.setWechatMpOpenid(OPEN_ID);
        lead.setWechatMpAppId(APP_ID);

        contact = new CrmContact();
        contact.setContactId(CONTACT_ID);
        contact.setName("测试客户");
    }

    @Test
    void syncAfterLeadConverted_noOpenId_skipsAll()
    {
        CrmLead plainLead = new CrmLead();
        plainLead.setLeadId(LEAD_ID);
        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(plainLead);
        when(crmContactMapper.selectCrmContactByContactId(CONTACT_ID)).thenReturn(contact);

        support.syncAfterLeadConverted(LEAD_ID, CONTACT_ID);

        verify(mpIdentityResolutionSupport, never()).bindOpenIdIfNeeded(any(), any(), any());
        verify(mpIdentityResolutionSupport, never()).bindUnionIdIfNeeded(any(), any(), any());
        verify(fanMapper, never()).upsert(any());
    }

    @Test
    void syncAfterLeadConverted_fanHasUnionId_bindsAndBackfills()
    {
        CrmWechatMpFan fan = new CrmWechatMpFan();
        fan.setAppId(APP_ID);
        fan.setOpenId(OPEN_ID);
        fan.setUnionId(UNION_ID);

        ActivityUnmatched unmatched = new ActivityUnmatched();
        unmatched.setId(10L);
        unmatched.setPulseId("mp_msg_1");
        unmatched.setIdentityType("wechat_mp_openid");
        unmatched.setIdentityValue(OPEN_ID);

        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(lead);
        when(crmContactMapper.selectCrmContactByContactId(CONTACT_ID)).thenReturn(contact);
        when(fanMapper.selectByOpenId(APP_ID, OPEN_ID)).thenReturn(fan);
        when(unmatchedMapper.selectPendingByOpenId(OPEN_ID, APP_ID))
                .thenReturn(Collections.singletonList(unmatched));

        support.syncAfterLeadConverted(LEAD_ID, CONTACT_ID);

        verify(mpIdentityResolutionSupport).bindOpenIdIfNeeded(
                eq(OPEN_ID), eq(CONTACT_ID), eq(WechatMpIdentityResolutionSupport.SOURCE_LEAD_CONVERT));
        verify(mpIdentityResolutionSupport).bindUnionIdIfNeeded(
                eq(UNION_ID), eq(CONTACT_ID), eq(WechatMpIdentityResolutionSupport.SOURCE_LEAD_CONVERT));
        verify(fanMapper).upsert(any(CrmWechatMpFan.class));
        verify(mpIdentityResolutionSupport).markUnmatchedMatched(eq(unmatched), eq(CONTACT_ID), eq("mp_msg_1"), eq(false));
        verify(userProfileResolver, never()).resolve(any(), any());
    }

    @Test
    void syncAfterLeadConverted_customFieldOpenId_resolvesAndBinds()
    {
        CrmLead customLead = new CrmLead();
        customLead.setLeadId(LEAD_ID);
        customLead.setCustomField("{\""
                + MpLeadIdentitySupport.CUSTOM_FIELD_OPENID + "\":\"" + OPEN_ID + "\",\""
                + MpLeadIdentitySupport.CUSTOM_FIELD_APP_ID + "\":\"" + APP_ID + "\"}");

        CrmWechatMpFan fan = new CrmWechatMpFan();
        fan.setAppId(APP_ID);
        fan.setOpenId(OPEN_ID);
        fan.setUnionId(UNION_ID);

        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(customLead);
        when(crmContactMapper.selectCrmContactByContactId(CONTACT_ID)).thenReturn(contact);
        when(fanMapper.selectByOpenId(APP_ID, OPEN_ID)).thenReturn(fan);
        when(unmatchedMapper.selectPendingByOpenId(OPEN_ID, APP_ID)).thenReturn(Collections.emptyList());

        support.syncAfterLeadConverted(LEAD_ID, CONTACT_ID);

        verify(mpIdentityResolutionSupport).bindUnionIdIfNeeded(
                eq(UNION_ID), eq(CONTACT_ID), eq(WechatMpIdentityResolutionSupport.SOURCE_LEAD_CONVERT));
    }

    @Test
    void syncAfterLeadConverted_unionIdFromUnmatchedWhenFanMissing()
    {
        ActivityUnmatched unmatched = new ActivityUnmatched();
        unmatched.setId(11L);
        unmatched.setPulseId("mp_msg_2");
        unmatched.setUnionId(UNION_ID);

        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(lead);
        when(crmContactMapper.selectCrmContactByContactId(CONTACT_ID)).thenReturn(contact);
        when(fanMapper.selectByOpenId(APP_ID, OPEN_ID)).thenReturn(null);
        when(unmatchedMapper.selectPendingByOpenId(OPEN_ID, APP_ID))
                .thenReturn(Collections.singletonList(unmatched));

        support.syncAfterLeadConverted(LEAD_ID, CONTACT_ID);

        verify(mpIdentityResolutionSupport).bindUnionIdIfNeeded(
                eq(UNION_ID), eq(CONTACT_ID), eq(WechatMpIdentityResolutionSupport.SOURCE_LEAD_CONVERT));
        verify(mpIdentityResolutionSupport).markUnmatchedMatched(eq(unmatched), eq(CONTACT_ID), eq("mp_msg_2"), eq(false));
        verify(userProfileResolver, never()).resolve(any(), any());
    }

    @Test
    void syncAfterLeadConverted_missingLeadOrContact_noOp()
    {
        when(crmLeadMapper.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(null);

        support.syncAfterLeadConverted(LEAD_ID, CONTACT_ID);

        verify(mpIdentityResolutionSupport, never()).bindOpenIdIfNeeded(any(), any(), any());
    }
}
