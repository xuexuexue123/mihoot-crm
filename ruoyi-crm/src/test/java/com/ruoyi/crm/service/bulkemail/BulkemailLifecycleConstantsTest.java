package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmBulkemail;

public class BulkemailLifecycleConstantsTest
{
    @Test
    public void normalizeLegacyActiveStatus()
    {
        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setStatus(BulkemailLifecycleConstants.STATUS_ACTIVE_LEGACY);
        BulkemailLifecycleConstants.normalizeLegacyStatus(campaign);
        assertEquals(BulkemailLifecycleConstants.STATUS_SENT, campaign.getStatus());
    }
}
