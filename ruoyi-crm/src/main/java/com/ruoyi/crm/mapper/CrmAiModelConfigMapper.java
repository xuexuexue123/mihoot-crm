package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmAiModelConfig;
import com.ruoyi.crm.domain.vo.AiModelConfigVo;

/**
 * 租户 AI 模型配置 Mapper
 */
public interface CrmAiModelConfigMapper
{
    AiModelConfigVo selectByTenantId(@Param("tenantId") String tenantId);

    CrmAiModelConfig selectEntityByTenantIdAny(@Param("tenantId") String tenantId);

    int insertCrmAiModelConfig(CrmAiModelConfig row);

    int updateCrmAiModelConfig(CrmAiModelConfig row);
}
