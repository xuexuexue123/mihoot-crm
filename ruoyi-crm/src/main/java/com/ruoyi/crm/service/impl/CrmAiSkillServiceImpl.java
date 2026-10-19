package com.ruoyi.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAiSkill;
import com.ruoyi.crm.domain.dto.AiSkillUpsertDto;
import com.ruoyi.crm.domain.vo.AiSkillVo;
import com.ruoyi.crm.mapper.CrmAiSkillMapper;
import com.ruoyi.crm.mapper.CrmAiSkillPlaybookStepMapper;
import com.ruoyi.crm.mapper.CrmMcpServerMapper;
import com.ruoyi.crm.service.ICrmAiSkillService;
import com.ruoyi.crm.service.support.AiSkillConverter;
import com.ruoyi.crm.service.support.AiSkillValidator;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.WebsiteBuilderSkillSeeder;

@Service
public class CrmAiSkillServiceImpl implements ICrmAiSkillService
{
    @Autowired
    private CrmAiSkillMapper crmAiSkillMapper;

    @Autowired
    private CrmMcpServerMapper crmMcpServerMapper;

    @Autowired
    private CrmAiSkillPlaybookStepMapper crmAiSkillPlaybookStepMapper;

    @Autowired
    private WebsiteBuilderSkillSeeder websiteBuilderSkillSeeder;

    @Override
    public List<AiSkillVo> list(String tenantId, String domain, String skillType, String trackKey)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        List<CrmAiSkill> rows = crmAiSkillMapper.selectList(tenantId, domain, skillType, trackKey);
        return AiSkillConverter.toVoList(rows);
    }

    @Override
    public AiSkillVo upsert(String tenantId, AiSkillUpsertDto dto)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        AiSkillValidator.validateUpsert(dto);
        AiSkillValidator.validateMcpReferences(tenantId, dto, this::isEnabledMcpServer);

        String name = dto.getName().trim();
        CrmAiSkill duplicate = crmAiSkillMapper.selectByNameAndTenant(name, tenantId);
        if (duplicate != null && (dto.getId() == null || !duplicate.getId().equals(dto.getId())))
        {
            throw new ServiceException("Skill name already exists", HttpStatus.CONFLICT);
        }

        String username = SecurityUtils.getUsername();
        CrmAiSkill row = buildRow(tenantId, dto, username);

        if (dto.getId() == null)
        {
            row.setCreateBy(username);
            row.setCreateTime(DateUtils.getNowDate());
            crmAiSkillMapper.insert(row);
        }
        else
        {
            CrmAiSkill existing = crmAiSkillMapper.selectByIdAndTenant(dto.getId(), tenantId);
            if (existing == null)
            {
                throw new ServiceException("Skill not found", HttpStatus.NOT_FOUND);
            }
            row.setId(existing.getId());
            row.setCreateBy(existing.getCreateBy());
            row.setCreateTime(existing.getCreateTime());
            crmAiSkillMapper.update(row);
        }

        return AiSkillConverter.toVo(crmAiSkillMapper.selectByIdAndTenant(row.getId(), tenantId));
    }

    @Override
    public void insertDefaultWebsiteBuilderSkill(String tenantId)
    {
        websiteBuilderSkillSeeder.seedIfAbsent(tenantId);
    }

    @Override
    public void delete(String tenantId, Long id)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        if (id == null)
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmAiSkill existing = crmAiSkillMapper.selectByIdAndTenant(id, tenantId);
        if (existing == null)
        {
            throw new ServiceException("Skill not found", HttpStatus.NOT_FOUND);
        }
        Long stepId = crmAiSkillPlaybookStepMapper.selectFirstStepIdBySkillId(id, tenantId);
        if (stepId != null)
        {
            throw new ServiceException("Skill is referenced by playbook step id=" + stepId, HttpStatus.CONFLICT);
        }
        crmAiSkillMapper.deleteByIdAndTenant(id, tenantId);
    }

    private CrmAiSkill buildRow(String tenantId, AiSkillUpsertDto dto, String username)
    {
        CrmAiSkill row = new CrmAiSkill();
        row.setTenantId(tenantId);
        row.setName(dto.getName().trim());
        row.setDescription(StringUtils.isNotBlank(dto.getDescription()) ? dto.getDescription().trim() : null);
        row.setInstruction(dto.getInstruction().trim());
        row.setTriggerKeywords(AiSkillConverter.toJsonString(dto.getTriggerKeywords()));
        row.setTriggerPatterns(AiSkillConverter.toJsonString(dto.getTriggerPatterns()));
        row.setScope(StringUtils.isNotBlank(dto.getScope()) ? dto.getScope().trim() : "all");
        row.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
        row.setEnabled(Boolean.FALSE.equals(dto.getEnabled()) ? 0 : 1);
        row.setSkillType(StringUtils.isNotBlank(dto.getSkillType()) ? dto.getSkillType().trim() : "prompt");
        row.setDomain(StringUtils.isNotBlank(dto.getDomain()) ? dto.getDomain().trim() : null);
        row.setTrackKey(StringUtils.isNotBlank(dto.getTrackKey()) ? dto.getTrackKey().trim() : null);
        row.setToolBindings(AiSkillConverter.toJsonString(dto.getToolBindings()));
        row.setInputContext(AiSkillConverter.toJsonString(dto.getInputContext()));
        row.setOutputKey(StringUtils.isNotBlank(dto.getOutputKey()) ? dto.getOutputKey().trim() : null);
        row.setOutputSchema(AiSkillConverter.toJsonString(dto.getOutputSchema()));
        row.setMcpServerIds(AiSkillConverter.toJsonString(dto.getMcpServerIds()));
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        return row;
    }

    private boolean isEnabledMcpServer(String tenantId, Long serverId)
    {
        if (serverId == null)
        {
            return false;
        }
        return crmMcpServerMapper.selectEnabledByIdAndTenant(serverId, tenantId) != null;
    }
}
