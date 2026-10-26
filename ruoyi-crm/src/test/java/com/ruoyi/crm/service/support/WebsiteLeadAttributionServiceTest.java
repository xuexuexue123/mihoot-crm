package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOrg;
import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmOrgService;

@ExtendWith(MockitoExtension.class)
class WebsiteLeadAttributionServiceTest
{
    @Mock
    private ICrmLeadService crmLeadService;
    @Mock
    private ICrmOrgService crmOrgService;
    @Mock
    private ActivityEventProducer activityEventProducer;

    @InjectMocks
    private WebsiteLeadAttributionService service;

    @Test
    void submitNewLead_writesOrgIdAndStoreNameToLead()
    {
        WebsiteLeadSubmitRequest request = new WebsiteLeadSubmitRequest();
        request.setLeadName("张三");
        request.setMobilePhone("13800138000");
        request.setOrgId("org-1");
        request.setAppointmentDate("2026-07-08");
        request.setAppointmentTime("11:30-12:00");

        CrmOrg org = new CrmOrg();
        org.setOrgId("org-1");
        org.setFullName("Tenant3 南山店");
        when(crmOrgService.selectCrmOrgByOrgId("org-1")).thenReturn(org);
        when(crmLeadService.insertCrmLead(any(CrmLead.class))).thenReturn(1);

        service.submitNewLead(request);

        ArgumentCaptor<CrmLead> captor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadService).insertCrmLead(captor.capture());
        CrmLead lead = captor.getValue();
        assertEquals("org-1", lead.getOrgId());
        assertEquals("Tenant3 南山店", lead.getFullName());
        assertEquals(AudienceLeadVisibility.STATUS_OPEN, lead.getStatus());

        JSONArray customField = JSON.parseArray(lead.getCustomField());
        JSONObject appointmentDate = null;
        for (int i = 0; i < customField.size(); i++)
        {
            JSONObject item = customField.getJSONObject(i);
            if ("appointmentDate".equals(item.getString("fieldName")))
            {
                appointmentDate = item;
                break;
            }
        }
        assertNotNull(appointmentDate);
        assertEquals("2026-07-08", appointmentDate.getString("value"));
        assertEquals("", appointmentDate.getString("fieldLabel"));
    }
}
