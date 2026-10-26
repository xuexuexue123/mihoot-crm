package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.mapper.CrmActivityMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;
import com.ruoyi.crm.wechatmp.service.WechatMpIdentityResolutionSupport;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OutreachRegistrationIdentityResolverTest
{
    private static final String EVENT_ID = "event-1";
    private static final String TENANT_ID = "tenant-1";
    private static final String OPEN_ID = "o-test-openid";
    private static final String APP_ID = "wx-app-1";
    private static final String CONTACT_ID = "contact-aaa";

    @Mock
    private CrmActivityMapper activityMapper;
    @Mock
    private CrmWechatMpFanMapper fanMapper;
    @Mock
    private ICrmCustomerIdentityService identityService;
    @Mock
    private WechatMpIdentityResolutionSupport mpIdentityResolutionSupport;
    @Mock
    private ICrmLeadConvertConfigService leadConvertConfigService;
    @Mock
    private OutreachEventLeadResolveService leadResolveService;

    @InjectMocks
    private OutreachRegistrationIdentityResolver resolver;

    private void stubDefaultConfig()
    {
        LeadConvertConfigVo config = LeadConvertConfigVo.defaults();
        when(leadConvertConfigService.getConfig(TENANT_ID)).thenReturn(config);
    }

    @Test
    void resolve_activityFollowContact_takesPriorityOverLead()
    {
        stubDefaultConfig();
        when(activityMapper.selectLatestFollowContactIdByEventAndOpenId(EVENT_ID, OPEN_ID))
                .thenReturn(CONTACT_ID);

        OutreachRegistrationIdentityResolution result = resolver.resolve(
                TENANT_ID, EVENT_ID, "13922009933", OPEN_ID, APP_ID);

        assertTrue(result.isContactKnown());
        assertEquals(CONTACT_ID, result.getContactId());
        assertEquals("activity_follow", result.getReason());
    }

    @Test
    void resolve_fanContact_whenNoActivity()
    {
        stubDefaultConfig();
        when(activityMapper.selectLatestFollowContactIdByEventAndOpenId(EVENT_ID, OPEN_ID))
                .thenReturn(null);
        CrmWechatMpFan fan = new CrmWechatMpFan();
        fan.setContactId(CONTACT_ID);
        when(fanMapper.selectByOpenId(APP_ID, OPEN_ID)).thenReturn(fan);

        OutreachRegistrationIdentityResolution result = resolver.resolve(
                TENANT_ID, EVENT_ID, null, OPEN_ID, APP_ID);

        assertTrue(result.isContactKnown());
        assertEquals("mp_fan", result.getReason());
    }

    @Test
    void resolve_mobileContact_whenMergeEnabled()
    {
        stubDefaultConfig();
        when(identityService.findContactByIdentity("mobile_phone", "13800138000"))
                .thenReturn(CONTACT_ID);

        OutreachRegistrationIdentityResolution result = resolver.resolve(
                TENANT_ID, EVENT_ID, "13800138000", null, null);

        assertTrue(result.isContactKnown());
        assertEquals("mobile", result.getReason());
    }

    @Test
    void resolve_existingLead_whenNoContact()
    {
        stubDefaultConfig();
        when(activityMapper.selectLatestFollowContactIdByEventAndOpenId(EVENT_ID, OPEN_ID))
                .thenReturn(null);
        when(fanMapper.selectByOpenId(APP_ID, OPEN_ID)).thenReturn(null);
        when(identityService.findContactByIdentity(eq("wechat_mp_openid"), eq(OPEN_ID)))
                .thenReturn(null);

        CrmLead lead = new CrmLead();
        lead.setLeadId("lead-existing");
        when(leadResolveService.resolveExistingLead(EVENT_ID, "13800138000", OPEN_ID, APP_ID))
                .thenReturn(lead);

        OutreachRegistrationIdentityResolution result = resolver.resolve(
                TENANT_ID, EVENT_ID, "13800138000", OPEN_ID, APP_ID);

        assertEquals(OutreachRegistrationIdentityResolution.Kind.LEAD_UPSERT, result.getKind());
        assertEquals("lead-existing", result.getExistingLead().getLeadId());
    }

    @Test
    void resolve_unknownUser_createsNewLead()
    {
        stubDefaultConfig();
        when(activityMapper.selectLatestFollowContactIdByEventAndOpenId(EVENT_ID, OPEN_ID))
                .thenReturn(null);
        when(fanMapper.selectByOpenId(APP_ID, OPEN_ID)).thenReturn(null);
        when(identityService.findContactByIdentity(eq("wechat_mp_openid"), eq(OPEN_ID)))
                .thenReturn(null);
        when(leadResolveService.resolveExistingLead(EVENT_ID, null, OPEN_ID, APP_ID))
                .thenReturn(null);

        OutreachRegistrationIdentityResolution result = resolver.resolve(
                TENANT_ID, EVENT_ID, null, OPEN_ID, APP_ID);

        assertEquals(OutreachRegistrationIdentityResolution.Kind.CREATE_NEW_LEAD, result.getKind());
        assertFalse(result.isContactKnown());
    }

    @Test
    void resolve_contactFromOpenIdIdentity_beforeLeadLookup()
    {
        stubDefaultConfig();
        when(activityMapper.selectLatestFollowContactIdByEventAndOpenId(EVENT_ID, OPEN_ID))
                .thenReturn(null);
        when(fanMapper.selectByOpenId(APP_ID, OPEN_ID)).thenReturn(null);
        when(identityService.findContactByIdentity("wechat_mp_openid", OPEN_ID))
                .thenReturn(CONTACT_ID);

        OutreachRegistrationIdentityResolution result = resolver.resolve(
                TENANT_ID, EVENT_ID, "13800138000", OPEN_ID, APP_ID);

        assertTrue(result.isContactKnown());
        assertEquals("mp_openid", result.getReason());
    }
}
