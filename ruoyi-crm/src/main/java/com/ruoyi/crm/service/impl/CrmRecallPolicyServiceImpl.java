package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmRecallPolicy;
import com.ruoyi.crm.domain.CrmRecallSchedule;
import com.ruoyi.crm.domain.dto.RecallPolicySaveRequest;
import com.ruoyi.crm.mapper.CrmRecallPolicyMapper;
import com.ruoyi.crm.service.ICrmRecallPolicyService;
import com.ruoyi.crm.service.recall.RecallPolicyConstants;
import com.ruoyi.crm.service.recall.RecallPolicyMatcher;
import com.ruoyi.crm.service.recall.RecallPolicySystemSeeder;
import com.ruoyi.crm.service.recall.RecallScheduleService;

@Service
public class CrmRecallPolicyServiceImpl implements ICrmRecallPolicyService
{
    @Autowired
    private CrmRecallPolicyMapper policyMapper;

    @Autowired
    private RecallPolicySystemSeeder systemSeeder;

    @Autowired
    private RecallScheduleService scheduleService;

    @Override
    public List<CrmRecallPolicy> selectList(String tenantId, String enabled)
    {
        tenantId = requireTenantId(tenantId);
        try
        {
            systemSeeder.ensure(tenantId);
        }
        catch (Exception ignored)
        {
        }
        return policyMapper.selectList(tenantId, enabled);
    }

    @Override
    public CrmRecallPolicy getById(String tenantId, String policyId)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(policyId))
        {
            return null;
        }
        return policyMapper.selectEntityById(tenantId, policyId.trim());
    }

    @Override
    public void add(String tenantId, RecallPolicySaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        Validated validated = validate(request, false);
        assertSingleCatchAll(tenantId, null, validated.matchJson, validated.enabled);
        CrmRecallPolicy row = new CrmRecallPolicy();
        row.setPolicyId(IdUtils.fastUUID());
        row.setTenantId(tenantId);
        row.setIsSystem(RecallPolicyConstants.DISABLED);
        row.setDelFlag("0");
        apply(row, validated);
        row.setCreateBy(safeUsername());
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(safeUsername());
        row.setUpdateTime(DateUtils.getNowDate());
        policyMapper.insertCrmRecallPolicy(row);
    }

    @Override
    public void update(String tenantId, RecallPolicySaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        Validated validated = validate(request, true);
        CrmRecallPolicy existing = policyMapper.selectEntityById(tenantId, validated.policyId);
        if (existing == null)
        {
            throw new ServiceException("复诊策略不存在或不属于当前租户", HttpStatus.BAD_REQUEST);
        }
        assertSingleCatchAll(tenantId, existing.getPolicyId(), validated.matchJson, validated.enabled);
        apply(existing, validated);
        existing.setUpdateBy(safeUsername());
        existing.setUpdateTime(DateUtils.getNowDate());
        policyMapper.updateCrmRecallPolicy(existing);
    }

    @Override
    public void remove(String tenantId, String policyId)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(policyId))
        {
            return;
        }
        CrmRecallPolicy existing = policyMapper.selectEntityById(tenantId, policyId.trim());
        if (existing == null)
        {
            return;
        }
        if (RecallPolicyConstants.IS_SYSTEM.equals(existing.getIsSystem()))
        {
            throw new ServiceException("系统预置策略不可删除", HttpStatus.BAD_REQUEST);
        }
        policyMapper.logicalDeleteById(existing.getPolicyId(), tenantId, safeUsername());
    }

    @Override
    public List<CrmRecallSchedule> selectScheduleList(String tenantId, String contactId, String status)
    {
        tenantId = requireTenantId(tenantId);
        return scheduleService.selectList(tenantId, contactId, status);
    }

    private void apply(CrmRecallPolicy row, Validated validated)
    {
        row.setName(validated.name);
        row.setDescription(validated.description);
        row.setEnabled(validated.enabled);
        row.setPriority(validated.priority);
        row.setMatchJson(validated.matchJson);
        row.setIntervalValue(validated.intervalValue);
        row.setIntervalUnit(validated.intervalUnit);
    }

    private void assertSingleCatchAll(String tenantId, String excludePolicyId, String matchJson, String enabled)
    {
        if (!RecallPolicyConstants.ENABLED.equals(enabled))
        {
            return;
        }
        CrmRecallPolicy probe = new CrmRecallPolicy();
        probe.setMatchJson(matchJson);
        if (!RecallPolicyMatcher.isCatchAll(probe))
        {
            return;
        }
        List<CrmRecallPolicy> enabledPolicies = policyMapper.selectEnabledOrdered(tenantId);
        if (enabledPolicies == null)
        {
            return;
        }
        for (CrmRecallPolicy policy : enabledPolicies)
        {
            if (excludePolicyId != null && excludePolicyId.equals(policy.getPolicyId()))
            {
                continue;
            }
            if (RecallPolicyMatcher.isCatchAll(policy))
            {
                throw new ServiceException("已存在启用的默认（catch-all）策略，请先停用或改为指定治疗项目", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private static Validated validate(RecallPolicySaveRequest request, boolean requireId)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (requireId && StringUtils.isBlank(request.getPolicyId()))
        {
            throw new ServiceException("policyId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getName()))
        {
            throw new ServiceException("name 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (request.getIntervalValue() == null || request.getIntervalValue() <= 0)
        {
            throw new ServiceException("intervalValue 必须为正整数", HttpStatus.BAD_REQUEST);
        }
        String unit = StringUtils.isBlank(request.getIntervalUnit())
                ? RecallPolicyConstants.UNIT_DAYS
                : request.getIntervalUnit().trim();
        if (!RecallPolicyConstants.UNIT_DAYS.equals(unit) && !RecallPolicyConstants.UNIT_MONTHS.equals(unit))
        {
            throw new ServiceException("intervalUnit 只支持 days 或 months", HttpStatus.BAD_REQUEST);
        }
        Validated validated = new Validated();
        validated.policyId = trimToNull(request.getPolicyId());
        validated.name = request.getName().trim();
        validated.description = trimToNull(request.getDescription());
        validated.enabled = normalizeFlag(request.getEnabled());
        validated.priority = request.getPriority() == null ? 100 : request.getPriority();
        validated.matchJson = normalizeMatchJson(request.getMatchJson());
        validated.intervalValue = request.getIntervalValue();
        validated.intervalUnit = unit;
        return validated;
    }

    private static String normalizeMatchJson(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            return "{\"treatmentItemCodes\":[]}";
        }
        try
        {
            JSONObject json = JSON.parseObject(raw);
            if (json == null)
            {
                throw new ServiceException("matchJson 不是合法 JSON", HttpStatus.BAD_REQUEST);
            }
            JSONArray codes = json.getJSONArray("treatmentItemCodes");
            if (codes == null)
            {
                json.put("treatmentItemCodes", new JSONArray());
            }
            return json.toJSONString();
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("matchJson 不是合法 JSON", HttpStatus.BAD_REQUEST);
        }
    }

    private static String normalizeFlag(String value)
    {
        if (StringUtils.isBlank(value) || RecallPolicyConstants.ENABLED.equals(value.trim()))
        {
            return RecallPolicyConstants.ENABLED;
        }
        if (RecallPolicyConstants.DISABLED.equals(value.trim()))
        {
            return RecallPolicyConstants.DISABLED;
        }
        throw new ServiceException("enabled 只支持 0 或 1", HttpStatus.BAD_REQUEST);
    }

    private static String trimToNull(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        return value.trim();
    }

    private String requireTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
        }
        return tenantId.trim();
    }

    private String safeUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception ex)
        {
            return "system";
        }
    }

    private static final class Validated
    {
        private String policyId;
        private String name;
        private String description;
        private String enabled;
        private Integer priority;
        private String matchJson;
        private Integer intervalValue;
        private String intervalUnit;
    }
}
