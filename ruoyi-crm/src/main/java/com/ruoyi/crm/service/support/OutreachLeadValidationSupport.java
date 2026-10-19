package com.ruoyi.crm.service.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmOutreachlead;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

/**
 * 活动报名表单与 outreach lead 提交校验
 */
public final class OutreachLeadValidationSupport
{
  private static final Set<String> REGISTRATION_BLOCKED_EVENT_STATUSES;

  static
  {
    Set<String> blocked = new HashSet<>();
    blocked.add("cancelled");
    blocked.add("canceled");
    blocked.add("已取消");
    REGISTRATION_BLOCKED_EVENT_STATUSES = Collections.unmodifiableSet(blocked);
  }

  private OutreachLeadValidationSupport()
  {
  }

  public static void validateEventAcceptsRegistration(CrmEvent event)
  {
    if (event == null)
    {
      throw new ServiceException("活动不存在", HttpStatus.NOT_FOUND);
    }
    if (Boolean.FALSE.equals(event.getRegistrationEnabled()))
    {
      throw new ServiceException("REGISTRATION_CLOSED", HttpStatus.GONE);
    }
    String status = StringUtils.trim(event.getStatus());
    if (StringUtils.isBlank(status))
    {
      return;
    }
    if (REGISTRATION_BLOCKED_EVENT_STATUSES.contains(status.toLowerCase(Locale.ROOT)))
    {
      throw new ServiceException("活动已取消，无法报名", HttpStatus.FORBIDDEN);
    }
  }

  public static void validateRegistrationFormConfig(CrmEvent crmEvent, ISysReTemplateService templateService)
  {
    if (crmEvent == null)
    {
      return;
    }

    boolean touched = Boolean.TRUE.equals(crmEvent.getRegistrationFormTouched());
    boolean hasTemplateId = StringUtils.isNotBlank(crmEvent.getRegistrationTemplateId());
    boolean hasFormJson = StringUtils.isNotBlank(crmEvent.getRegistrationFormJson());
    if (!touched && !hasTemplateId && !hasFormJson)
    {
      return;
    }

    EventRegistrationFormSupport.validateRegistrationFormPair(
        crmEvent.getRegistrationTemplateId(),
        crmEvent.getRegistrationFormJson());

    if (!hasTemplateId)
    {
      return;
    }

    SysReTemplate template = templateService.selectSysReTemplateByTemplateId(crmEvent.getRegistrationTemplateId());
    if (template == null)
    {
      throw new ServiceException("报名表单模版不存在", HttpStatus.BAD_REQUEST);
    }
    if (!EventRegistrationFormSupport.isSurveyCategory(template.getCategory()))
    {
      throw new ServiceException("报名表单模版 category 须为 Survey", HttpStatus.BAD_REQUEST);
    }
  }

  public static void validateOutreachLeadSubmission(CrmOutreachlead lead, CrmEvent event)
  {
    if (lead == null)
    {
      throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
    }

    if (StringUtils.isBlank(lead.getLeadName()) && StringUtils.isBlank(lead.getMobilePhone()))
    {
      throw new ServiceException("leadName 与 mobilePhone 至少一项必填", HttpStatus.BAD_REQUEST);
    }

    if (StringUtils.isBlank(lead.getSid()))
    {
      return;
    }

    if (event == null)
    {
      throw new ServiceException("活动不存在", HttpStatus.BAD_REQUEST);
    }

    validateEventAcceptsRegistration(event);

    String formJson = StringUtils.isNotBlank(event.getRegistrationFormJson())
        ? event.getRegistrationFormJson()
        : EventRegistrationFormSupport.DEFAULT_REGISTRATION_FORM_JSON;

    for (String fieldName : EventRegistrationFormSupport.extractRequiredFieldNames(formJson))
    {
      if (!hasFieldValue(lead, fieldName))
      {
        throw new ServiceException("缺少必填字段: " + fieldName, HttpStatus.BAD_REQUEST);
      }
    }
  }

  static boolean hasFieldValue(CrmOutreachlead lead, String fieldName)
  {
    String value = resolveLeadFieldValue(lead, fieldName);
    return StringUtils.isNotBlank(value);
  }

  private static String resolveLeadFieldValue(CrmOutreachlead lead, String fieldName)
  {
    if (lead == null || StringUtils.isBlank(fieldName))
    {
      return null;
    }

    switch (fieldName)
    {
      case "leadName":
        return lead.getLeadName();
      case "mobilePhone":
        return lead.getMobilePhone();
      case "fullName":
        return lead.getFullName();
      case "workEmail":
        return lead.getWorkEmail();
      case "shortdesc":
      case "shortDescription":
        return lead.getShortDescription();
      case "description":
        return lead.getDescription();
      case "workPhone":
        return lead.getWorkPhone();
      case "industry":
        return lead.getIndustry();
      case "website":
        return lead.getWebsite();
      default:
        return resolveCustomFieldValue(lead.getCustomField(), fieldName);
    }
  }

  private static String resolveCustomFieldValue(String customFieldJson, String fieldName)
  {
    if (StringUtils.isBlank(customFieldJson))
    {
      return null;
    }
    try
    {
      JSONArray array = JSON.parseArray(customFieldJson);
      if (array == null)
      {
        return null;
      }
      for (int i = 0; i < array.size(); i++)
      {
        JSONObject item = array.getJSONObject(i);
        if (item == null)
        {
          continue;
        }
        if (fieldName.equals(item.getString("fieldName")))
        {
          Object value = item.get("value");
          if (value == null)
          {
            return null;
          }
          if (value instanceof String)
          {
            return (String) value;
          }
          return JSON.toJSONString(value);
        }
      }
    }
    catch (Exception ignored)
    {
      return null;
    }
    return null;
  }
}
