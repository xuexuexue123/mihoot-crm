package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmLeadDedupeConfig;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.domain.dto.LeadConvertConfigUpsertDto;
import com.ruoyi.crm.domain.dto.LeadRecycleConfigUpsertDto;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecycleConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecycleConvertConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecycleDedupeConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecyclePoolConfigVo;
import com.ruoyi.crm.mapper.CrmLeadDedupeConfigMapper;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;
import com.ruoyi.crm.service.ICrmLeadPoolConfigService;

@ExtendWith(MockitoExtension.class)
class CrmLeadRecycleConfigServiceImplTest
{
    @Mock
    private ICrmLeadPoolConfigService poolConfigService;
    @Mock
    private ICrmLeadConvertConfigService convertConfigService;
    @Mock
    private CrmLeadDedupeConfigMapper dedupeConfigMapper;
    @Mock
    private IAssignableUserService assignableUserService;

    @InjectMocks
    private CrmLeadRecycleConfigServiceImpl service;

    @Test
    void getConfig_returnsSeedDefaultsWhenEmpty()
    {
        CrmLeadPoolConfig pool = new CrmLeadPoolConfig();
        pool.setIdleToPoolHours(72);
        pool.setMaxHoldCount(100);
        pool.setRecycleEnabled(true);
        when(poolConfigService.getConfig()).thenReturn(pool);
        when(dedupeConfigMapper.selectFirst()).thenReturn(null);
        when(convertConfigService.getConfig(isNull())).thenReturn(LeadConvertConfigVo.defaults());

        LeadRecycleConfigVo vo = service.getConfig();

        assertEquals(Integer.valueOf(72), vo.getPool().getIdleToPoolHours());
        assertEquals(Integer.valueOf(100), vo.getPool().getMaxHoldCount());
        assertTrue(vo.getPool().getRecycleEnabled());
        assertEquals(Arrays.asList("mobilePhone", "wecomExternalUserid"), vo.getDedupe().getLeadMatchFields());
        assertEquals("keep_lead_owner", vo.getConvert().getContactOwnerMode());
        assertNull(vo.getConvert().getContactOwnerUserId());
    }

    @Test
    void saveConfig_specifiedUserWithoutId_throws400()
    {
        LeadRecycleConfigUpsertDto dto = validDto();
        dto.getConvert().setContactOwnerMode("specified_user");
        dto.getConvert().setContactOwnerUserId(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.saveConfig(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void saveConfig_persistsHoursAndDedupe()
    {
        CrmLeadPoolConfig existingPool = new CrmLeadPoolConfig();
        existingPool.setClaimDailyLimit(30);
        existingPool.setQualifyTemplateId("tpl");
        existingPool.setDefaultPoolOrgScope("dept");
        when(poolConfigService.getConfig()).thenReturn(existingPool, existingPool);
        when(dedupeConfigMapper.selectFirst()).thenReturn(null);
        when(convertConfigService.getConfig(isNull())).thenReturn(LeadConvertConfigVo.defaults());

        LeadRecycleConfigUpsertDto dto = validDto();
        dto.getPool().setIdleToPoolHours(48);
        dto.getConvert().setContactOwnerMode("keep_lead_owner");

        service.saveConfig(dto);

        ArgumentCaptor<CrmLeadPoolConfig> poolCaptor = ArgumentCaptor.forClass(CrmLeadPoolConfig.class);
        verify(poolConfigService).saveConfig(poolCaptor.capture(), eq(false));
        assertEquals(Integer.valueOf(48), poolCaptor.getValue().getIdleToPoolHours());
        assertEquals(Integer.valueOf(30), poolCaptor.getValue().getClaimDailyLimit());

        ArgumentCaptor<CrmLeadDedupeConfig> dedupeCaptor = ArgumentCaptor.forClass(CrmLeadDedupeConfig.class);
        verify(dedupeConfigMapper).insertCrmLeadDedupeConfig(dedupeCaptor.capture());
        assertTrue(dedupeCaptor.getValue().getLeadMatchFields().contains("mobilePhone"));

        ArgumentCaptor<LeadConvertConfigUpsertDto> convertCaptor =
                ArgumentCaptor.forClass(LeadConvertConfigUpsertDto.class);
        verify(convertConfigService).upsert(isNull(), convertCaptor.capture());
        assertEquals("keep_lead_owner", convertCaptor.getValue().getContactOwnerMode());
    }

    @Test
    void saveConfig_emptyLeadFields_throws400()
    {
        LeadRecycleConfigUpsertDto dto = validDto();
        dto.getDedupe().setLeadMatchFields(Collections.emptyList());
        ServiceException ex = assertThrows(ServiceException.class, () -> service.saveConfig(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    private static LeadRecycleConfigUpsertDto validDto()
    {
        LeadRecycleConfigUpsertDto dto = new LeadRecycleConfigUpsertDto();
        LeadRecyclePoolConfigVo pool = new LeadRecyclePoolConfigVo();
        pool.setIdleToPoolHours(72);
        pool.setMaxHoldCount(100);
        pool.setRecycleEnabled(true);
        dto.setPool(pool);
        LeadRecycleDedupeConfigVo dedupe = new LeadRecycleDedupeConfigVo();
        dedupe.setLeadMatchFields(Arrays.asList("mobilePhone", "wecomExternalUserid"));
        dedupe.setContactMatchFields(Arrays.asList("mobilePhone", "wecomExternalUserid"));
        dedupe.setMatchMode("any");
        dto.setDedupe(dedupe);
        LeadRecycleConvertConfigVo convert = new LeadRecycleConvertConfigVo();
        convert.setContactOwnerMode("keep_lead_owner");
        dto.setConvert(convert);
        return dto;
    }
}
