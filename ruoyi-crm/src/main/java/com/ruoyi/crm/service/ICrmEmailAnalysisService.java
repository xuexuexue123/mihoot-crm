package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.EmailAnalysisUpsertDto;
import com.ruoyi.crm.domain.vo.EmailAnalysisVo;

/**
 * 邮件 AI 分析 Service
 */
public interface ICrmEmailAnalysisService
{
    EmailAnalysisVo getLatest(String tenantId, String messageExternalId, String locale,
            String provider, String externalName, String bodyHash);

    EmailAnalysisVo upsert(String tenantId, EmailAnalysisUpsertDto dto);

    void remove(String tenantId, String analysisId);
}
