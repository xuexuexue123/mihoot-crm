package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmAiPlaybookRun;

/**
 * Playbook Run Mapper
 */
public interface CrmAiPlaybookRunMapper
{
    int insert(CrmAiPlaybookRun row);

    List<CrmAiPlaybookRun> selectList(@Param("tenantId") String tenantId,
            @Param("subjectType") String subjectType,
            @Param("subjectId") String subjectId,
            @Param("playbookId") Long playbookId);
}
