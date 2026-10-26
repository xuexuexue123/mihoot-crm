package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmAiSkillPlaybookStep;

/**
 * Playbook Step Mapper
 */
public interface CrmAiSkillPlaybookStepMapper
{
    List<CrmAiSkillPlaybookStep> selectByPlaybookId(@Param("playbookId") Long playbookId);

    List<CrmAiSkillPlaybookStep> selectByPlaybookIdWithSkillName(@Param("playbookId") Long playbookId);

    int deleteByPlaybookId(@Param("playbookId") Long playbookId);

    int insertBatch(@Param("list") List<CrmAiSkillPlaybookStep> list);

    Long selectFirstStepIdBySkillId(@Param("skillId") Long skillId, @Param("tenantId") String tenantId);

    int countByPlaybookId(@Param("playbookId") Long playbookId);

    List<String> selectDistinctTrackKeys(@Param("playbookId") Long playbookId);
}
