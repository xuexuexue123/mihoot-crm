package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 活动 H5 报名表单 SurveyJS JSON 校验与解析
 */
public final class EventRegistrationFormSupport
{
  public static final String SURVEY_CATEGORY = "Survey";

  public static final String DEFAULT_REGISTRATION_FORM_JSON = "{"
      + "\"title\":\"Registration\","
      + "\"description\":\"This form will submit business data for registration. If you have special requirements, please indicate them in the requirements description below.\","
      + "\"pages\":[{\"name\":\"page1\",\"elements\":[{\"type\":\"panel\",\"name\":\"issue-report\",\"elements\":["
      + "{\"type\":\"text\",\"name\":\"leadName\",\"title\":\"Name\",\"isRequired\":true},"
      + "{\"type\":\"text\",\"name\":\"mobilePhone\",\"title\":\"Mobile\",\"isRequired\":true},"
      + "{\"type\":\"text\",\"name\":\"fullName\",\"title\":\"Organization\"},"
      + "{\"type\":\"text\",\"name\":\"workEmail\",\"title\":\"Email\"},"
      + "{\"type\":\"comment\",\"name\":\"shortdesc\",\"title\":\"Comment\",\"maxLength\":512,\"rows\":6,\"isRequired\":false}"
      + "]}]}],"
      + "\"showQuestionNumbers\":\"off\","
      + "\"completeText\":\"Submit\","
      + "\"widthMode\":\"static\","
      + "\"width\":\"720\""
      + "}";

  private static final Set<String> KNOWN_LEAD_FIELD_NAMES;

  static
  {
    Set<String> names = new HashSet<>();
    names.add("leadName");
    names.add("mobilePhone");
    names.add("fullName");
    names.add("workEmail");
    names.add("shortdesc");
    names.add("shortDescription");
    names.add("description");
    names.add("workPhone");
    names.add("industry");
    names.add("website");
    KNOWN_LEAD_FIELD_NAMES = Collections.unmodifiableSet(names);
  }

  private EventRegistrationFormSupport()
  {
  }

  public static boolean isSurveyCategory(String category)
  {
    return SURVEY_CATEGORY.equalsIgnoreCase(StringUtils.trim(category));
  }

  public static JSONObject parseSurveyJson(String json)
  {
    if (StringUtils.isBlank(json))
    {
      return null;
    }
    try
    {
      return JSON.parseObject(json);
    }
    catch (Exception ex)
    {
      throw new ServiceException("registrationFormJson 不是合法 JSON", HttpStatus.BAD_REQUEST);
    }
  }

  public static void validateSurveyJson(String json)
  {
    JSONObject root = parseSurveyJson(json);
    if (root == null)
    {
      throw new ServiceException("registrationFormJson 不能为空", HttpStatus.BAD_REQUEST);
    }
    if (!root.containsKey("pages") || !(root.get("pages") instanceof JSONArray))
    {
      throw new ServiceException("registrationFormJson 根对象须包含 pages 数组", HttpStatus.BAD_REQUEST);
    }
  }

  public static boolean isValidSurveyJson(String json)
  {
    if (StringUtils.isBlank(json))
    {
      return false;
    }
    try
    {
      validateSurveyJson(json);
      return true;
    }
    catch (ServiceException ex)
    {
      return false;
    }
  }

  public static String resolveFormJson(String registrationFormJson)
  {
    if (StringUtils.isNotBlank(registrationFormJson) && isValidSurveyJson(registrationFormJson))
    {
      return registrationFormJson;
    }
    return DEFAULT_REGISTRATION_FORM_JSON;
  }

  public static List<String> extractRequiredFieldNames(String json)
  {
    JSONObject root = parseSurveyJson(json);
    if (root == null)
    {
      return Collections.emptyList();
    }
    JSONArray pages = root.getJSONArray("pages");
    if (pages == null)
    {
      return Collections.emptyList();
    }
    List<String> required = new ArrayList<>();
    for (int i = 0; i < pages.size(); i++)
    {
      JSONObject page = pages.getJSONObject(i);
      if (page != null)
      {
        collectRequiredFromElements(page.getJSONArray("elements"), required);
      }
    }
    return required;
  }

  private static void collectRequiredFromElements(JSONArray elements, List<String> required)
  {
    if (elements == null)
    {
      return;
    }
    for (int i = 0; i < elements.size(); i++)
    {
      JSONObject element = elements.getJSONObject(i);
      if (element == null)
      {
        continue;
      }
      if (Boolean.TRUE.equals(element.getBoolean("isRequired")))
      {
        String name = element.getString("name");
        if (StringUtils.isNotBlank(name))
        {
          required.add(name);
        }
      }
      collectRequiredFromElements(element.getJSONArray("elements"), required);
    }
  }

  public static boolean isKnownLeadFieldName(String fieldName)
  {
    return KNOWN_LEAD_FIELD_NAMES.contains(fieldName);
  }

  public static void validateRegistrationFormPair(String registrationTemplateId, String registrationFormJson)
  {
    boolean hasTemplateId = StringUtils.isNotBlank(registrationTemplateId);
    boolean hasFormJson = StringUtils.isNotBlank(registrationFormJson);
    if (hasTemplateId != hasFormJson)
    {
      throw new ServiceException("registrationTemplateId 与 registrationFormJson 须同时提供或同时清空",
          HttpStatus.BAD_REQUEST);
    }
    if (hasFormJson)
    {
      validateSurveyJson(registrationFormJson);
    }
  }
}
