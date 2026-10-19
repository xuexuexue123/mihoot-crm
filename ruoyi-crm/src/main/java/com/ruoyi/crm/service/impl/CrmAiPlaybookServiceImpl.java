package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAiSkill;
import com.ruoyi.crm.domain.CrmAiSkillPlaybook;
import com.ruoyi.crm.domain.CrmAiSkillPlaybookStep;
import com.ruoyi.crm.domain.dto.AiPlaybookStepDto;
import com.ruoyi.crm.domain.dto.AiPlaybookUpsertDto;
import com.ruoyi.crm.domain.vo.AiPlaybookDetailVo;
import com.ruoyi.crm.domain.vo.AiPlaybookListVo;
import com.ruoyi.crm.mapper.CrmAiSkillMapper;
import com.ruoyi.crm.mapper.CrmAiSkillPlaybookMapper;
import com.ruoyi.crm.mapper.CrmAiSkillPlaybookStepMapper;
import com.ruoyi.crm.service.ICrmAiPlaybookService;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.PlaybookConverter;
import com.ruoyi.crm.service.support.PlaybookDagValidator;

@Service
public class CrmAiPlaybookServiceImpl implements ICrmAiPlaybookService
{
    @Autowired
    private CrmAiSkillPlaybookMapper crmAiSkillPlaybookMapper;

    @Autowired
    private CrmAiSkillPlaybookStepMapper crmAiSkillPlaybookStepMapper;

    @Autowired
    private CrmAiSkillMapper crmAiSkillMapper;

    @Override
    public List<AiPlaybookListVo> list(String tenantId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        List<CrmAiSkillPlaybook> rows = crmAiSkillPlaybookMapper.selectListByTenant(tenantId);
        List<AiPlaybookListVo> result = new ArrayList<>();
        for (CrmAiSkillPlaybook row : rows)
        {
            int stepCount = crmAiSkillPlaybookStepMapper.countByPlaybookId(row.getId());
            List<String> trackKeys = crmAiSkillPlaybookStepMapper.selectDistinctTrackKeys(row.getId());
            result.add(PlaybookConverter.toListVo(row, stepCount, trackKeys));
        }
        return result;
    }

    @Override
    public AiPlaybookDetailVo getDetail(String tenantId, Long id)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        CrmAiSkillPlaybook row = requirePlaybook(id, tenantId);
        List<CrmAiSkillPlaybookStep> steps = crmAiSkillPlaybookStepMapper.selectByPlaybookIdWithSkillName(row.getId());
        return PlaybookConverter.toDetailVo(row, steps);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiPlaybookDetailVo upsert(String tenantId, AiPlaybookUpsertDto dto)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        validateUpsert(dto);
        PlaybookDagValidator.validateSteps(dto.getSteps());
        validateStepSkills(tenantId, dto.getSteps());

        int version = dto.getVersion() != null ? dto.getVersion() : 1;
        String name = dto.getName().trim();
        CrmAiSkillPlaybook duplicate = crmAiSkillPlaybookMapper.selectByNameVersionAndTenant(name, version, tenantId);
        if (duplicate != null && (dto.getId() == null || !duplicate.getId().equals(dto.getId())))
        {
            throw new ServiceException("Playbook name+version already exists", HttpStatus.CONFLICT);
        }

        String username = SecurityUtils.getUsername();
        CrmAiSkillPlaybook existing = dto.getId() != null ? requirePlaybook(dto.getId(), tenantId) : null;
        CrmAiSkillPlaybook row = buildPlaybookRow(tenantId, dto, version, name, username);

        if (existing == null)
        {
            row.setCreateBy(username);
            row.setCreateTime(DateUtils.getNowDate());
            crmAiSkillPlaybookMapper.insert(row);
        }
        else
        {
            row.setId(existing.getId());
            row.setCreateBy(existing.getCreateBy());
            row.setCreateTime(existing.getCreateTime());
            crmAiSkillPlaybookMapper.update(row);
            crmAiSkillPlaybookStepMapper.deleteByPlaybookId(row.getId());
        }

        List<CrmAiSkillPlaybookStep> stepRows = buildStepRows(row.getId(), dto.getSteps());
        if (!stepRows.isEmpty())
        {
            crmAiSkillPlaybookStepMapper.insertBatch(stepRows);
        }

        return getDetail(tenantId, row.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String tenantId, Long id)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        requirePlaybook(id, tenantId);
        crmAiSkillPlaybookStepMapper.deleteByPlaybookId(id);
        crmAiSkillPlaybookMapper.deleteByIdAndTenant(id, tenantId);
    }

    private CrmAiSkillPlaybook requirePlaybook(Long id, String tenantId)
    {
        if (id == null)
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmAiSkillPlaybook row = crmAiSkillPlaybookMapper.selectByIdAndTenant(id, tenantId);
        if (row == null)
        {
            throw new ServiceException("Playbook not found", HttpStatus.NOT_FOUND);
        }
        return row;
    }

    private void validateUpsert(AiPlaybookUpsertDto dto)
    {
        if (dto == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(dto.getName()))
        {
            throw new ServiceException("name 不能为空", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateStepSkills(String tenantId, List<AiPlaybookStepDto> steps)
    {
        for (AiPlaybookStepDto step : steps)
        {
            if (step.getSkillId() == null)
            {
                throw new ServiceException("skillId 不能为空", HttpStatus.BAD_REQUEST);
            }
            CrmAiSkill skill = crmAiSkillMapper.selectByIdAndTenant(step.getSkillId(), tenantId);
            if (skill == null)
            {
                throw new ServiceException("skill id=" + step.getSkillId() + " 不存在", HttpStatus.BAD_REQUEST);
            }
            if (skill.getEnabled() == null || skill.getEnabled() != 1)
            {
                throw new ServiceException("skill id=" + step.getSkillId() + " 未启用", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private CrmAiSkillPlaybook buildPlaybookRow(String tenantId, AiPlaybookUpsertDto dto, int version, String name,
            String username)
    {
        CrmAiSkillPlaybook row = new CrmAiSkillPlaybook();
        row.setTenantId(tenantId);
        row.setName(name);
        row.setDescription(StringUtils.isNotBlank(dto.getDescription()) ? dto.getDescription().trim() : null);
        row.setTriggerKeywords(PlaybookConverter.toJsonString(dto.getTriggerKeywords()));
        row.setEntrySkillId(dto.getEntrySkillId());
        row.setOutputSchema(PlaybookConverter.toJsonString(dto.getOutputSchema()));
        row.setEnabled(Boolean.FALSE.equals(dto.getEnabled()) ? 0 : 1);
        row.setVersion(version);
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        return row;
    }

    private List<CrmAiSkillPlaybookStep> buildStepRows(Long playbookId, List<AiPlaybookStepDto> steps)
    {
        List<CrmAiSkillPlaybookStep> rows = new ArrayList<>();
        int index = 0;
        for (AiPlaybookStepDto step : steps)
        {
            CrmAiSkillPlaybookStep row = new CrmAiSkillPlaybookStep();
            row.setPlaybookId(playbookId);
            row.setStepKey(step.getStepKey().trim());
            row.setSkillId(step.getSkillId());
            row.setStepType(step.getStepType());
            row.setParallelGroup(StringUtils.isNotBlank(step.getParallelGroup()) ? step.getParallelGroup().trim() : null);
            row.setDependsOn(PlaybookConverter.toJsonString(step.getDependsOn()));
            row.setTrackKey(StringUtils.isNotBlank(step.getTrackKey()) ? step.getTrackKey().trim() : null);
            row.setSortOrder(step.getSortOrder() != null ? step.getSortOrder() : index);
            row.setEnabled(Boolean.FALSE.equals(step.getEnabled()) ? 0 : 1);
            rows.add(row);
            index++;
        }
        return rows;
    }
}
