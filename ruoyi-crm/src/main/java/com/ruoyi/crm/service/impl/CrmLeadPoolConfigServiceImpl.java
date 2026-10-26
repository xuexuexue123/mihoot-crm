package com.ruoyi.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmLeadPoolConfigMapper;
import com.ruoyi.crm.service.ICrmLeadPoolConfigService;
import com.ruoyi.crm.service.support.EventRegistrationFormSupport;
import com.ruoyi.crm.service.support.LeadRecyclePolicyDefaults;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

@Service
public class CrmLeadPoolConfigServiceImpl implements ICrmLeadPoolConfigService
{
    @Autowired
    private CrmLeadPoolConfigMapper poolConfigMapper;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private ISysReTemplateService templateService;

    @Override
    public CrmLeadPoolConfig getConfig()
    {
        CrmLeadPoolConfig existing = poolConfigMapper.selectFirst();
        if (existing != null)
        {
            existing.setIdleToPoolHours(LeadRecyclePolicyDefaults.resolveIdleHours(
                    existing.getIdleToPoolHours(), existing.getProtectDays()));
            existing.setMaxHoldCount(LeadRecyclePolicyDefaults.resolveMaxHold(existing.getMaxHoldCount()));
            existing.setProtectDays(LeadRecyclePolicyDefaults.protectDaysFromHours(existing.getIdleToPoolHours()));
            return existing;
        }
        CrmLeadPoolConfig defaults = new CrmLeadPoolConfig();
        defaults.setClaimDailyLimit(30);
        defaults.setProtectDays(LeadRecyclePolicyDefaults.protectDaysFromHours(
                LeadRecyclePolicyDefaults.IDLE_TO_POOL_HOURS));
        defaults.setRecycleEnabled(true);
        defaults.setDefaultPoolOrgScope("dept");
        defaults.setQualifyTemplateId(null);
        defaults.setIdleToPoolHours(LeadRecyclePolicyDefaults.IDLE_TO_POOL_HOURS);
        defaults.setMaxHoldCount(LeadRecyclePolicyDefaults.MAX_HOLD_COUNT);
        return defaults;
    }

    @Override
    public CrmLeadPoolConfig saveConfig(CrmLeadPoolConfig config, boolean qualifyTemplateIdPresent)
    {
        if (config == null)
        {
            throw new ServiceException("配置不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmLeadPoolConfig existing = poolConfigMapper.selectFirst();
        String resolvedQualifyTemplateId = resolveQualifyTemplateId(config, existing, qualifyTemplateIdPresent);
        syncIdleHoursAndProtectDays(config, existing);
        if (config.getRecycleEnabled() == null)
        {
            config.setRecycleEnabled(existing != null && existing.getRecycleEnabled() != null
                    ? existing.getRecycleEnabled() : Boolean.TRUE);
        }
        if (config.getClaimDailyLimit() == null)
        {
            config.setClaimDailyLimit(existing != null && existing.getClaimDailyLimit() != null
                    ? existing.getClaimDailyLimit() : 30);
        }
        if (config.getMaxHoldCount() == null)
        {
            config.setMaxHoldCount(existing != null && existing.getMaxHoldCount() != null
                    ? existing.getMaxHoldCount() : LeadRecyclePolicyDefaults.MAX_HOLD_COUNT);
        }
        if (config.getMaxHoldCount() < 1)
        {
            throw new ServiceException("最大持有量须大于 0", HttpStatus.BAD_REQUEST);
        }

        if (existing == null)
        {
            config.setConfigId(IdUtils.fastUUID());
            config.setQualifyTemplateId(resolvedQualifyTemplateId);
            config.setCreateTime(DateUtils.getNowDate());
            config.setUpdateTime(DateUtils.getNowDate());
            poolConfigMapper.insertCrmLeadPoolConfig(config);
            return config;
        }

        config.setConfigId(existing.getConfigId());
        config.setQualifyTemplateId(resolvedQualifyTemplateId);
        config.setUpdateTime(DateUtils.getNowDate());
        poolConfigMapper.updateCrmLeadPoolConfig(config);
        return poolConfigMapper.selectFirst();
    }

    void syncIdleHoursAndProtectDays(CrmLeadPoolConfig config, CrmLeadPoolConfig existing)
    {
        Integer hours = config.getIdleToPoolHours();
        Integer days = config.getProtectDays();
        if (hours == null && days != null)
        {
            hours = days * 24;
        }
        if (hours == null && existing != null)
        {
            hours = LeadRecyclePolicyDefaults.resolveIdleHours(existing.getIdleToPoolHours(),
                    existing.getProtectDays());
        }
        if (hours == null)
        {
            hours = LeadRecyclePolicyDefaults.IDLE_TO_POOL_HOURS;
        }
        if (hours < 1)
        {
            throw new ServiceException("未跟进转入公海时间须大于 0", HttpStatus.BAD_REQUEST);
        }
        config.setIdleToPoolHours(hours);
        config.setProtectDays(LeadRecyclePolicyDefaults.protectDaysFromHours(hours));
    }

    /**
     * 省略字段 → 保留原值；null/空串 → 清空；非空 → 校验后写入。
     */
    String resolveQualifyTemplateId(CrmLeadPoolConfig config, CrmLeadPoolConfig existing,
            boolean qualifyTemplateIdPresent)
    {
        if (!qualifyTemplateIdPresent)
        {
            return existing != null ? existing.getQualifyTemplateId() : null;
        }
        String raw = config.getQualifyTemplateId();
        if (StringUtils.isEmpty(raw))
        {
            return null;
        }
        String templateId = raw.trim();
        validateQualifyTemplateId(templateId);
        return templateId;
    }

    void validateQualifyTemplateId(String templateId)
    {
        SysReTemplate template = templateService.selectSysReTemplateByTemplateId(templateId);
        if (template == null)
        {
            throw new ServiceException("模板不存在", HttpStatus.BAD_REQUEST);
        }
        if (!EventRegistrationFormSupport.isSurveyCategory(template.getCategory()))
        {
            throw new ServiceException("须为 Survey 类模板", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void assertClaimAllowed(Long userId)
    {
        if (userId == null)
        {
            return;
        }
        CrmLeadPoolConfig config = getConfig();
        Integer limit = config.getClaimDailyLimit();
        if (limit == null || limit <= 0)
        {
            return;
        }
        int todayCount = poolConfigMapper.countClaimsTodayByUserId(userId);
        if (todayCount >= limit)
        {
            throw new ServiceException("已超过今日领取上限", 429)
                    .setReason(LeadRecyclePolicyDefaults.REASON_CLAIM_DAILY_LIMIT);
        }
    }

    @Override
    public void assertHoldAllowed(Long userId)
    {
        if (userId == null)
        {
            return;
        }
        CrmLeadPoolConfig config = getConfig();
        int maxHold = LeadRecyclePolicyDefaults.resolveMaxHold(config.getMaxHoldCount());
        int assigned = crmLeadMapper.countAssignedLeadsByUserId(userId);
        if (assigned >= maxHold)
        {
            throw new ServiceException("已超过私海持有上限", 429)
                    .setReason(LeadRecyclePolicyDefaults.REASON_HOLD_LIMIT);
        }
    }
}
