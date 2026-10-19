package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SalesKpiActivityCriteriaTest
{
    @Test
    public void followupAndMetTypes()
    {
        assertTrue(SalesKpiActivityCriteria.isFollowupEvent("followup.completed"));
        assertFalse(SalesKpiActivityCriteria.isFollowupEvent("lead.assigned"));
        assertTrue(SalesKpiActivityCriteria.isMetCustomerEvent("visit.first"));
        assertTrue(SalesKpiActivityCriteria.isMetCustomerEvent("appointment.completed"));
        assertFalse(SalesKpiActivityCriteria.isMetCustomerEvent("appointment.noshow"));
        assertTrue(SalesKpiActivityCriteria.isAssignedExcluded("transfer_customer"));
    }
}
