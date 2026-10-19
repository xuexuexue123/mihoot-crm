package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.FollowupRuleSaveRequest;
import com.ruoyi.crm.service.followup.FollowupRuleConstants;

/**
 * 回访规则保存校验
 */
public final class FollowupRuleValidator
{
    private static final Set<String> DELAY_UNITS = new HashSet<>(Arrays.asList(
            FollowupRuleConstants.UNIT_MINUTES, FollowupRuleConstants.UNIT_HOURS, FollowupRuleConstants.UNIT_DAYS));

    private FollowupRuleValidator()
    {
    }

    public static ValidatedFollowupRule validateSaveRequest(FollowupRuleSaveRequest request, boolean requireRuleId)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (requireRuleId && StringUtils.isBlank(request.getRuleId()))
        {
            throw new ServiceException("ruleId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getName()))
        {
            throw new ServiceException("name 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getTriggerType())
                || !FollowupRuleConstants.TRIGGER_TYPES.contains(request.getTriggerType().trim()))
        {
            throw new ServiceException("triggerType 不合法", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getDelayMode())
                || !FollowupRuleConstants.DELAY_MODES.contains(request.getDelayMode().trim()))
        {
            throw new ServiceException("delayMode 不合法", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getTaskTemplate()))
        {
            throw new ServiceException("taskTemplate 不能为空", HttpStatus.BAD_REQUEST);
        }

        ValidatedFollowupRule validated = new ValidatedFollowupRule();
        validated.ruleId = trimToNull(request.getRuleId());
        validated.name = request.getName().trim();
        validated.description = trimToNull(request.getDescription());
        validated.enabled = normalizeFlag(request.getEnabled(), "enabled");
        validated.priority = request.getPriority() == null ? 100 : request.getPriority();
        validated.triggerType = request.getTriggerType().trim();
        validated.triggerConfig = optionalJsonObject(request.getTriggerConfig(), "triggerConfig");
        validated.delayMode = request.getDelayMode().trim();
        validated.delayValue = request.getDelayValue();
        validated.delayUnit = trimToNull(request.getDelayUnit());
        validated.delayField = trimToNull(request.getDelayField());
        validated.taskTemplate = requiredJsonObject(request.getTaskTemplate(), "taskTemplate");
        validated.dedupeConfig = optionalJsonObject(request.getDedupeConfig(), "dedupeConfig");
        validated.workflowId = trimToNull(request.getWorkflowId());
        validated.catalogKey = trimToNull(request.getCatalogKey());

        if (FollowupRuleConstants.DELAY_FIXED.equals(validated.delayMode)
                || FollowupRuleConstants.DELAY_BUSINESS_DAYS.equals(validated.delayMode))
        {
            if (validated.delayValue == null || validated.delayValue < 0)
            {
                throw new ServiceException("delayValue 必须为非负整数", HttpStatus.BAD_REQUEST);
            }
        }
        if (FollowupRuleConstants.DELAY_FIXED.equals(validated.delayMode))
        {
            if (validated.delayUnit == null || !DELAY_UNITS.contains(validated.delayUnit))
            {
                throw new ServiceException("fixed_delay 需要 delayUnit 为 minutes/hours/days", HttpStatus.BAD_REQUEST);
            }
        }
        if (FollowupRuleConstants.DELAY_UNTIL_FIELD.equals(validated.delayMode)
                && StringUtils.isBlank(validated.delayField))
        {
            throw new ServiceException("until_field 需要 delayField", HttpStatus.BAD_REQUEST);
        }
        return validated;
    }

    private static String normalizeFlag(String value, String field)
    {
        if (StringUtils.isBlank(value))
        {
            return FollowupRuleConstants.ENABLED;
        }
        if (FollowupRuleConstants.ENABLED.equals(value) || FollowupRuleConstants.DISABLED.equals(value))
        {
            return value;
        }
        throw new ServiceException(field + " 必须为 0 或 1", HttpStatus.BAD_REQUEST);
    }

    private static String requiredJsonObject(String json, String field)
    {
        String trimmed = json.trim();
        try
        {
            Object parsed = JSON.parse(trimmed);
            if (!(parsed instanceof JSONObject))
            {
                throw new ServiceException(field + " 必须为 JSON 对象", HttpStatus.BAD_REQUEST);
            }
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException(field + " 不是合法的 JSON", HttpStatus.BAD_REQUEST);
        }
        return trimmed;
    }

    private static String optionalJsonObject(String json, String field)
    {
        if (StringUtils.isBlank(json))
        {
            return null;
        }
        return requiredJsonObject(json, field);
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

    public static class ValidatedFollowupRule
    {
        public String ruleId;
        public String name;
        public String description;
        public String enabled;
        public Integer priority;
        public String triggerType;
        public String triggerConfig;
        public String delayMode;
        public Integer delayValue;
        public String delayUnit;
        public String delayField;
        public String taskTemplate;
        public String dedupeConfig;
        public String workflowId;
        public String catalogKey;
    }
}
