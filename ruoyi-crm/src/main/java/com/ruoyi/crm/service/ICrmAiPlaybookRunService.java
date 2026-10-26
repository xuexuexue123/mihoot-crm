package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.AiPlaybookRunCreateDto;
import com.ruoyi.crm.domain.vo.AiPlaybookRunVo;

/**
 * Playbook 执行审计服务
 */
public interface ICrmAiPlaybookRunService
{
    void recordRun(String tenantId, AiPlaybookRunCreateDto dto);

    List<AiPlaybookRunVo> listRuns(String tenantId, String subjectType, String subjectId, Long playbookId);
}
