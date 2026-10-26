package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateRequest;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateResult;
import com.ruoyi.crm.domain.dto.LeadConvertRequest;
import com.ruoyi.crm.domain.dto.LeadConvertResult;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmActivityMapper;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmConversionHistoryMapper;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.mapper.CrmWebchatConversationMapper;
import com.ruoyi.crm.mapper.CrmWebchatVisitorMapper;
import com.ruoyi.crm.rule.SmartPushEventExecutor;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;
import com.ruoyi.crm.service.ICrmOptyService;
import com.ruoyi.crm.service.IEventAudienceService;
import com.ruoyi.crm.service.support.LeadArrivedStageDetector;
import com.ruoyi.crm.service.support.LeadToContactFieldMapper;
import com.ruoyi.crm.wecom.WecomLeadConvertSyncSupport;
import com.ruoyi.crm.wechatmp.service.MpLeadConvertSyncSupport;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;

@ExtendWith(MockitoExtension.class)
class CrmLeadConvertServiceTest
{
    @Mock
    private CrmLeadMapper crmLeadMapper;
    @Mock
    private CrmActivityMapper crmActivityMapper;
    @Mock
    private CrmWecomExternalContactMapper wecomExternalContactMapper;
    @Mock
    private CrmContactMapper crmContactMapper;
    @Mock
    private CrmConversionHistoryMapper conversionHistoryMapper;
    @Mock
    private ICrmOptyService crmOptyService;
    @Mock
    private CrmEventMapper crmEventMapper;
    @Mock
    private CrmEventRegistrationMapper registrationMapper;
    @Mock
    private ICrmLeadConvertConfigService configService;
    @Mock
    private ICrmCustomerIdentityService identityService;
    @Mock
    private LeadToContactFieldMapper fieldMapper;
    @Mock
    private LeadDedupeService leadDedupeService;
    @Mock
    private com.ruoyi.crm.service.support.LeadConvertOwnerResolver convertOwnerResolver;
    @Mock
    private ActivityEventProducer activityEventProducer;
    @Mock
    private WecomLeadConvertSyncSupport wecomLeadConvertSyncSupport;
    @Mock
    private MpLeadConvertSyncSupport mpLeadConvertSyncSupport;
    @Mock
    private IEventAudienceService eventAudienceService;
    @Mock
    private LeadArrivedStageDetector arrivedStageDetector;
    @Mock
    private SmartPushEventExecutor smartPushEventExecutor;
    @Mock
    private WebsiteVisitorSessionService visitorSessionService;
    @Mock
    private CrmWebchatVisitorMapper webchatVisitorMapper;
    @Mock
    private CrmWebchatConversationMapper webchatConversationMapper;

    @InjectMocks
    private CrmLeadConvertServiceImpl service;

    private LeadConvertConfigVo defaultConfig;

    @BeforeEach
    void setUp()
    {
        defaultConfig = LeadConvertConfigVo.defaults();
    }

    @Test
    void convert_skipsWhenAlreadyConverted()
    {
        CrmLead lead = baseLead();
        lead.setContactId("existing-contact");
        when(crmLeadMapper.selectByLeadIdForUpdate("lead-1")).thenReturn(lead);

        LeadConvertResult result = service.convert("lead-1", new LeadConvertRequest(), "tester");

        assertTrue(result.isSkipped());
        assertEquals("existing-contact", result.getContactId());
        verify(crmContactMapper, never()).insertCrmContact(any());
    }

    @Test
    void convert_missingMobileThrows()
    {
        CrmLead lead = baseLead();
        lead.setMobilePhone(null);
        when(crmLeadMapper.selectByLeadIdForUpdate("lead-1")).thenReturn(lead);
        when(configService.getConfig(null)).thenReturn(defaultConfig);

        LeadConvertRequest req = new LeadConvertRequest();
        req.setOrgId("org-1");

        assertThrows(ServiceException.class, () -> service.convert("lead-1", req, "tester"));
    }

    @Test
    void convert_createsContactAndWritesBack()
    {
        CrmLead lead = baseLead();
        when(crmLeadMapper.selectByLeadIdForUpdate("lead-1")).thenReturn(lead);
        when(configService.getConfig(null)).thenReturn(defaultConfig);
        when(leadDedupeService.findDuplicateContact(any(), any())).thenReturn(null);

        CrmContact mapped = new CrmContact();
        mapped.setName("张三");
        mapped.setMobilePhone("13800138000");
        mapped.setOrgId("org-1");
        when(fieldMapper.mapNewContact(eq(lead), eq("org-1"), eq("张三"), eq("13800138000"), any()))
                .thenReturn(mapped);
        when(crmContactMapper.insertCrmContact(any())).thenReturn(1);
        when(crmLeadMapper.updateCrmLead(any())).thenReturn(1);
        when(conversionHistoryMapper.insertCrmConversionHistory(any())).thenReturn(1);
        when(wecomExternalContactMapper.selectList(any())).thenReturn(Collections.emptyList());

        LeadConvertRequest req = new LeadConvertRequest();
        req.setOrgId("org-1");
        LeadConvertResult result = service.convert("lead-1", req, "tester");

        assertFalse(result.isSkipped());
        assertFalse(result.isMerged());
        verify(activityEventProducer).produceLeadConverted(eq("lead-1"), any(), eq("Web"), eq("manual"), eq(false),
                any(), any());
        verify(crmActivityMapper).migrateWebsitePageViewSubject(eq("lead-1"), any());
        verify(visitorSessionService).migrateSubjectLeadToContact(eq("lead-1"), any());
        verify(crmContactMapper).insertCrmContact(any());
        verify(convertOwnerResolver).applyToNewContact(any(), eq(lead), eq(defaultConfig), eq("tester"));
    }

    @Test
    void evaluate_missingMobileNotEligible()
    {
        CrmLead lead = baseLead();
        lead.setMobilePhone(null);
        when(crmLeadMapper.selectCrmLeadByLeadId("lead-1")).thenReturn(lead);
        when(configService.getConfig(null)).thenReturn(defaultConfig);

        LeadConvertEvaluateRequest req = new LeadConvertEvaluateRequest();
        req.setTrigger("stage_arrived");
        LeadConvertEvaluateResult result = service.evaluate("lead-1", req);

        assertFalse(result.isEligible());
        assertEquals("missing_mobile", result.getReason());
    }

    @Test
    void evaluate_stageArrivedEligibleWhenConfigured()
    {
        CrmLead lead = baseLead();
        lead.setOrgId("org-1");
        when(crmLeadMapper.selectCrmLeadByLeadId("lead-1")).thenReturn(lead);
        when(configService.getConfig(null)).thenReturn(defaultConfig);

        LeadConvertEvaluateRequest req = new LeadConvertEvaluateRequest();
        req.setTrigger("stage_arrived");
        LeadConvertEvaluateResult result = service.evaluate("lead-1", req);

        assertTrue(result.isEligible());
        assertEquals("org-1", result.getOrgId());
    }

    @Test
    void evaluateStageArrived_onlyWhenStageChangesToArrived()
    {
        CrmLead before = baseLead();
        before.setStage("跟进中");
        CrmLead after = baseLead();
        after.setStage("已到店");
        after.setOrgId("org-1");

        when(arrivedStageDetector.stageChangedToArrived("跟进中", "已到店")).thenReturn(true);
        when(crmLeadMapper.selectCrmLeadByLeadId("lead-1")).thenReturn(after);
        when(configService.getConfig(null)).thenReturn(defaultConfig);

        LeadConvertEvaluateResult result = service.evaluateStageArrived(before, after);
        assertTrue(result.isEligible());
    }

    private static CrmLead baseLead()
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId("lead-1");
        lead.setLeadName("张三");
        lead.setMobilePhone("13800138000");
        lead.setLeadSource("Web");
        return lead;
    }
}
