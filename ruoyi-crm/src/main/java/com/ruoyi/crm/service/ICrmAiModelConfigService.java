package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.AiModelConfigUpsertDto;
import com.ruoyi.crm.domain.vo.AiModelConfigVo;

/**
 * 租户 AI 模型配置 Service
 */
public interface ICrmAiModelConfigService
{
    AiModelConfigVo getCurrent(String tenantId);

    AiModelConfigVo upsert(String tenantId, AiModelConfigUpsertDto dto);
}
