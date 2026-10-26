package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.domain.vo.LeadQualifyOutreachUrlVo;
import com.ruoyi.crm.service.ICrmLeadPoolConfigService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

@ExtendWith(MockitoExtension.class)
class LeadQualifyOutreachUrlServiceTest
{
    private static final String TENANT = "tenant-a";
    private static final String LEAD_ID = "lead-1";
    private static final String TEMPLATE_ID = "8a530792-da39-4230-826f-fa768b54373e";

    @Mock
    private ICrmLeadService crmLeadService;

    @Mock
    private ICrmLeadPoolConfigService poolConfigService;

    @Mock
    private ISysReTemplateService templateService;

    @Mock
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    @InjectMocks
    private LeadQualifyOutreachUrlService service;

    @Test
    void issueUrl_success()
    {
        when(crmLeadService.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(lead());
        CrmLeadPoolConfig config = new CrmLeadPoolConfig();
        config.setQualifyTemplateId(TEMPLATE_ID);
        when(poolConfigService.getConfig()).thenReturn(config);
        when(templateService.selectSysReTemplateByTemplateId(TEMPLATE_ID)).thenReturn(template());
        when(outreachRegistrationTokenService.isSigningConfigured()).thenReturn(true);
        when(outreachRegistrationTokenService.hasPublicBaseUrl(TENANT)).thenReturn(true);
        when(outreachRegistrationTokenService.computeQualifyExp()).thenReturn(1_700_000_000L);
        when(outreachRegistrationTokenService.buildQualifySurveyUrl(
                eq(TENANT), eq(LEAD_ID), eq(TEMPLATE_ID), eq(1_700_000_000L)))
                .thenReturn("https://tenant.example.com/qualify-survey?token=abc");

        LeadQualifyOutreachUrlVo vo = service.issueUrl(TENANT, LEAD_ID);

        assertTrue(vo.getQualifyUrl().contains("token=abc"));
        assertTrue(vo.getExpiresAt() != null && !vo.getExpiresAt().isEmpty());
    }

    @Test
    void issueUrl_missingTemplate_returns400()
    {
        when(crmLeadService.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(lead());
        CrmLeadPoolConfig config = new CrmLeadPoolConfig();
        config.setQualifyTemplateId(null);
        when(poolConfigService.getConfig()).thenReturn(config);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.issueUrl(TENANT, LEAD_ID));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
        assertTrue(ex.getMessage().contains("未配置"));
    }

    @Test
    void issueUrl_leadMissing_returns404()
    {
        when(crmLeadService.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.issueUrl(TENANT, LEAD_ID));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
    }

    @Test
    void issueUrl_templateNotFound_returns400()
    {
        when(crmLeadService.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(lead());
        CrmLeadPoolConfig config = new CrmLeadPoolConfig();
        config.setQualifyTemplateId(TEMPLATE_ID);
        when(poolConfigService.getConfig()).thenReturn(config);
        when(templateService.selectSysReTemplateByTemplateId(TEMPLATE_ID)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.issueUrl(TENANT, LEAD_ID));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void issueUrl_allowsConvertedLead()
    {
        CrmLead converted = lead();
        converted.setStatus("Converted");
        when(crmLeadService.selectCrmLeadByLeadId(LEAD_ID)).thenReturn(converted);
        CrmLeadPoolConfig config = new CrmLeadPoolConfig();
        config.setQualifyTemplateId(TEMPLATE_ID);
        when(poolConfigService.getConfig()).thenReturn(config);
        when(templateService.selectSysReTemplateByTemplateId(TEMPLATE_ID)).thenReturn(template());
        when(outreachRegistrationTokenService.isSigningConfigured()).thenReturn(true);
        when(outreachRegistrationTokenService.hasPublicBaseUrl(TENANT)).thenReturn(true);
        when(outreachRegistrationTokenService.computeQualifyExp()).thenReturn(1_700_000_000L);
        when(outreachRegistrationTokenService.buildQualifySurveyUrl(anyString(), anyString(), anyString(), anyLong()))
                .thenReturn("https://tenant.example.com/qualify-survey?token=x");

        LeadQualifyOutreachUrlVo vo = service.issueUrl(TENANT, LEAD_ID);
        assertTrue(vo.getQualifyUrl().contains("qualify-survey"));
        verify(crmLeadService).selectCrmLeadByLeadId(LEAD_ID);
    }

    private static CrmLead lead()
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId(LEAD_ID);
        lead.setLeadName("张三");
        lead.setStatus("Assigned");
        return lead;
    }

    private static SysReTemplate template()
    {
        SysReTemplate t = new SysReTemplate();
        t.setTemplateId(TEMPLATE_ID);
        t.setTemplateName("认定潜客评估");
        t.setContent("{\"title\":\"评估\"}");
        return t;
    }
}
