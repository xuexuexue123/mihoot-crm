package com.ruoyi.crm.service.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.WebsiteLeadSubmitRequest;

/**
 * 官网在线预约提交校验
 */
public final class WebsiteLeadValidationSupport
{
    private static final Logger log = LoggerFactory.getLogger(WebsiteLeadValidationSupport.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private WebsiteLeadValidationSupport()
    {
    }

    public static void validateSubmission(WebsiteLeadSubmitRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!Boolean.TRUE.equals(request.getConsentAccepted()))
        {
            throw new ServiceException("consentAccepted 必须为 true", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getTenantSlug()))
        {
            throw new ServiceException("tenantSlug 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getLeadName()))
        {
            throw new ServiceException("leadName 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getMobilePhone()))
        {
            throw new ServiceException("mobilePhone 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getOrgId()))
        {
            throw new ServiceException("orgId 不能为空", HttpStatus.BAD_REQUEST);
        }
        validateAppointmentDate(request.getAppointmentDate());
        if (StringUtils.isBlank(request.getAppointmentTime()))
        {
            throw new ServiceException("appointmentTime 不能为空", HttpStatus.BAD_REQUEST);
        }
        validateCustomField(request.getCustomField());
        logAudit(request);
    }

    public static void validateAppointmentDate(String appointmentDate)
    {
        if (StringUtils.isBlank(appointmentDate))
        {
            throw new ServiceException("appointmentDate 不能为空", HttpStatus.BAD_REQUEST);
        }
        try
        {
            LocalDate.parse(appointmentDate.trim(), DATE_FORMAT);
        }
        catch (DateTimeParseException ex)
        {
            throw new ServiceException("appointmentDate 格式须为 YYYY-MM-DD", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateCustomField(String customField)
    {
        if (StringUtils.isBlank(customField))
        {
            return;
        }
        try
        {
            JSONArray array = JSON.parseArray(customField);
            if (array == null)
            {
                throw new ServiceException("customField 须为 JSON 数组", HttpStatus.BAD_REQUEST);
            }
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject item = array.getJSONObject(i);
                if (item == null || StringUtils.isBlank(item.getString("fieldName")))
                {
                    throw new ServiceException("customField 每项须包含 fieldName", HttpStatus.BAD_REQUEST);
                }
            }
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("customField 须为合法 JSON 数组", HttpStatus.BAD_REQUEST);
        }
    }

    private static void logAudit(WebsiteLeadSubmitRequest request)
    {
        log.warn("websitelead submission tenantSlug={} orgId={} mobilePhone={}",
                request.getTenantSlug(),
                request.getOrgId(),
                maskPhone(request.getMobilePhone()));
    }

    static String maskPhone(String phone)
    {
        if (StringUtils.isBlank(phone))
        {
            return "";
        }
        String trimmed = phone.trim();
        if (trimmed.length() <= 4)
        {
            return "****";
        }
        return trimmed.substring(0, 3) + "****" + trimmed.substring(trimmed.length() - 2);
    }
}
