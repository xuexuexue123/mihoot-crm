package com.ruoyi.crm.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmFollowupRule;
import com.ruoyi.crm.domain.dto.FollowupRuleSaveRequest;
import com.ruoyi.crm.domain.vo.FollowupRuleEnrollmentVo;
import com.ruoyi.crm.domain.vo.FollowupRuleVo;
import com.ruoyi.crm.mapper.CrmFollowupRuleEnrollmentMapper;
import com.ruoyi.crm.mapper.CrmFollowupRuleMapper;
import com.ruoyi.crm.service.ICrmFollowupRuleService;
import com.ruoyi.crm.service.followup.FollowupRuleConstants;
import com.ruoyi.crm.service.followup.FollowupRuleSystemSeeder;
import com.ruoyi.crm.service.support.FollowupRuleValidator;
import com.ruoyi.crm.service.support.FollowupRuleValidator.ValidatedFollowupRule;

@Service
public class CrmFollowupRuleServiceImpl implements ICrmFollowupRuleService
{
    @Autowired
    private CrmFollowupRuleMapper ruleMapper;

    @Autowired
    private CrmFollowupRuleEnrollmentMapper enrollmentMapper;

    @Autowired
    private FollowupRuleSystemSeeder systemSeeder;

    @Override
    public List<FollowupRuleVo> selectList(String tenantId, String triggerType, String enabled)
    {
        tenantId = requireTenantId(tenantId);
        try
        {
            systemSeeder.ensure(tenantId);
        }
        catch (Exception ignored)
        {
        }
        return ruleMapper.selectList(tenantId, triggerType, enabled);
    }

    @Override
    public FollowupRuleVo getById(String tenantId, String ruleId)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(ruleId))
        {
            return null;
        }
        return ruleMapper.selectById(tenantId, ruleId.trim());
    }

    @Override
    public void add(String tenantId, FollowupRuleSaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        ValidatedFollowupRule validated = FollowupRuleValidator.validateSaveRequest(request, false);
        String username = safeUsername();
        CrmFollowupRule row = new CrmFollowupRule();
        row.setRuleId(IdUtils.fastUUID());
        row.setTenantId(tenantId);
        row.setIsSystem(FollowupRuleConstants.DISABLED);
        row.setDelFlag("0");
        applyValidated(row, validated);
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        ruleMapper.insertCrmFollowupRule(row);
    }

    @Override
    public void update(String tenantId, FollowupRuleSaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        ValidatedFollowupRule validated = FollowupRuleValidator.validateSaveRequest(request, true);
        CrmFollowupRule existing = ruleMapper.selectEntityById(tenantId, validated.ruleId);
        if (existing == null)
        {
            throw new ServiceException("回访规则不存在或不属于当前租户", HttpStatus.BAD_REQUEST);
        }
        applyValidated(existing, validated);
        existing.setUpdateBy(safeUsername());
        existing.setUpdateTime(DateUtils.getNowDate());
        ruleMapper.updateCrmFollowupRule(existing);
    }

    @Override
    public void remove(String tenantId, String ruleId)
    {
        tenantId = requireTenantId(tenantId);
        if (StringUtils.isBlank(ruleId))
        {
            return;
        }
        CrmFollowupRule existing = ruleMapper.selectEntityById(tenantId, ruleId.trim());
        if (existing == null)
        {
            return;
        }
        if (FollowupRuleConstants.IS_SYSTEM.equals(existing.getIsSystem()))
        {
            throw new ServiceException("系统预置规则不可删除", HttpStatus.BAD_REQUEST);
        }
        ruleMapper.logicalDeleteById(existing.getRuleId(), tenantId, safeUsername());
    }

    @Override
    public List<FollowupRuleEnrollmentVo> selectEnrollmentList(String tenantId, String ruleId, String status,
            String subjectId)
    {
        tenantId = requireTenantId(tenantId);
        return enrollmentMapper.selectList(tenantId, ruleId, status, subjectId);
    }

    @Override
    public List<String> selectEnabledCatalogKeys(String tenantId)
    {
        tenantId = requireTenantId(tenantId);
        try
        {
            systemSeeder.ensure(tenantId);
        }
        catch (Exception ignored)
        {
        }
        List<String> keys = ruleMapper.selectEnabledCatalogKeys(tenantId);
        return keys == null ? Collections.emptyList() : keys;
    }

    private void applyValidated(CrmFollowupRule row, ValidatedFollowupRule validated)
    {
        row.setName(validated.name);
        row.setDescription(validated.description);
        row.setEnabled(validated.enabled);
        row.setPriority(validated.priority);
        row.setTriggerType(validated.triggerType);
        row.setTriggerConfig(validated.triggerConfig);
        row.setDelayMode(validated.delayMode);
        row.setDelayValue(validated.delayValue);
        row.setDelayUnit(validated.delayUnit);
        row.setDelayField(validated.delayField);
        row.setTaskTemplate(validated.taskTemplate);
        row.setDedupeConfig(validated.dedupeConfig);
        row.setWorkflowId(validated.workflowId);
        row.setCatalogKey(validated.catalogKey);
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
}
