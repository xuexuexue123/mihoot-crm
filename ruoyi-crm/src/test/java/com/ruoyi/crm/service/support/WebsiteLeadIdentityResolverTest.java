package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WebsiteLeadIdentityResolverTest
{
    private static final String TENANT_KEY = "tenant_demo";
    private static final String PHONE = "13800138000";
    private static final String CONTACT_ID = "contact-1";

    @Mock
    private ICrmCustomerIdentityService identityService;
    @Mock
    private ICrmLeadConvertConfigService leadConvertConfigService;
    @Mock
    private CrmLeadMapper crmLeadMapper;

    @InjectMocks
    private WebsiteLeadIdentityResolver resolver;

    private void stubMergeEnabled()
    {
        LeadConvertConfigVo config = LeadConvertConfigVo.defaults();
        when(leadConvertConfigService.getConfig(TENANT_KEY)).thenReturn(config);
    }

    @Test
    void resolve_contactKnown_whenPhoneMatchesContact()
    {
        stubMergeEnabled();
        when(identityService.findContactByIdentity(eq("mobile_phone"), eq(PHONE))).thenReturn(CONTACT_ID);

        OutreachRegistrationIdentityResolution result = resolver.resolve(TENANT_KEY, PHONE);

        assertTrue(result.isContactKnown());
        assertEquals(CONTACT_ID, result.getContactId());
    }

    @Test
    void resolve_leadUpsert_whenAssignedLeadExists()
    {
        stubMergeEnabled();
        when(identityService.findContactByIdentity(eq("mobile_phone"), eq(PHONE))).thenReturn(null);
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-1");
        existing.setStatus("Assigned");
        when(crmLeadMapper.selectInitialByMobilePhone(PHONE)).thenReturn(existing);

        OutreachRegistrationIdentityResolution result = resolver.resolve(TENANT_KEY, PHONE);

        assertEquals(OutreachRegistrationIdentityResolution.Kind.LEAD_UPSERT, result.getKind());
        assertEquals("lead-1", result.getExistingLead().getLeadId());
    }

    @Test
    void resolve_createNew_whenNoMatch()
    {
        stubMergeEnabled();
        when(identityService.findContactByIdentity(eq("mobile_phone"), eq(PHONE))).thenReturn(null);
        when(crmLeadMapper.selectInitialByMobilePhone(PHONE)).thenReturn(null);
        when(crmLeadMapper.selectLatestByMobilePhone(PHONE)).thenReturn(null);

        OutreachRegistrationIdentityResolution result = resolver.resolve(TENANT_KEY, PHONE);

        assertEquals(OutreachRegistrationIdentityResolution.Kind.CREATE_NEW_LEAD, result.getKind());
    }

    @Test
    void resolve_forbidden_whenNonInitialLeadExists()
    {
        stubMergeEnabled();
        when(identityService.findContactByIdentity(eq("mobile_phone"), eq(PHONE))).thenReturn(null);
        when(crmLeadMapper.selectInitialByMobilePhone(PHONE)).thenReturn(null);
        CrmLead existing = new CrmLead();
        existing.setLeadId("lead-2");
        existing.setStatus("Converted");
        when(crmLeadMapper.selectLatestByMobilePhone(PHONE)).thenReturn(existing);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> resolver.resolve(TENANT_KEY, PHONE));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
    }
}
