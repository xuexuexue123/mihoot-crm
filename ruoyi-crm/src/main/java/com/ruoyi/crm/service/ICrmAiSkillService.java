package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.AiSkillUpsertDto;
import com.ruoyi.crm.domain.vo.AiSkillVo;

/**
 * AI Skill 服务
 */
public interface ICrmAiSkillService
{
    List<AiSkillVo> list(String tenantId, String domain, String skillType, String trackKey);

    AiSkillVo upsert(String tenantId, AiSkillUpsertDto dto);

    void delete(String tenantId, Long id);

    /**
     * 插入默认建站 Skill（website-builder），已存在则跳过。
     */
    void insertDefaultWebsiteBuilderSkill(String tenantId);
}
