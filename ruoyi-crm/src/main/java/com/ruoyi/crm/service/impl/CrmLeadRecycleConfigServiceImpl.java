package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmLeadDedupeConfig;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.domain.dto.LeadConvertConfigUpsertDto;
import com.ruoyi.crm.domain.dto.LeadRecycleConfigUpsertDto;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecycleConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecycleConvertConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecycleDedupeConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecyclePoolConfigVo;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.mapper.CrmLeadDedupeConfigMapper;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;
import com.ruoyi.crm.service.ICrmLeadPoolConfigService;
import com.ruoyi.crm.service.ICrmLeadRecycleConfigService;
import com.ruoyi.crm.service.support.LeadRecyclePolicyDefaults;

@Service
public class CrmLeadRecycleConfigServiceImpl implements ICrmLeadRecycleConfigService
{
    @Autowired
    private ICrmLeadPoolConfigService poolConfigService;

    @Autowired
    private ICrmLeadConvertConfigService convertConfigService;

    @Autowired
    private CrmLeadDedupeConfigMapper dedupeConfigMapper;

    @Autowired
    private IAssignableUserService assignableUserService;

    @Override
    public LeadRecycleConfigVo getConfig()
    {
        LeadRecycleConfigVo vo = new LeadRecycleConfigVo();
        vo.setPool(toPoolVo(poolConfigService.getConfig()));
        vo.setDedupe(getDedupeConfig());
        vo.setConvert(toConvertVo(convertConfigService.getConfig(null)));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LeadRecycleConfigVo saveConfig(LeadRecycleConfigUpsertDto dto)
    {
        if (dto == null || dto.getPool() == null || dto.getDedupe() == null || dto.getConvert() == null)
        {
            throw new ServiceException("配置不能为空", HttpStatus.BAD_REQUEST);
        }
        validatePool(dto.getPool());
        validateDedupe(dto.getDedupe());
        validateConvert(dto.getConvert());
        savePool(dto.getPool());
        saveDedupe(dto.getDedupe());
        saveConvert(dto.getConvert());
        return getConfig();
    }

    @Override
    public LeadRecycleDedupeConfigVo getDedupeConfig()
    {
        CrmLeadDedupeConfig stored = dedupeConfigMapper.selectFirst();
        if (stored == null)
        {
            return defaultDedupe();
        }
        LeadRecycleDedupeConfigVo vo = new LeadRecycleDedupeConfigVo();
        vo.setLeadMatchFields(parseFields(stored.getLeadMatchFields(),
                LeadRecyclePolicyDefaults.DEFAULT_LEAD_MATCH_FIELDS));
        vo.setContactMatchFields(parseFields(stored.getContactMatchFields(),
                LeadRecyclePolicyDefaults.DEFAULT_CONTACT_MATCH_FIELDS));
        vo.setMatchMode(stored.getMatchMode() == null || stored.getMatchMode().isEmpty()
                ? LeadRecyclePolicyDefaults.MATCH_MODE_ANY : stored.getMatchMode());
        return vo;
    }

    private void savePool(LeadRecyclePoolConfigVo pool)
    {
        validatePool(pool);
        CrmLeadPoolConfig existing = poolConfigService.getConfig();
        CrmLeadPoolConfig patch = new CrmLeadPoolConfig();
        patch.setClaimDailyLimit(existing.getClaimDailyLimit());
        patch.setRecycleEnabled(pool.getRecycleEnabled() != null
                ? pool.getRecycleEnabled() : Boolean.TRUE);
        patch.setDefaultPoolOrgScope(existing.getDefaultPoolOrgScope());
        patch.setIdleToPoolHours(pool.getIdleToPoolHours());
        patch.setMaxHoldCount(pool.getMaxHoldCount());
        patch.setQualifyTemplateId(existing.getQualifyTemplateId());
        poolConfigService.saveConfig(patch, false);
    }

    private void saveDedupe(LeadRecycleDedupeConfigVo dedupe)
    {
        List<String> leadFields = normalizeFields(dedupe.getLeadMatchFields(),
                LeadRecyclePolicyDefaults.ALLOWED_LEAD_MATCH_FIELDS, "线索查重字段");
        List<String> contactFields = normalizeFields(dedupe.getContactMatchFields(),
                LeadRecyclePolicyDefaults.ALLOWED_CONTACT_MATCH_FIELDS, "客户查重字段");
        CrmLeadDedupeConfig existing = dedupeConfigMapper.selectFirst();
        CrmLeadDedupeConfig row = existing != null ? existing : new CrmLeadDedupeConfig();
        row.setLeadMatchFields(JSON.toJSONString(leadFields));
        row.setContactMatchFields(JSON.toJSONString(contactFields));
        row.setMatchMode(LeadRecyclePolicyDefaults.MATCH_MODE_ANY);
        row.setUpdateTime(DateUtils.getNowDate());
        if (existing == null)
        {
            row.setConfigId(IdUtils.fastUUID());
            row.setCreateTime(DateUtils.getNowDate());
            dedupeConfigMapper.insertCrmLeadDedupeConfig(row);
        }
        else
        {
            dedupeConfigMapper.updateCrmLeadDedupeConfig(row);
        }
    }

    private void saveConvert(LeadRecycleConvertConfigVo convert)
    {
        String mode = validatedOwnerMode(convert);
        Long specifiedUserId = LeadRecyclePolicyDefaults.OWNER_SPECIFIED_USER.equals(mode)
                ? convert.getContactOwnerUserId() : null;
        LeadConvertConfigVo existingConvert = convertConfigService.getConfig(null);
        LeadConvertConfigUpsertDto dto = new LeadConvertConfigUpsertDto();
        if (existingConvert != null)
        {
            dto.setAutoConvertOnFirstVisit(existingConvert.getAutoConvertOnFirstVisit());
            dto.setAutoConvertOnCheckIn(existingConvert.getAutoConvertOnCheckIn());
            dto.setAutoConvertOnRegistration(existingConvert.getAutoConvertOnRegistration());
            dto.setMergeOnDuplicatePhone(existingConvert.getMergeOnDuplicatePhone());
            dto.setDefaultOrgId(existingConvert.getDefaultOrgId());
        }
        dto.setContactOwnerMode(mode);
        dto.setContactOwnerUserId(specifiedUserId);
        convertConfigService.upsert(null, dto);
    }

    private static void validatePool(LeadRecyclePoolConfigVo pool)
    {
        if (pool.getIdleToPoolHours() == null || pool.getIdleToPoolHours() < 1)
        {
            throw new ServiceException("未跟进转入公海时间须大于 0", HttpStatus.BAD_REQUEST);
        }
        if (pool.getMaxHoldCount() == null || pool.getMaxHoldCount() < 1)
        {
            throw new ServiceException("最大持有量须大于 0", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateDedupe(LeadRecycleDedupeConfigVo dedupe)
    {
        normalizeFields(dedupe.getLeadMatchFields(),
                LeadRecyclePolicyDefaults.ALLOWED_LEAD_MATCH_FIELDS, "线索查重字段");
        normalizeFields(dedupe.getContactMatchFields(),
                LeadRecyclePolicyDefaults.ALLOWED_CONTACT_MATCH_FIELDS, "客户查重字段");
    }

    private void validateConvert(LeadRecycleConvertConfigVo convert)
    {
        validatedOwnerMode(convert);
    }

    private String validatedOwnerMode(LeadRecycleConvertConfigVo convert)
    {
        String mode = convert.getContactOwnerMode();
        if (mode == null || mode.trim().isEmpty())
        {
            mode = LeadRecyclePolicyDefaults.OWNER_KEEP_LEAD;
        }
        if (!LeadRecyclePolicyDefaults.ALLOWED_OWNER_MODES.contains(mode))
        {
            throw new ServiceException("无效的客户负责人策略", HttpStatus.BAD_REQUEST);
        }
        if (LeadRecyclePolicyDefaults.OWNER_SPECIFIED_USER.equals(mode))
        {
            if (convert.getContactOwnerUserId() == null)
            {
                throw new ServiceException("指定负责人时必须选择用户", HttpStatus.BAD_REQUEST);
            }
            AssignableResolveResult resolve = assignableUserService.resolve(null, convert.getContactOwnerUserId(),
                    "contact_owner_dropdown");
            if (!resolve.isResolved())
            {
                throw new ServiceException("指定用户不在可分配池: " + resolve.getReason(), HttpStatus.BAD_REQUEST);
            }
        }
        return mode;
    }

    private static LeadRecyclePoolConfigVo toPoolVo(CrmLeadPoolConfig config)
    {
        LeadRecyclePoolConfigVo vo = new LeadRecyclePoolConfigVo();
        vo.setIdleToPoolHours(LeadRecyclePolicyDefaults.resolveIdleHours(
                config.getIdleToPoolHours(), config.getProtectDays()));
        vo.setMaxHoldCount(LeadRecyclePolicyDefaults.resolveMaxHold(config.getMaxHoldCount()));
        vo.setRecycleEnabled(config.getRecycleEnabled() == null || config.getRecycleEnabled());
        return vo;
    }

    private static LeadRecycleConvertConfigVo toConvertVo(LeadConvertConfigVo config)
    {
        LeadRecycleConvertConfigVo vo = new LeadRecycleConvertConfigVo();
        vo.setContactOwnerMode(config.getContactOwnerMode() == null || config.getContactOwnerMode().isEmpty()
                ? LeadRecyclePolicyDefaults.OWNER_KEEP_LEAD : config.getContactOwnerMode());
        vo.setContactOwnerUserId(config.getContactOwnerUserId());
        return vo;
    }

    private static LeadRecycleDedupeConfigVo defaultDedupe()
    {
        LeadRecycleDedupeConfigVo vo = new LeadRecycleDedupeConfigVo();
        vo.setLeadMatchFields(new ArrayList<>(LeadRecyclePolicyDefaults.DEFAULT_LEAD_MATCH_FIELDS));
        vo.setContactMatchFields(new ArrayList<>(LeadRecyclePolicyDefaults.DEFAULT_CONTACT_MATCH_FIELDS));
        vo.setMatchMode(LeadRecyclePolicyDefaults.MATCH_MODE_ANY);
        return vo;
    }

    private static List<String> parseFields(String json, List<String> fallback)
    {
        if (json == null || json.trim().isEmpty())
        {
            return new ArrayList<>(fallback);
        }
        try
        {
            List<String> parsed = JSON.parseArray(json, String.class);
            if (parsed == null || parsed.isEmpty())
            {
                return new ArrayList<>(fallback);
            }
            return parsed;
        }
        catch (Exception ex)
        {
            return new ArrayList<>(fallback);
        }
    }

    private static List<String> normalizeFields(List<String> raw, java.util.Set<String> allowed, String label)
    {
        if (raw == null || raw.isEmpty())
        {
            throw new ServiceException("请至少勾选一个" + label, HttpStatus.BAD_REQUEST);
        }
        List<String> normalized = new ArrayList<>();
        for (String field : raw)
        {
            if (field == null || field.trim().isEmpty())
            {
                continue;
            }
            String key = field.trim();
            if ("wechat_id".equals(key))
            {
                key = "wechat";
            }
            if (!allowed.contains(key) && !allowed.contains(field.trim()))
            {
                throw new ServiceException(label + "包含不支持的项: " + field, HttpStatus.BAD_REQUEST);
            }
            if (!normalized.contains(key))
            {
                normalized.add(key);
            }
        }
        if (normalized.isEmpty())
        {
            throw new ServiceException("请至少勾选一个" + label, HttpStatus.BAD_REQUEST);
        }
        return normalized;
    }
}
