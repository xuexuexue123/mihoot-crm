package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;

class WebsiteLeadValidationSupportTest
{
    private WebsiteLeadSubmitRequest validRequest()
    {
        WebsiteLeadSubmitRequest request = new WebsiteLeadSubmitRequest();
        request.setTenantSlug("demo-clinic");
        request.setLeadName("张三");
        request.setMobilePhone("+8613800138000");
        request.setOrgId("org-1");
        request.setAppointmentDate("2026-07-15");
        request.setAppointmentTime("11:30-12:00");
        request.setConsentAccepted(true);
        request.setCustomField("[{\"fieldName\":\"gender\",\"value\":\"male\"}]");
        return request;
    }

    @Test
    void validateSubmission_acceptsValidRequest()
    {
        WebsiteLeadValidationSupport.validateSubmission(validRequest());
    }

    @Test
    void validateSubmission_rejectsMissingConsent()
    {
        WebsiteLeadSubmitRequest request = validRequest();
        request.setConsentAccepted(false);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WebsiteLeadValidationSupport.validateSubmission(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void validateSubmission_rejectsMissingLeadName()
    {
        WebsiteLeadSubmitRequest request = validRequest();
        request.setLeadName(null);
        assertThrows(ServiceException.class,
                () -> WebsiteLeadValidationSupport.validateSubmission(request));
    }

    @Test
    void validateSubmission_rejectsMissingMobilePhone()
    {
        WebsiteLeadSubmitRequest request = validRequest();
        request.setMobilePhone("");
        assertThrows(ServiceException.class,
                () -> WebsiteLeadValidationSupport.validateSubmission(request));
    }

    @Test
    void validateSubmission_rejectsInvalidAppointmentDate()
    {
        WebsiteLeadSubmitRequest request = validRequest();
        request.setAppointmentDate("07/15/2026");
        assertThrows(ServiceException.class,
                () -> WebsiteLeadValidationSupport.validateSubmission(request));
    }

    @Test
    void validateSubmission_rejectsMalformedCustomField()
    {
        WebsiteLeadSubmitRequest request = validRequest();
        request.setCustomField("not-json");
        assertThrows(ServiceException.class,
                () -> WebsiteLeadValidationSupport.validateSubmission(request));
    }

    @Test
    void maskPhone_masksMiddleDigits()
    {
        String masked = WebsiteLeadValidationSupport.maskPhone("13800138000");
        assertTrue(masked.contains("****"));
    }
}
