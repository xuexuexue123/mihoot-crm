package com.ruoyi.crm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmAiModelConfig;
import com.ruoyi.crm.domain.dto.AiModelConfigUpsertDto;
import com.ruoyi.crm.domain.vo.AiModelConfigVo;
import com.ruoyi.crm.enums.AiModelProvider;
import com.ruoyi.crm.mapper.CrmAiModelConfigMapper;
import com.ruoyi.crm.mapper.WCredentialMapper;
import com.ruoyi.crm.service.ICrmAiModelConfigService;
import com.ruoyi.crm.service.support.AiModelConfigValidator;

@Service
public class CrmAiModelConfigServiceImpl implements ICrmAiModelConfigService
{
    @Autowired
    private CrmAiModelConfigMapper crmAiModelConfigMapper;

    @Autowired
    private WCredentialMapper wCredentialMapper;

    @Override
    public AiModelConfigVo getCurrent(String tenantId)
    {
        return crmAiModelConfigMapper.selectByTenantId(requireTenantId(tenantId));
    }

    @Override
    public AiModelConfigVo upsert(String tenantId, AiModelConfigUpsertDto dto)
    {
        tenantId = requireTenantId(tenantId);
        AiModelConfigValidator.validateUpsertDto(dto);
        AiModelConfigValidator.validateCredentialForProvider(dto, wCredentialMapper);

        AiModelProvider provider = AiModelProvider.fromKey(dto.getProvider());
        String username = SecurityUtils.getUsername();
        CrmAiModelConfig existing = crmAiModelConfigMapper.selectEntityByTenantIdAny(tenantId);
        if (existing == null)
        {
            insertRow(tenantId, dto, provider, username);
        }
        else
        {
            updateRow(existing, dto, provider, username);
        }
        return crmAiModelConfigMapper.selectByTenantId(tenantId);
    }

    private void insertRow(String tenantId, AiModelConfigUpsertDto dto, AiModelProvider provider, String username)
    {
        CrmAiModelConfig row = new CrmAiModelConfig();
        row.setConfigId(IdUtils.fastUUID());
        row.setTenantId(tenantId);
        row.setProvider(provider.getKey());
        row.setCredentialId(dto.getCredentialId().trim());
        row.setModel(dto.getModel().trim());
        row.setTemperature(AiModelConfigValidator.normalizeTemperature(dto.getTemperature()));
        row.setDefaultTranslationLanguage(dto.getDefaultTranslationLanguage().trim());
        row.setDelFlag("0");
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmAiModelConfigMapper.insertCrmAiModelConfig(row);
    }

    private void updateRow(CrmAiModelConfig existing, AiModelConfigUpsertDto dto, AiModelProvider provider, String username)
    {
        existing.setDelFlag("0");
        existing.setProvider(provider.getKey());
        existing.setCredentialId(dto.getCredentialId().trim());
        existing.setModel(dto.getModel().trim());
        existing.setTemperature(AiModelConfigValidator.normalizeTemperature(dto.getTemperature()));
        existing.setDefaultTranslationLanguage(dto.getDefaultTranslationLanguage().trim());
        existing.setUpdateBy(username);
        existing.setUpdateTime(DateUtils.getNowDate());
        crmAiModelConfigMapper.updateCrmAiModelConfig(existing);
    }

    private String requireTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
        }
        return tenantId.trim();
    }
}
