package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CrmPublicTenantWebsiteEventServiceImplTest
{
    @Test
    void buildShortDescription_stripsMarkdownAndTruncates()
    {
        StringBuilder longPlain = new StringBuilder();
        for (int i = 0; i < 150; i++)
        {
            longPlain.append('字');
        }
        String md = "# 标题\n\n**加粗** " + longPlain;
        String shortDesc = CrmPublicTenantWebsiteEventServiceImpl.buildShortDescription(md);
        assertEquals(120, shortDesc.length());
        assertNull(CrmPublicTenantWebsiteEventServiceImpl.buildShortDescription(null));
        assertNull(CrmPublicTenantWebsiteEventServiceImpl.buildShortDescription("   "));
        assertEquals("简短介绍", CrmPublicTenantWebsiteEventServiceImpl.buildShortDescription("简短介绍"));
    }
}
