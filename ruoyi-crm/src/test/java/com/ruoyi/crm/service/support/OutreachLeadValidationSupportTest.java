package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmOutreachlead;

class OutreachLeadValidationSupportTest
{
  @Test
  void validateEventAcceptsRegistration_rejectsCancelledStatus()
  {
    CrmEvent event = new CrmEvent();
    event.setEventId("evt-1");
    event.setStatus("Cancelled");
    ServiceException ex = assertThrows(ServiceException.class,
        () -> OutreachLeadValidationSupport.validateEventAcceptsRegistration(event));
    assertEquals(HttpStatus.FORBIDDEN, ex.getCode());
  }

  @Test
  void validateOutreachLeadSubmission_requiresLeadNameOrMobilePhone()
  {
    CrmOutreachlead lead = new CrmOutreachlead();
    assertThrows(ServiceException.class,
        () -> OutreachLeadValidationSupport.validateOutreachLeadSubmission(lead, null));
  }

  @Test
  void validateOutreachLeadSubmission_requiresEventWhenSidPresent()
  {
    CrmOutreachlead lead = new CrmOutreachlead();
    lead.setLeadName("Alice");
    lead.setSid("evt-1");
    assertThrows(ServiceException.class,
        () -> OutreachLeadValidationSupport.validateOutreachLeadSubmission(lead, null));
  }

  @Test
  void validateOutreachLeadSubmission_checksRequiredFieldsFromDefaultForm()
  {
    CrmOutreachlead lead = new CrmOutreachlead();
    lead.setSid("evt-1");
    lead.setLeadName("Alice");

    CrmEvent event = new CrmEvent();
    event.setEventId("evt-1");

    assertThrows(ServiceException.class,
        () -> OutreachLeadValidationSupport.validateOutreachLeadSubmission(lead, event));

    lead.setMobilePhone("13800000000");
    OutreachLeadValidationSupport.validateOutreachLeadSubmission(lead, event);
  }

  @Test
  void hasFieldValue_readsCustomFieldArray()
  {
    CrmOutreachlead lead = new CrmOutreachlead();
    lead.setCustomField("[{\"fieldName\":\"budget\",\"value\":\"Yes\"}]");
    assertTrue(OutreachLeadValidationSupport.hasFieldValue(lead, "budget"));
  }
}
