package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.AiPlaybookUpsertDto;
import com.ruoyi.crm.domain.vo.AiPlaybookDetailVo;
import com.ruoyi.crm.domain.vo.AiPlaybookListVo;

/**
 * Playbook 服务
 */
public interface ICrmAiPlaybookService
{
    List<AiPlaybookListVo> list(String tenantId);

    AiPlaybookDetailVo getDetail(String tenantId, Long id);

    AiPlaybookDetailVo upsert(String tenantId, AiPlaybookUpsertDto dto);

    void delete(String tenantId, Long id);
}
