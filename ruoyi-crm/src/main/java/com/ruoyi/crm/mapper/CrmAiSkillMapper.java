package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmAiSkill;

/**
 * AI Skill Mapper
 */
public interface CrmAiSkillMapper
{
    List<CrmAiSkill> selectList(@Param("tenantId") String tenantId,
            @Param("domain") String domain,
            @Param("skillType") String skillType,
            @Param("trackKey") String trackKey);

    CrmAiSkill selectByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId);

    CrmAiSkill selectByNameAndTenant(@Param("name") String name, @Param("tenantId") String tenantId);

    int insert(CrmAiSkill row);

    int update(CrmAiSkill row);

    int deleteByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId);

    List<CrmAiSkill> selectAllByTenant(@Param("tenantId") String tenantId);
}
