package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EventRegistrationServiceImplTest
{
    @Test
    void resolveLeadDisplayName_prefersLeadNameOverMobile()
    {
        assertEquals("张三", EventRegistrationServiceImpl.resolveLeadDisplayName("张三", "13800138000"));
    }

    @Test
    void resolveLeadDisplayName_fallsBackToMobile()
    {
        assertEquals("13800138000", EventRegistrationServiceImpl.resolveLeadDisplayName(null, "13800138000"));
    }

    @Test
    void resolveLeadDisplayName_doesNotUseFullName()
    {
        // fullName（机构名）不应作为 display_name；仅 leadName 参与解析
        assertEquals("13822993311", EventRegistrationServiceImpl.resolveLeadDisplayName(null, "13822993311"));
    }
}
