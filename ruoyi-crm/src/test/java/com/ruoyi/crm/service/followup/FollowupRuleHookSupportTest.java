package com.ruoyi.crm.service.followup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmLead;

public class FollowupRuleHookSupportTest
{
    @Test
    public void relatedTypeMapsEnglishToChinese()
    {
        assertEquals("销售机会", FollowupRuleRelatedTypeMapper.toUiRelatedType("Opportunity"));
        assertEquals("业务线索", FollowupRuleRelatedTypeMapper.toUiRelatedType("Lead"));
        assertEquals("联系人", FollowupRuleRelatedTypeMapper.toUiRelatedType("Contact"));
    }

    @Test
    public void taskTypeMapsEnglishToChinese()
    {
        assertEquals("售后回访", FollowupRuleRelatedTypeMapper.toUiTaskType("After Sales"));
        assertEquals("支持类活动", FollowupRuleRelatedTypeMapper.toUiTaskType("Sales Activity"));
    }

    @Test
    public void noshowStatusDetection()
    {
        assertTrue(FollowupRuleHookSupport.isNoshowStatus("noshow"));
        assertTrue(FollowupRuleHookSupport.isNoshowStatus("未到诊"));
        assertTrue(FollowupRuleHookSupport.isNoshowStatus("爽约"));
        assertFalse(FollowupRuleHookSupport.isNoshowStatus("completed"));
        assertFalse(FollowupRuleHookSupport.isNoshowStatus(null));
    }

    @Test
    public void closedLostDetection()
    {
        assertTrue(FollowupRuleHookSupport.isClosedLost("closed_lost", null));
        assertTrue(FollowupRuleHookSupport.isClosedLost("方案", "lost"));
        assertFalse(FollowupRuleHookSupport.isClosedLost("方案", null));
    }

    @Test
    public void ownerChangedOnlyWhenNewOwnerPresentDiffers()
    {
        CrmLead existing = new CrmLead();
        existing.setOwner("甲");
        existing.setUserId(1L);
        CrmLead updated = new CrmLead();
        updated.setOwner("乙");
        updated.setUserId(2L);
        assertTrue(FollowupRuleHookSupport.ownerChanged(existing, updated));
        assertFalse(FollowupRuleHookSupport.ownerChanged(existing, existing));
    }

    @Test
    public void templateRendersNestedPath()
    {
        com.alibaba.fastjson2.JSONObject payload = new com.alibaba.fastjson2.JSONObject();
        com.alibaba.fastjson2.JSONObject opty = new com.alibaba.fastjson2.JSONObject();
        opty.put("name", "种植方案-A");
        opty.put("optyId", "opty-1");
        payload.put("opportunity", opty);
        String rendered = FollowupRuleTemplateRenderer.render("赢单回访：{{opportunity.name}}", payload, "opty-1");
        assertEquals("赢单回访：种植方案-A", rendered);
    }
}
