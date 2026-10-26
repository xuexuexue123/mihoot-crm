package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.support.CaptureSurveyDataMapper.MappedLeadFields;

@ExtendWith(MockitoExtension.class)
class CaptureLeadAttributionServiceTest
{
    @Mock
    private ICrmLeadService crmLeadService;
    @Mock
    private ActivityEventProducer activityEventProducer;

    @InjectMocks
    private CaptureLeadAttributionService service;

    @Test
    void submitNewLead_setsCaptureDefaults()
    {
        MappedLeadFields fields = new MappedLeadFields("张三", "13800138000", "a@b.com", "Acme", null);
        when(crmLeadService.insertCrmLead(any(CrmLead.class))).thenReturn(1);

        service.submitNewLead(fields, "[{\"fieldName\":\"captureId\",\"value\":\"cap-1\"}]", "/p");

        ArgumentCaptor<CrmLead> captor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadService).insertCrmLead(captor.capture());
        CrmLead lead = captor.getValue();
        assertEquals("Capture", lead.getLeadSource());
        assertEquals("business", lead.getLeadType());
        assertEquals("Initial", lead.getStatus());
        assertEquals(LeadDefaults.DEFAULT_STAGE, lead.getStage());
        assertEquals("System", lead.getOwner());
        assertEquals("System", lead.getCreateBy());
        assertEquals("张三", lead.getLeadName());
        assertEquals("Acme", lead.getFullName());
        verify(activityEventProducer).produceWebsiteLeadCreated(any(), eq("张三"), eq("Capture"), eq("/p"));
    }
}
