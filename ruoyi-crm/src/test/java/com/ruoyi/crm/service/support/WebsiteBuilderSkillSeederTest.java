package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmAiSkill;
import com.ruoyi.crm.mapper.CrmAiSkillMapper;
import com.ruoyi.tenant.website.WebsiteBuilderSkillSeedSupport;

@ExtendWith(MockitoExtension.class)
class WebsiteBuilderSkillSeederTest
{
    @Mock
    private CrmAiSkillMapper crmAiSkillMapper;

    @InjectMocks
    private WebsiteBuilderSkillSeeder seeder;

    @Test
    void seedIfAbsent_insertsWhenMissing()
    {
        when(crmAiSkillMapper.selectByNameAndTenant(WebsiteBuilderSkillSeedSupport.SKILL_NAME, "d33"))
                .thenReturn(null);

        seeder.seedIfAbsent("d33");

        ArgumentCaptor<CrmAiSkill> captor = ArgumentCaptor.forClass(CrmAiSkill.class);
        verify(crmAiSkillMapper).insert(captor.capture());
        CrmAiSkill row = captor.getValue();
        assertEquals("d33", row.getTenantId());
        assertEquals(WebsiteBuilderSkillSeedSupport.SKILL_NAME, row.getName());
        assertEquals(WebsiteBuilderSkillSeedSupport.DOMAIN, row.getDomain());
        assertEquals(Integer.valueOf(100), row.getPriority());
        assertEquals(WebsiteBuilderSkillSeedSupport.DEFAULT_INSTRUCTION, row.getInstruction());
    }

    @Test
    void seedIfAbsent_skipsWhenExists()
    {
        CrmAiSkill existing = new CrmAiSkill();
        existing.setId(1L);
        when(crmAiSkillMapper.selectByNameAndTenant(WebsiteBuilderSkillSeedSupport.SKILL_NAME, "d33"))
                .thenReturn(existing);

        seeder.seedIfAbsent("d33");

        verify(crmAiSkillMapper, never()).insert(any());
    }
}
