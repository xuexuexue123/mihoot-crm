package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmEmailAnalysis;

/**
 * 邮件 AI 分析 Mapper
 */
public interface CrmEmailAnalysisMapper
{
    CrmEmailAnalysis selectLatestByKey(@Param("tenantId") String tenantId,
            @Param("messageExternalId") String messageExternalId,
            @Param("locale") String locale,
            @Param("provider") String provider,
            @Param("externalName") String externalName);

    int upsert(CrmEmailAnalysis row);

    int logicalDeleteById(@Param("analysisId") String analysisId,
            @Param("tenantId") String tenantId,
            @Param("updateBy") String updateBy);
}
