package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.MiniappLeadUpsertRequest;
import com.ruoyi.crm.domain.vo.MiniappLeadUpsertResultVo;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadService;

@ExtendWith(MockitoExtension.class)
class CrmMiniappLeadServiceImplTest
{
    @Mock
    private CrmLeadMapper crmLeadMapper;

    @Mock
    private ICrmLeadService crmLeadService;

    @Mock
    private com.ruoyi.crm.event.ActivityEventProducer activityEventProducer;

    @Mock
    private ICrmCustomerIdentityService identityService;

    @InjectMocks
    private CrmMiniappLeadServiceImpl service;

    @Test
    void upsert_createsThenUpdatesSameLead()
    {
        when(identityService.findContactByIdentity("mobile_phone", "13800138000")).thenReturn(null);
        when(crmLeadMapper.selectActiveUnconvertedByMobilePhone("13800138000"))
                .thenReturn(null)
                .thenAnswer(inv -> {
                    CrmLead stored = new CrmLead();
                    stored.setLeadId("lead-1");
                    stored.setMobilePhone("13800138000");
                    stored.setLeadName("用户8000");
                    stored.setStatus("Open");
                    stored.setLeadSource("wechat_miniapp");
                    return stored;
                });
        when(crmLeadService.insertCrmLead(any(CrmLead.class))).thenAnswer(inv -> {
            CrmLead lead = inv.getArgument(0);
            lead.setLeadId("lead-1");
            return 1;
        });
        when(crmLeadService.updateCrmLead(any(CrmLead.class))).thenReturn(1);

        MiniappLeadUpsertRequest createReq = new MiniappLeadUpsertRequest();
        createReq.setMobilePhone("13800138000");
        createReq.setLeadName("用户8000");
        createReq.setRemark("首次预约");

        MiniappLeadUpsertResultVo created = service.upsert(createReq);
        assertEquals(CrmMiniappLeadServiceImpl.RESOLUTION_CREATED, created.getResolution());
        assertEquals("lead-1", created.getLeadId());
        assertNull(created.getContactId());

        MiniappLeadUpsertRequest updateReq = new MiniappLeadUpsertRequest();
        updateReq.setMobilePhone("138-0013-8000");
        updateReq.setRemark("二次预约");
        Map<String, Object> profile = new HashMap<>();
        profile.put("industry", "制造");
        updateReq.setProfileJson(profile);

        MiniappLeadUpsertResultVo updated = service.upsert(updateReq);
        assertEquals(CrmMiniappLeadServiceImpl.RESOLUTION_UPDATED, updated.getResolution());
        assertEquals("lead-1", updated.getLeadId());

        verify(crmLeadService, times(1)).insertCrmLead(any(CrmLead.class));
        verify(crmLeadService, times(1)).updateCrmLead(any(CrmLead.class));

        ArgumentCaptor<CrmLead> updateCaptor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadService).updateCrmLead(updateCaptor.capture());
        assertNotNull(updateCaptor.getValue().getCustomField());
        assertEquals(true, updateCaptor.getValue().getDescription().contains("二次预约"));
    }

    @Test
    void upsert_linksExistingContact()
    {
        when(identityService.findContactByIdentity("mobile_phone", "13800138000")).thenReturn("C-1");
        when(crmLeadMapper.selectActiveUnconvertedByMobilePhone("13800138000")).thenReturn(null);
        when(crmLeadService.insertCrmLead(any(CrmLead.class))).thenReturn(1);

        MiniappLeadUpsertRequest req = new MiniappLeadUpsertRequest();
        req.setMobilePhone("13800138000");
        req.setLeadName("已有联系人");

        MiniappLeadUpsertResultVo result = service.upsert(req);
        assertEquals(CrmMiniappLeadServiceImpl.RESOLUTION_LINKED_CONTACT, result.getResolution());
        assertEquals("C-1", result.getContactId());

        ArgumentCaptor<CrmLead> captor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadService).insertCrmLead(captor.capture());
        assertEquals("C-1", captor.getValue().getContactId());
        assertEquals("wechat_miniapp", captor.getValue().getLeadSource());
        verify(crmLeadService, never()).updateCrmLead(any());
    }

    @Test
    void upsert_createsWithMiniappAppointmentSource()
    {
        when(identityService.findContactByIdentity("mobile_phone", "13800138001")).thenReturn(null);
        when(crmLeadMapper.selectActiveUnconvertedByMobilePhone("13800138001")).thenReturn(null);
        when(crmLeadService.insertCrmLead(any(CrmLead.class))).thenReturn(1);

        MiniappLeadUpsertRequest req = new MiniappLeadUpsertRequest();
        req.setMobilePhone("13800138001");
        req.setLeadName("王总");
        req.setSourceChannel(CrmMiniappLeadServiceImpl.SOURCE_MINIAPP_APPOINTMENT);
        Map<String, Object> profile = new HashMap<>();
        profile.put("topic_label", "薪资用工");
        profile.put("timeslot_label", "工作日白天");
        req.setProfileJson(profile);

        MiniappLeadUpsertResultVo result = service.upsert(req);
        assertEquals(CrmMiniappLeadServiceImpl.RESOLUTION_CREATED, result.getResolution());

        ArgumentCaptor<CrmLead> captor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadService).insertCrmLead(captor.capture());
        assertEquals(CrmMiniappLeadServiceImpl.SOURCE_MINIAPP_APPOINTMENT, captor.getValue().getLeadSource());
        assertTrue(captor.getValue().getCustomField().contains("profile.topic_label"));
        assertTrue(captor.getValue().getCustomField().contains("薪资用工"));
    }

    @Test
    void upsert_upgradesWechatMiniappSourceToAppointment()
    {
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-apt");
        existing.setMobilePhone("13800138002");
        existing.setLeadName("用户8002");
        existing.setStatus("Open");
        existing.setLeadSource(CrmMiniappLeadServiceImpl.DEFAULT_SOURCE);

        when(identityService.findContactByIdentity("mobile_phone", "13800138002")).thenReturn(null);
        when(crmLeadMapper.selectActiveUnconvertedByMobilePhone("13800138002")).thenReturn(existing);
        when(crmLeadService.updateCrmLead(any(CrmLead.class))).thenReturn(1);

        MiniappLeadUpsertRequest req = new MiniappLeadUpsertRequest();
        req.setMobilePhone("13800138002");
        req.setSourceChannel(CrmMiniappLeadServiceImpl.SOURCE_MINIAPP_APPOINTMENT);
        Map<String, Object> profile = new HashMap<>();
        profile.put("wechat", "wang_vn2026");
        profile.put("note", "想请顾问复核");
        req.setProfileJson(profile);

        MiniappLeadUpsertResultVo result = service.upsert(req);
        assertEquals(CrmMiniappLeadServiceImpl.RESOLUTION_UPDATED, result.getResolution());

        ArgumentCaptor<CrmLead> captor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadService).updateCrmLead(captor.capture());
        assertEquals(CrmMiniappLeadServiceImpl.SOURCE_MINIAPP_APPOINTMENT, captor.getValue().getLeadSource());
        assertTrue(captor.getValue().getCustomField().contains("profile.wechat"));
        assertTrue(captor.getValue().getCustomField().contains("wang_vn2026"));
    }

    @Test
    void upsert_doesNotOverwriteNonMiniappSource()
    {
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-capture");
        existing.setMobilePhone("13800138003");
        existing.setLeadName("采集线索");
        existing.setStatus("Open");
        existing.setLeadSource("Capture");

        when(identityService.findContactByIdentity("mobile_phone", "13800138003")).thenReturn(null);
        when(crmLeadMapper.selectActiveUnconvertedByMobilePhone("13800138003")).thenReturn(existing);
        when(crmLeadService.updateCrmLead(any(CrmLead.class))).thenReturn(1);

        MiniappLeadUpsertRequest req = new MiniappLeadUpsertRequest();
        req.setMobilePhone("13800138003");
        req.setSourceChannel(CrmMiniappLeadServiceImpl.SOURCE_MINIAPP_APPOINTMENT);
        req.setRemark("小程序预约备注");

        service.upsert(req);

        ArgumentCaptor<CrmLead> captor = ArgumentCaptor.forClass(CrmLead.class);
        verify(crmLeadService).updateCrmLead(captor.capture());
        assertEquals("Capture", captor.getValue().getLeadSource());
        assertTrue(captor.getValue().getDescription().contains("小程序预约备注"));
    }

    @Test
    void upsertRequest_acceptsLeadSourceJsonAlias() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        MiniappLeadUpsertRequest req = mapper.readValue(
                "{\"mobilePhone\":\"13800138004\",\"leadSource\":\"miniapp_appointment\"}",
                MiniappLeadUpsertRequest.class);
        assertEquals(CrmMiniappLeadServiceImpl.SOURCE_MINIAPP_APPOINTMENT, req.getSourceChannel());
    }

    @Test
    void findByMobile_returnsNullWhenMissing()
    {
        when(crmLeadMapper.selectActiveUnconvertedByMobilePhone("13800138000")).thenReturn(null);
        assertNull(service.findByMobile("13800138000"));
    }

    @Test
    void findByMobile_returnsSummary()
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId("L1");
        lead.setMobilePhone("13800138000");
        lead.setLeadName("张三");
        lead.setStage("线索获取");
        lead.setStatus("Open");
        lead.setLeadSource("wechat_miniapp");
        when(crmLeadMapper.selectActiveUnconvertedByMobilePhone("13800138000")).thenReturn(lead);
        when(identityService.findContactByIdentity("mobile_phone", "13800138000")).thenReturn(null);

        assertEquals("L1", service.findByMobile("13800138000").getLeadId());
        assertEquals("张三", service.findByMobile("13800138000").getLeadName());
    }
}
