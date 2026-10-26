package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.support.OutreachEventLeadResolveService;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;

@ExtendWith(MockitoExtension.class)
class WecomEventLeadResolveServiceTest
{
    private static final String EVENT_ID = "evt-1";
    private static final String UNION_ID = "union-abc";
    private static final String OPEN_ID = "o-open-1";
    private static final String APP_ID = "wx-app-1";

    @Mock
    private CrmLeadMapper crmLeadMapper;

    @Mock
    private CrmWechatMpFanMapper fanMapper;

    @Mock
    private OutreachEventLeadResolveService outreachEventLeadResolveService;

    @InjectMocks
    private WecomEventLeadResolveService service;

    @Test
    void resolveInitialLeadForWecomAdd_findsLeadViaUnionIdAndFan()
    {
        CrmWechatMpFan fan = new CrmWechatMpFan();
        fan.setUnionId(UNION_ID);
        fan.setOpenId(OPEN_ID);
        fan.setAppId(APP_ID);
        when(fanMapper.selectByUnionId(UNION_ID)).thenReturn(Collections.singletonList(fan));

        CrmLead lead = initialLead("lead-1");
        when(outreachEventLeadResolveService.resolveLeadForEvent(EVENT_ID, OPEN_ID, APP_ID)).thenReturn(lead);

        Optional<CrmLead> result = service.resolveInitialLeadForWecomAdd(
                EVENT_ID, UNION_ID, "wm_new", null, null);

        assertTrue(result.isPresent());
        assertEquals("lead-1", result.get().getLeadId());
    }

    @Test
    void resolveInitialLeadForWecomAdd_findsLeadByMobile()
    {
        CrmLead lead = initialLead("lead-phone");
        when(crmLeadMapper.selectBySidAndMobilePhone(EVENT_ID, "13800138000")).thenReturn(lead);

        Optional<CrmLead> result = service.resolveInitialLeadForWecomAdd(
                EVENT_ID, null, "wm_new", "13800138000", null);

        assertTrue(result.isPresent());
        assertEquals("lead-phone", result.get().getLeadId());
    }

    @Test
    void resolveInitialLeadForWecomAdd_skipsConvertedLead()
    {
        CrmLead converted = new CrmLead();
        converted.setLeadId("lead-converted");
        converted.setStatus("Converted");
        when(crmLeadMapper.selectByWecomExternalUserid("wm_existing")).thenReturn(converted);

        Optional<CrmLead> result = service.resolveInitialLeadForWecomAdd(
                EVENT_ID, null, "wm_existing", null, null);

        assertTrue(!result.isPresent());
    }

    @Test
    void resolveInitialLeadForWecomAdd_prefersSameEventSid()
    {
        CrmWechatMpFan fan = new CrmWechatMpFan();
        fan.setUnionId(UNION_ID);
        fan.setOpenId(OPEN_ID);
        fan.setAppId(APP_ID);
        when(fanMapper.selectByUnionId(UNION_ID)).thenReturn(Collections.singletonList(fan));

        CrmLead otherEvent = initialLead("lead-other");
        otherEvent.setSid("other-event");
        otherEvent.setCreateTime(new Date(1_000L));

        CrmLead sameEvent = initialLead("lead-same");
        sameEvent.setSid(EVENT_ID);
        sameEvent.setCreateTime(new Date(2_000L));

        when(outreachEventLeadResolveService.resolveLeadForEvent(eq(EVENT_ID), eq(OPEN_ID), eq(APP_ID)))
                .thenReturn(sameEvent);

        Optional<CrmLead> result = service.resolveInitialLeadForWecomAdd(
                EVENT_ID, UNION_ID, "wm_new", null, null);

        assertTrue(result.isPresent());
        assertEquals("lead-same", result.get().getLeadId());
    }

    private static CrmLead initialLead(String leadId)
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId(leadId);
        lead.setStatus("Initial");
        lead.setSid(EVENT_ID);
        lead.setCreateTime(new Date());
        return lead;
    }
}
