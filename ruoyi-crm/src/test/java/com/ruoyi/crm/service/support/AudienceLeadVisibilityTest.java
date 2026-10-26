package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AudienceLeadVisibilityTest
{
    @Test
    public void detectsInitialStatusCaseInsensitive()
    {
        assertTrue(AudienceLeadVisibility.isInitialStatus("Initial"));
        assertTrue(AudienceLeadVisibility.isInitialStatus(" initial "));
        assertFalse(AudienceLeadVisibility.isInitialStatus("Converted"));
    }

    @Test
    public void pendingListIncludesOpenAndAssigned()
    {
        assertTrue(AudienceLeadVisibility.isVisibleInPendingList("Assigned"));
        assertTrue(AudienceLeadVisibility.isVisibleInPendingList(" assigned "));
        assertTrue(AudienceLeadVisibility.isVisibleInPendingList("Open"));
        assertTrue(AudienceLeadVisibility.isVisibleInPendingList(" open "));
        assertFalse(AudienceLeadVisibility.isVisibleInPendingList("converted"));
        assertFalse(AudienceLeadVisibility.isVisibleInPendingList("Converted"));
        assertFalse(AudienceLeadVisibility.isVisibleInPendingList("Initial"));
        assertFalse(AudienceLeadVisibility.isVisibleInPendingList(null));
        assertFalse(AudienceLeadVisibility.isVisibleInPendingList(""));
    }
}
