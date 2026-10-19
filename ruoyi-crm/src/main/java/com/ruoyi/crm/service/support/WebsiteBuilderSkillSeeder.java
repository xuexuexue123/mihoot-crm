package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAiSkill;
import com.ruoyi.crm.mapper.CrmAiSkillMapper;
import com.ruoyi.tenant.website.WebsiteBuilderSkillSeedSupport;

/**
 * 建站 AI Skill 种子（Spring 上下文，供运行时幂等插入）。
 */
@Component
public class WebsiteBuilderSkillSeeder
{
    @Autowired
    private CrmAiSkillMapper crmAiSkillMapper;

    /**
     * 若租户尚无 website-builder Skill，则插入默认记录。
     */
    public void seedIfAbsent(String tenantId)
    {
        tenantId = DataTableTenantSupport.requireTenantId(tenantId);
        CrmAiSkill existing = crmAiSkillMapper.selectByNameAndTenant(
                WebsiteBuilderSkillSeedSupport.SKILL_NAME, tenantId);
        if (existing != null)
        {
            return;
        }
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(username))
        {
            username = "system";
        }
        CrmAiSkill row = new CrmAiSkill();
        row.setTenantId(tenantId);
        row.setName(WebsiteBuilderSkillSeedSupport.SKILL_NAME);
        row.setDescription(WebsiteBuilderSkillSeedSupport.DESCRIPTION);
        row.setInstruction(WebsiteBuilderSkillSeedSupport.DEFAULT_INSTRUCTION);
        row.setTriggerKeywords(WebsiteBuilderSkillSeedSupport.TRIGGER_KEYWORDS);
        row.setScope("all");
        row.setPriority(100);
        row.setEnabled(1);
        row.setSkillType("prompt");
        row.setDomain(WebsiteBuilderSkillSeedSupport.DOMAIN);
        row.setCreateBy(username);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy(username);
        row.setUpdateTime(DateUtils.getNowDate());
        crmAiSkillMapper.insert(row);
    }
}
