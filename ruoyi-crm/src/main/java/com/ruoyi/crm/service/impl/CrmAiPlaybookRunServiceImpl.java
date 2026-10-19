package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAiPlaybookRun;
import com.ruoyi.crm.domain.CrmAiSkillPlaybook;
import com.ruoyi.crm.domain.dto.AiPlaybookRunCreateDto;
import com.ruoyi.crm.domain.vo.AiPlaybookRunVo;
import com.ruoyi.crm.mapper.CrmAiPlaybookRunMapper;
import com.ruoyi.crm.mapper.CrmAiSkillPlaybookMapper;
import com.ruoyi.crm.service.ICrmAiPlaybookRunService;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.PlaybookConverter;

@Service
public class CrmAiPlaybookRunServiceImpl implements ICrmAiPlaybookRunService
{
    private static final Set<String> TRIGGER_SOURCES = new HashSet<>(
            Arrays.asList("assistant", "workflow", "api"));

    private static final Set<String> STATUSES = new HashSet<>(
            Arrays.asList("running", "completed", "failed", "partial"));

    @Autowired
    private CrmAiPlaybookRunMapper crmAiPlaybookRunMapper;

    @Autowired
    private CrmAiSkillPlaybookMapper crmAiSkillPlaybookMapper;

    @Override
    public void recordRun(String tenantId, AiPlaybookRunCreateDto dto)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        validateRunDto(dto);

        CrmAiSkillPlaybook playbook = crmAiSkillPlaybookMapper.selectByIdAndTenant(dto.getPlaybookId(), tenantId);
        if (playbook == null)
        {
            throw new ServiceException("Playbook not found", HttpStatus.NOT_FOUND);
        }

        CrmAiPlaybookRun row = new CrmAiPlaybookRun();
        row.setTenantId(tenantId);
        row.setPlaybookId(dto.getPlaybookId());
        row.setPlaybookVersion(dto.getPlaybookVersion() != null ? dto.getPlaybookVersion() : playbook.getVersion());
        row.setSubjectType(dto.getSubjectType());
        row.setSubjectId(dto.getSubjectId());
        row.setTriggerSource(dto.getTriggerSource().trim());
        row.setStatus(dto.getStatus().trim());
        row.setContextJson(PlaybookConverter.toJsonString(dto.getContextJson()));
        row.setFinalOutput(PlaybookConverter.toJsonString(dto.getFinalOutput()));
        row.setErrorMessage(dto.getErrorMessage());
        row.setStartedAt(DateUtils.getNowDate());
        if (isTerminalStatus(row.getStatus()))
        {
            row.setFinishedAt(DateUtils.getNowDate());
        }
        row.setCreateBy(SecurityUtils.getUsername());
        row.setCreateTime(DateUtils.getNowDate());
        crmAiPlaybookRunMapper.insert(row);
    }

    @Override
    public List<AiPlaybookRunVo> listRuns(String tenantId, String subjectType, String subjectId, Long playbookId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        List<CrmAiPlaybookRun> rows = crmAiPlaybookRunMapper.selectList(tenantId, subjectType, subjectId, playbookId);
        List<AiPlaybookRunVo> result = new ArrayList<>();
        for (CrmAiPlaybookRun row : rows)
        {
            result.add(PlaybookConverter.toRunVo(row));
        }
        return result;
    }

    private void validateRunDto(AiPlaybookRunCreateDto dto)
    {
        if (dto == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (dto.getPlaybookId() == null)
        {
            throw new ServiceException("playbookId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(dto.getTriggerSource()) || !TRIGGER_SOURCES.contains(dto.getTriggerSource().trim()))
        {
            throw new ServiceException("triggerSource 无效", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(dto.getStatus()) || !STATUSES.contains(dto.getStatus().trim()))
        {
            throw new ServiceException("status 无效", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isTerminalStatus(String status)
    {
        return "completed".equals(status) || "failed".equals(status) || "partial".equals(status);
    }
}
