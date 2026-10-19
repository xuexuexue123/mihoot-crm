package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmAiSkillPlaybook;

/**
 * Playbook Mapper
 */
public interface CrmAiSkillPlaybookMapper
{
    List<CrmAiSkillPlaybook> selectListByTenant(@Param("tenantId") String tenantId);

    CrmAiSkillPlaybook selectByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId);

    CrmAiSkillPlaybook selectByNameVersionAndTenant(@Param("name") String name, @Param("version") Integer version,
            @Param("tenantId") String tenantId);

    int insert(CrmAiSkillPlaybook row);

    int update(CrmAiSkillPlaybook row);

    int deleteByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId);
}
