package com.ruoyi.crm.service.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.FollowUpCadenceSaveRequest;

/**
 * 跟进序列配置校验
 */
public final class FollowUpCadenceValidator
{
    private static final String ENABLED = "1";
    private static final String DISABLED = "0";
    private static final String DEFAULT_START_TRIGGER = "Initial Email Sent";

    private FollowUpCadenceValidator()
    {
    }

    public static ValidatedFollowUpCadence validateSaveRequest(FollowUpCadenceSaveRequest request, boolean requireCadenceId)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (requireCadenceId && StringUtils.isBlank(request.getCadenceId()))
        {
            throw new ServiceException("cadenceId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getName()))
        {
            throw new ServiceException("name 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getEnabled()))
        {
            throw new ServiceException("enabled 不能为空", HttpStatus.BAD_REQUEST);
        }

        ValidatedFollowUpCadence validated = new ValidatedFollowUpCadence();
        validated.cadenceId = trimToNull(request.getCadenceId());
        validated.name = request.getName().trim();
        validated.description = trimToNull(request.getDescription());
        validated.enabled = normalizeEnabled(request.getEnabled());
        validated.startTrigger = StringUtils.isNotBlank(request.getStartTrigger())
                ? request.getStartTrigger().trim()
                : DEFAULT_START_TRIGGER;
        validated.stepsJson = validateStepsJson(request.getStepsJson());
        validated.remark = trimToNull(request.getRemark());
        return validated;
    }

    private static String normalizeEnabled(String enabled)
    {
        if (ENABLED.equals(enabled) || DISABLED.equals(enabled))
        {
            return enabled;
        }
        throw new ServiceException("enabled 必须为 0 或 1", HttpStatus.BAD_REQUEST);
    }

    private static String validateStepsJson(String stepsJson)
    {
        if (stepsJson == null)
        {
            return null;
        }
        String trimmed = stepsJson.trim();
        if (trimmed.isEmpty())
        {
            return null;
        }
        try
        {
            Object parsed = JSON.parse(trimmed);
            if (!(parsed instanceof JSONArray))
            {
                throw new ServiceException("stepsJson 必须为 JSON 数组", HttpStatus.BAD_REQUEST);
            }
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("stepsJson 不是合法的 JSON", HttpStatus.BAD_REQUEST);
        }
        return trimmed;
    }

    private static String trimToNull(String value)
    {
        if (value == null)
        {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static class ValidatedFollowUpCadence
    {
        public String cadenceId;
        public String name;
        public String description;
        public String enabled;
        public String startTrigger;
        public String stepsJson;
        public String remark;
    }
}
