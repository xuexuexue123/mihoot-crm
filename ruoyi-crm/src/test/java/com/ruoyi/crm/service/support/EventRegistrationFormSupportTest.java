package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.HmacUtil;

class EventRegistrationFormSupportTest
{
  @Test
  void validateSurveyJson_acceptsPagesArray()
  {
    EventRegistrationFormSupport.validateSurveyJson("{\"pages\":[]}");
  }

  @Test
  void validateSurveyJson_rejectsMissingPages()
  {
    assertThrows(ServiceException.class, () -> EventRegistrationFormSupport.validateSurveyJson("{\"title\":\"x\"}"));
  }

  @Test
  void validateSurveyJson_rejectsInvalidJson()
  {
    assertThrows(ServiceException.class, () -> EventRegistrationFormSupport.validateSurveyJson("{bad"));
  }

  @Test
  void extractRequiredFieldNames_includesNestedPanelFields()
  {
    String json = "{\"pages\":[{\"elements\":["
        + "{\"type\":\"panel\",\"elements\":["
        + "{\"name\":\"leadName\",\"isRequired\":true},"
        + "{\"name\":\"workEmail\",\"isRequired\":false}"
        + "]}]}]}";
    List<String> required = EventRegistrationFormSupport.extractRequiredFieldNames(json);
    assertEquals(Collections.singletonList("leadName"), required);
  }

  @Test
  void defaultFormJson_isValidAndHasRequiredFields()
  {
    String json = EventRegistrationFormSupport.DEFAULT_REGISTRATION_FORM_JSON;
    assertTrue(EventRegistrationFormSupport.isValidSurveyJson(json));
    List<String> required = EventRegistrationFormSupport.extractRequiredFieldNames(json);
    assertTrue(required.contains("leadName"));
    assertTrue(required.contains("mobilePhone"));
  }

  @Test
  void resolveFormJson_fallsBackToDefaultWhenBlank()
  {
    assertEquals(EventRegistrationFormSupport.DEFAULT_REGISTRATION_FORM_JSON,
        EventRegistrationFormSupport.resolveFormJson(null));
  }

  @Test
  void validateRegistrationFormPair_requiresBothOrNeither()
  {
    assertThrows(ServiceException.class, () -> EventRegistrationFormSupport
        .validateRegistrationFormPair("tpl-1", null));
    assertThrows(ServiceException.class, () -> EventRegistrationFormSupport
        .validateRegistrationFormPair(null, "{\"pages\":[]}"));
  }

  @Test
  void isSurveyCategory_matchesSurveyCaseInsensitive()
  {
    assertTrue(EventRegistrationFormSupport.isSurveyCategory("Survey"));
    assertTrue(EventRegistrationFormSupport.isSurveyCategory("survey"));
    assertFalse(EventRegistrationFormSupport.isSurveyCategory("问卷"));
  }

  @Test
  void generateEventRegistrationToken_matchesSurveyTokenOrder()
  {
    String eventId = "a814999a-e5f6-7890-abcd-ef999a8eff4";
    String tenantId = "tenant-1";
    assertEquals(HmacUtil.generateSurveyToken(eventId, tenantId),
        HmacUtil.generateEventRegistrationToken(eventId, tenantId));
  }
}
