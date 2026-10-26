package com.ruoyi.crm.service.recall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmRecallPolicy;

public class RecallPolicyMatcherTest
{
    @Test
    public void implantCodeMatchesFirstSpecificPolicy()
    {
        CrmRecallPolicy implant = policy("sys-recall-implant", 10, "{\"treatmentItemCodes\":[\"implant\"]}");
        CrmRecallPolicy fallback = policy("sys-recall-default", 100, "{\"treatmentItemCodes\":[]}");
        CrmRecallPolicy matched = RecallPolicyMatcher.match(Arrays.asList(implant, fallback), "implant");
        assertEquals("sys-recall-implant", matched.getPolicyId());
    }

    @Test
    public void chineseAliasNormalizesToImplant()
    {
        CrmRecallPolicy implant = policy("sys-recall-implant", 10, "{\"treatmentItemCodes\":[\"implant\"]}");
        CrmRecallPolicy fallback = policy("sys-recall-default", 100, "{\"treatmentItemCodes\":[]}");
        assertEquals("sys-recall-implant",
                RecallPolicyMatcher.match(Arrays.asList(implant, fallback), "种植牙").getPolicyId());
        assertEquals("sys-recall-implant",
                RecallPolicyMatcher.match(Arrays.asList(implant, fallback), " 种植 ").getPolicyId());
    }

    @Test
    public void unknownCodeFallsToCatchAll()
    {
        CrmRecallPolicy implant = policy("sys-recall-implant", 10, "{\"treatmentItemCodes\":[\"implant\"]}");
        CrmRecallPolicy fallback = policy("sys-recall-default", 100, "{\"treatmentItemCodes\":[]}");
        CrmRecallPolicy matched = RecallPolicyMatcher.match(Arrays.asList(implant, fallback), "not_a_real_code");
        assertEquals("sys-recall-default", matched.getPolicyId());
        assertTrue(RecallPolicyMatcher.isCatchAll(fallback));
    }

    @Test
    public void orthoFallsToCatchAllWithoutDedicatedPolicy()
    {
        CrmRecallPolicy implant = policy("sys-recall-implant", 10, "{\"treatmentItemCodes\":[\"implant\"]}");
        CrmRecallPolicy fallback = policy("sys-recall-default", 100, "{\"treatmentItemCodes\":[]}");
        assertEquals("sys-recall-default",
                RecallPolicyMatcher.match(Arrays.asList(implant, fallback), "ortho").getPolicyId());
        assertEquals("sys-recall-default",
                RecallPolicyMatcher.match(Arrays.asList(implant, fallback), "正畸").getPolicyId());
    }

    @Test
    public void blankCodeFallsToCatchAll()
    {
        CrmRecallPolicy implant = policy("sys-recall-implant", 10, "{\"treatmentItemCodes\":[\"implant\"]}");
        CrmRecallPolicy fallback = policy("sys-recall-default", 100, "{}");
        assertEquals("sys-recall-default", RecallPolicyMatcher.match(Arrays.asList(implant, fallback), " ").getPolicyId());
        assertEquals("sys-recall-default", RecallPolicyMatcher.match(Arrays.asList(implant, fallback), null).getPolicyId());
    }

    @Test
    public void trimExactMatch()
    {
        CrmRecallPolicy implant = policy("sys-recall-implant", 10, "{\"treatmentItemCodes\":[\"implant\"]}");
        assertEquals("sys-recall-implant", RecallPolicyMatcher.match(Collections.singletonList(implant), " implant ").getPolicyId());
    }

    @Test
    public void emptyListReturnsNull()
    {
        assertNull(RecallPolicyMatcher.match(Collections.emptyList(), "implant"));
    }

    private static CrmRecallPolicy policy(String id, int priority, String matchJson)
    {
        CrmRecallPolicy row = new CrmRecallPolicy();
        row.setPolicyId(id);
        row.setPriority(priority);
        row.setMatchJson(matchJson);
        return row;
    }
}
