package com.ruoyi.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmLeadConvertConfig;
import com.ruoyi.crm.domain.dto.LeadConvertConfigUpsertDto;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.mapper.CrmLeadConvertConfigMapper;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.LeadRecyclePolicyDefaults;

@Service
public class CrmLeadConvertConfigServiceImpl implements ICrmLeadConvertConfigService
{
    @Autowired
    private CrmLeadConvertConfigMapper configMapper;

    @Override
    public LeadConvertConfigVo getConfig(String tenantId)
    {
        tenantId = resolveTenantId(tenantId);
        LeadConvertConfigVo stored = configMapper.selectVoByTenantId(tenantId);
        if (stored == null)
        {
            return LeadConvertConfigVo.defaults();
        }
        applyDefaultsIfNull(stored);
        return stored;
    }

    @Override
    public LeadConvertConfigVo upsert(String tenantId, LeadConvertConfigUpsertDto dto)
    {
        tenantId = resolveTenantId(tenantId);
        if (dto == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        String username = SecurityUtils.getUsername();
        CrmLeadConvertConfig existing = configMapper.selectEntityByTenantId(tenantId);
        if (existing == null)
        {
            CrmLeadConvertConfig row = new CrmLeadConvertConfig();
            row.setConfigId(IdUtils.fastUUID());
            row.setTenantId(tenantId);
            row.setAutoConvertOnFirstVisit(dto.getAutoConvertOnFirstVisit() != null
                    ? dto.getAutoConvertOnFirstVisit() : true);
            row.setAutoConvertOnCheckIn(dto.getAutoConvertOnCheckIn() != null
                    ? dto.getAutoConvertOnCheckIn() : true);
            row.setAutoConvertOnRegistration(dto.getAutoConvertOnRegistration() != null
                    ? dto.getAutoConvertOnRegistration() : false);
            row.setMergeOnDuplicatePhone(dto.getMergeOnDuplicatePhone() != null
                    ? dto.getMergeOnDuplicatePhone() : true);
            row.setDefaultOrgId(dto.getDefaultOrgId());
            row.setContactOwnerMode(dto.getContactOwnerMode() != null
                    ? dto.getContactOwnerMode() : LeadRecyclePolicyDefaults.OWNER_KEEP_LEAD);
            row.setContactOwnerUserId(dto.getContactOwnerUserId());
            row.setCreateBy(username);
            row.setCreateTime(DateUtils.getNowDate());
            row.setUpdateBy(username);
            row.setUpdateTime(DateUtils.getNowDate());
            configMapper.insert(row);
        }
        else
        {
            if (dto.getAutoConvertOnFirstVisit() != null)
            {
                existing.setAutoConvertOnFirstVisit(dto.getAutoConvertOnFirstVisit());
            }
            if (dto.getAutoConvertOnCheckIn() != null)
            {
                existing.setAutoConvertOnCheckIn(dto.getAutoConvertOnCheckIn());
            }
            if (dto.getAutoConvertOnRegistration() != null)
            {
                existing.setAutoConvertOnRegistration(dto.getAutoConvertOnRegistration());
            }
            if (dto.getMergeOnDuplicatePhone() != null)
            {
                existing.setMergeOnDuplicatePhone(dto.getMergeOnDuplicatePhone());
            }
            if (dto.getContactOwnerMode() != null)
            {
                existing.setContactOwnerMode(dto.getContactOwnerMode());
                existing.setContactOwnerUserId(dto.getContactOwnerUserId());
            }
            else if (dto.getContactOwnerUserId() != null)
            {
                existing.setContactOwnerUserId(dto.getContactOwnerUserId());
            }
            existing.setDefaultOrgId(dto.getDefaultOrgId());
            existing.setUpdateBy(username);
            existing.setUpdateTime(DateUtils.getNowDate());
            configMapper.update(existing);
        }
        return getConfig(tenantId);
    }

    private static String resolveTenantId(String tenantId)
    {
        if (StringUtils.isNotBlank(tenantId))
        {
            return DataTableTenantSupport.requireTenantId(tenantId);
        }
        String key = com.ruoyi.crm.service.support.TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isNotBlank(key))
        {
            return key;
        }
        throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
    }

    private static void applyDefaultsIfNull(LeadConvertConfigVo vo)
    {
        if (vo.getAutoConvertOnFirstVisit() == null)
        {
            vo.setAutoConvertOnFirstVisit(true);
        }
        if (vo.getAutoConvertOnCheckIn() == null)
        {
            vo.setAutoConvertOnCheckIn(true);
        }
        if (vo.getAutoConvertOnRegistration() == null)
        {
            vo.setAutoConvertOnRegistration(false);
        }
        if (vo.getMergeOnDuplicatePhone() == null)
        {
            vo.setMergeOnDuplicatePhone(true);
        }
        if (vo.getContactOwnerMode() == null || vo.getContactOwnerMode().isEmpty())
        {
            vo.setContactOwnerMode(LeadRecyclePolicyDefaults.OWNER_KEEP_LEAD);
        }
    }
}
