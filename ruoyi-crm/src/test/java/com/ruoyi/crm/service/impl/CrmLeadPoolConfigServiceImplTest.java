package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmLeadPoolConfigMapper;
import com.ruoyi.crm.service.support.EventRegistrationFormSupport;
import com.ruoyi.crm.service.support.LeadRecyclePolicyDefaults;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

@ExtendWith(MockitoExtension.class)
class CrmLeadPoolConfigServiceImplTest
{
    private static final String SURVEY_ID = "8a530792-da39-4230-826f-fa768b54373e";
    private static final String EXISTING_QUALIFY_ID = "existing-tpl-id";

    @Mock
    private CrmLeadPoolConfigMapper poolConfigMapper;

    @Mock
    private CrmLeadMapper crmLeadMapper;

    @Mock
    private ISysReTemplateService templateService;

    @InjectMocks
    private CrmLeadPoolConfigServiceImpl service;

    @Test
    void getConfig_returnsDefaultsWithNullQualifyTemplateId()
    {
        when(poolConfigMapper.selectFirst()).thenReturn(null);

        CrmLeadPoolConfig config = service.getConfig();

        assertEquals(Integer.valueOf(30), config.getClaimDailyLimit());
        assertNull(config.getQualifyTemplateId());
    }

    @Test
    void saveConfig_omitQualifyTemplateId_keepsExisting()
    {
        CrmLeadPoolConfig existing = baseExisting();
        existing.setQualifyTemplateId(EXISTING_QUALIFY_ID);
        when(poolConfigMapper.selectFirst()).thenReturn(existing);

        CrmLeadPoolConfig request = new CrmLeadPoolConfig();
        request.setClaimDailyLimit(50);
        request.setProtectDays(7);
        request.setRecycleEnabled(true);
        request.setDefaultPoolOrgScope("dept");

        when(poolConfigMapper.selectFirst()).thenReturn(existing, existing);

        service.saveConfig(request, false);

        ArgumentCaptor<CrmLeadPoolConfig> captor = ArgumentCaptor.forClass(CrmLeadPoolConfig.class);
        verify(poolConfigMapper).updateCrmLeadPoolConfig(captor.capture());
        assertEquals(EXISTING_QUALIFY_ID, captor.getValue().getQualifyTemplateId());
        assertEquals(Integer.valueOf(50), captor.getValue().getClaimDailyLimit());
        verify(templateService, never()).selectSysReTemplateByTemplateId(any());
    }

    @Test
    void saveConfig_validSurveyId_writes()
    {
        CrmLeadPoolConfig existing = baseExisting();
        existing.setQualifyTemplateId(EXISTING_QUALIFY_ID);
        when(poolConfigMapper.selectFirst()).thenReturn(existing, existing);

        SysReTemplate template = new SysReTemplate();
        template.setTemplateId(SURVEY_ID);
        template.setCategory(EventRegistrationFormSupport.SURVEY_CATEGORY);
        when(templateService.selectSysReTemplateByTemplateId(SURVEY_ID)).thenReturn(template);

        CrmLeadPoolConfig request = new CrmLeadPoolConfig();
        request.setClaimDailyLimit(30);
        request.setProtectDays(7);
        request.setRecycleEnabled(true);
        request.setDefaultPoolOrgScope("dept");
        request.setQualifyTemplateId(SURVEY_ID);

        service.saveConfig(request, true);

        ArgumentCaptor<CrmLeadPoolConfig> captor = ArgumentCaptor.forClass(CrmLeadPoolConfig.class);
        verify(poolConfigMapper).updateCrmLeadPoolConfig(captor.capture());
        assertEquals(SURVEY_ID, captor.getValue().getQualifyTemplateId());
    }

    @Test
    void saveConfig_missingTemplate_throws400()
    {
        CrmLeadPoolConfig existing = baseExisting();
        when(poolConfigMapper.selectFirst()).thenReturn(existing);
        when(templateService.selectSysReTemplateByTemplateId(SURVEY_ID)).thenReturn(null);

        CrmLeadPoolConfig request = new CrmLeadPoolConfig();
        request.setClaimDailyLimit(30);
        request.setProtectDays(7);
        request.setRecycleEnabled(true);
        request.setDefaultPoolOrgScope("dept");
        request.setQualifyTemplateId(SURVEY_ID);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.saveConfig(request, true));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
        assertEquals("模板不存在", ex.getMessage());
        verify(poolConfigMapper, never()).updateCrmLeadPoolConfig(any());
    }

    @Test
    void saveConfig_nonSurveyCategory_throws400()
    {
        CrmLeadPoolConfig existing = baseExisting();
        when(poolConfigMapper.selectFirst()).thenReturn(existing);

        SysReTemplate template = new SysReTemplate();
        template.setTemplateId(SURVEY_ID);
        template.setCategory("投资者问卷");
        when(templateService.selectSysReTemplateByTemplateId(SURVEY_ID)).thenReturn(template);

        CrmLeadPoolConfig request = new CrmLeadPoolConfig();
        request.setClaimDailyLimit(30);
        request.setProtectDays(7);
        request.setRecycleEnabled(true);
        request.setDefaultPoolOrgScope("dept");
        request.setQualifyTemplateId(SURVEY_ID);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.saveConfig(request, true));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
        assertEquals("须为 Survey 类模板", ex.getMessage());
        verify(poolConfigMapper, never()).updateCrmLeadPoolConfig(any());
    }

    @Test
    void saveConfig_nullClearsQualifyTemplateId()
    {
        CrmLeadPoolConfig existing = baseExisting();
        existing.setQualifyTemplateId(EXISTING_QUALIFY_ID);
        when(poolConfigMapper.selectFirst()).thenReturn(existing, existing);

        CrmLeadPoolConfig request = new CrmLeadPoolConfig();
        request.setClaimDailyLimit(30);
        request.setProtectDays(7);
        request.setRecycleEnabled(true);
        request.setDefaultPoolOrgScope("dept");
        request.setQualifyTemplateId(null);

        service.saveConfig(request, true);

        ArgumentCaptor<CrmLeadPoolConfig> captor = ArgumentCaptor.forClass(CrmLeadPoolConfig.class);
        verify(poolConfigMapper).updateCrmLeadPoolConfig(captor.capture());
        assertNull(captor.getValue().getQualifyTemplateId());
        verify(templateService, never()).selectSysReTemplateByTemplateId(any());
    }

    @Test
    void saveConfig_emptyStringClearsQualifyTemplateId()
    {
        CrmLeadPoolConfig existing = baseExisting();
        existing.setQualifyTemplateId(EXISTING_QUALIFY_ID);
        when(poolConfigMapper.selectFirst()).thenReturn(existing, existing);

        CrmLeadPoolConfig request = new CrmLeadPoolConfig();
        request.setClaimDailyLimit(30);
        request.setProtectDays(7);
        request.setRecycleEnabled(true);
        request.setDefaultPoolOrgScope("dept");
        request.setQualifyTemplateId("  ");

        service.saveConfig(request, true);

        ArgumentCaptor<CrmLeadPoolConfig> captor = ArgumentCaptor.forClass(CrmLeadPoolConfig.class);
        verify(poolConfigMapper).updateCrmLeadPoolConfig(captor.capture());
        assertNull(captor.getValue().getQualifyTemplateId());
    }

    @Test
    void saveConfig_insertWhenNoRow_withValidSurveyId()
    {
        when(poolConfigMapper.selectFirst()).thenReturn(null);

        SysReTemplate template = new SysReTemplate();
        template.setTemplateId(SURVEY_ID);
        template.setCategory("Survey");
        when(templateService.selectSysReTemplateByTemplateId(SURVEY_ID)).thenReturn(template);

        CrmLeadPoolConfig request = new CrmLeadPoolConfig();
        request.setClaimDailyLimit(30);
        request.setProtectDays(7);
        request.setRecycleEnabled(true);
        request.setDefaultPoolOrgScope("dept");
        request.setQualifyTemplateId(SURVEY_ID);

        service.saveConfig(request, true);

        ArgumentCaptor<CrmLeadPoolConfig> captor = ArgumentCaptor.forClass(CrmLeadPoolConfig.class);
        verify(poolConfigMapper).insertCrmLeadPoolConfig(captor.capture());
        assertEquals(SURVEY_ID, captor.getValue().getQualifyTemplateId());
        org.junit.jupiter.api.Assertions.assertNotNull(captor.getValue().getConfigId());
    }

    @Test
    void saveConfig_protectDaysOnly_writesHoursAsDaysTimes24()
    {
        when(poolConfigMapper.selectFirst()).thenReturn(null);

        CrmLeadPoolConfig request = new CrmLeadPoolConfig();
        request.setClaimDailyLimit(30);
        request.setProtectDays(7);
        request.setRecycleEnabled(true);
        request.setDefaultPoolOrgScope("dept");

        service.saveConfig(request, false);

        ArgumentCaptor<CrmLeadPoolConfig> captor = ArgumentCaptor.forClass(CrmLeadPoolConfig.class);
        verify(poolConfigMapper).insertCrmLeadPoolConfig(captor.capture());
        assertEquals(Integer.valueOf(168), captor.getValue().getIdleToPoolHours());
        assertEquals(Integer.valueOf(7), captor.getValue().getProtectDays());
        assertEquals(Integer.valueOf(100), captor.getValue().getMaxHoldCount());
    }

    @Test
    void assertHoldAllowed_exceedsLimit_throws429()
    {
        CrmLeadPoolConfig existing = baseExisting();
        existing.setMaxHoldCount(2);
        existing.setIdleToPoolHours(72);
        when(poolConfigMapper.selectFirst()).thenReturn(existing);
        when(crmLeadMapper.countAssignedLeadsByUserId(9L)).thenReturn(2);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.assertHoldAllowed(9L));
        assertEquals(Integer.valueOf(429), ex.getCode());
        assertEquals(LeadRecyclePolicyDefaults.REASON_HOLD_LIMIT, ex.getReason());
    }

    private static CrmLeadPoolConfig baseExisting()
    {
        CrmLeadPoolConfig existing = new CrmLeadPoolConfig();
        existing.setConfigId("cfg-1");
        existing.setClaimDailyLimit(30);
        existing.setProtectDays(7);
        existing.setRecycleEnabled(true);
        existing.setDefaultPoolOrgScope("dept");
        return existing;
    }
}
