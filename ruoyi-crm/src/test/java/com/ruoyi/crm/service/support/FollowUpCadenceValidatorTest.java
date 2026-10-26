package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.FollowUpCadenceSaveRequest;

public class FollowUpCadenceValidatorTest
{
    @Test
    public void validSaveRequestPasses()
    {
        FollowUpCadenceSaveRequest request = buildValidRequest();
        assertDoesNotThrow(() -> FollowUpCadenceValidator.validateSaveRequest(request, false));
    }

    @Test
    public void missingNameFails()
    {
        FollowUpCadenceSaveRequest request = buildValidRequest();
        request.setName("");
        assertThrows(ServiceException.class, () -> FollowUpCadenceValidator.validateSaveRequest(request, false));
    }

    @Test
    public void invalidEnabledFails()
    {
        FollowUpCadenceSaveRequest request = buildValidRequest();
        request.setEnabled("2");
        assertThrows(ServiceException.class, () -> FollowUpCadenceValidator.validateSaveRequest(request, false));
    }

    @Test
    public void invalidStepsJsonFails()
    {
        FollowUpCadenceSaveRequest request = buildValidRequest();
        request.setStepsJson("{\"id\":\"s1\"}");
        assertThrows(ServiceException.class, () -> FollowUpCadenceValidator.validateSaveRequest(request, false));
    }

    @Test
    public void defaultStartTriggerApplied()
    {
        FollowUpCadenceSaveRequest request = buildValidRequest();
        request.setStartTrigger(null);
        FollowUpCadenceValidator.ValidatedFollowUpCadence validated =
                FollowUpCadenceValidator.validateSaveRequest(request, false);
        assertEquals("Initial Email Sent", validated.startTrigger);
    }

    @Test
    public void updateRequiresCadenceId()
    {
        FollowUpCadenceSaveRequest request = buildValidRequest();
        assertThrows(ServiceException.class, () -> FollowUpCadenceValidator.validateSaveRequest(request, true));
    }

    private static FollowUpCadenceSaveRequest buildValidRequest()
    {
        FollowUpCadenceSaveRequest request = new FollowUpCadenceSaveRequest();
        request.setName("No Response Follow-up");
        request.setEnabled("1");
        request.setStartTrigger("Initial Email Sent");
        request.setStepsJson("[{\"id\":\"s1\",\"order\":0,\"waitDays\":2,\"actionType\":\"REMINDER\",\"title\":\"Reminder\",\"body\":\"Check if opened\"}]");
        return request;
    }
}
