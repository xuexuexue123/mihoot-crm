package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadDedupeMatch;
import com.ruoyi.crm.domain.vo.LeadRecycleDedupeConfigVo;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadRecycleConfigService;

@ExtendWith(MockitoExtension.class)
class LeadDedupeServiceTest
{
    @Mock
    private ICrmLeadRecycleConfigService recycleConfigService;
    @Mock
    private CrmLeadMapper crmLeadMapper;
    @Mock
    private CrmContactMapper crmContactMapper;
    @Mock
    private ICrmCustomerIdentityService identityService;

    @InjectMocks
    private LeadDedupeService service;

    @Test
    void findDuplicateLead_returnsMatch()
    {
        LeadRecycleDedupeConfigVo cfg = new LeadRecycleDedupeConfigVo();
        cfg.setLeadMatchFields(Arrays.asList("mobilePhone", "wecomExternalUserid"));
        when(recycleConfigService.getDedupeConfig()).thenReturn(cfg);

        CrmLead hit = new CrmLead();
        hit.setLeadId("existing");
        hit.setMobilePhone("13800138000");
        when(crmLeadMapper.selectUnconvertedDuplicate(any())).thenReturn(hit);

        CrmLead candidate = new CrmLead();
        candidate.setMobilePhone("+86 138-0013-8000");
        LeadDedupeMatch match = service.findDuplicateLead(candidate);
        assertNotNull(match);
        assertEquals("existing", match.getId());
        assertEquals("mobilePhone", match.getMatchedField());
    }

    @Test
    void findDuplicateLead_emptyFieldsSkipped()
    {
        LeadRecycleDedupeConfigVo cfg = new LeadRecycleDedupeConfigVo();
        cfg.setLeadMatchFields(Arrays.asList("mobilePhone"));
        when(recycleConfigService.getDedupeConfig()).thenReturn(cfg);

        CrmLead candidate = new CrmLead();
        assertNull(service.findDuplicateLead(candidate));
    }

    @Test
    void findDuplicateContact_identityFirst()
    {
        LeadRecycleDedupeConfigVo cfg = new LeadRecycleDedupeConfigVo();
        cfg.setContactMatchFields(Arrays.asList("wecomExternalUserid", "mobilePhone"));
        when(recycleConfigService.getDedupeConfig()).thenReturn(cfg);
        when(identityService.findContactByIdentity("wecom_external_userid", "wm1")).thenReturn("C-9");

        CrmLead lead = new CrmLead();
        lead.setWecomExternalUserid("wm1");
        LeadDedupeMatch match = service.findDuplicateContact(lead, null);
        assertEquals("C-9", match.getId());
        assertEquals("wecomExternalUserid", match.getMatchedField());
    }
}
