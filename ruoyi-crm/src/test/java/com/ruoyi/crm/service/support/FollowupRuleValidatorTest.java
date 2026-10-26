package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.FollowupRuleSaveRequest;
import com.ruoyi.crm.service.followup.FollowupRuleConstants;

public class FollowupRuleValidatorTest
{
    @Test
    public void validRequestPasses()
    {
        FollowupRuleSaveRequest request = valid();
        FollowupRuleValidator.ValidatedFollowupRule validated =
                FollowupRuleValidator.validateSaveRequest(request, false);
        assertEquals(FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON, validated.triggerType);
    }

    @Test
    public void invalidTriggerFails()
    {
        FollowupRuleSaveRequest request = valid();
        request.setTriggerType("unknown");
        assertThrows(ServiceException.class, () -> FollowupRuleValidator.validateSaveRequest(request, false));
    }

    @Test
    public void untilFieldRequiresDelayField()
    {
        FollowupRuleSaveRequest request = valid();
        request.setDelayMode(FollowupRuleConstants.DELAY_UNTIL_FIELD);
        request.setDelayField(null);
        assertThrows(ServiceException.class, () -> FollowupRuleValidator.validateSaveRequest(request, false));
        request.setDelayField("patientValue.nextRecallDueAt");
        assertDoesNotThrow(() -> FollowupRuleValidator.validateSaveRequest(request, false));
    }

    @Test
    public void systemDeleteNotValidatedHere()
    {
        FollowupRuleSaveRequest request = valid();
        request.setTaskTemplate("not-json");
        assertThrows(ServiceException.class, () -> FollowupRuleValidator.validateSaveRequest(request, false));
    }

    private static FollowupRuleSaveRequest valid()
    {
        FollowupRuleSaveRequest request = new FollowupRuleSaveRequest();
        request.setName("赢单后回访");
        request.setEnabled("1");
        request.setTriggerType(FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON);
        request.setDelayMode(FollowupRuleConstants.DELAY_BUSINESS_DAYS);
        request.setDelayValue(2);
        request.setTaskTemplate("{\"taskSubject\":\"回访\"}");
        request.setDedupeConfig("{\"strategy\":\"once_per_record\"}");
        return request;
    }
}
