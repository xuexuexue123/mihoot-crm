package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmLead;

class MpLeadIdentitySupportTest
{
    @Test
    void applyMpOpenIdColumns_setsEntityFields()
    {
        CrmLead lead = new CrmLead();
        MpLeadIdentitySupport.applyMpOpenIdColumns(lead, "oABC", "wx1");
        assertEquals("oABC", lead.getWechatMpOpenid());
        assertEquals("wx1", lead.getWechatMpAppId());
    }

    @Test
    void applyMpOpenIdColumns_skipsBlank()
    {
        CrmLead lead = new CrmLead();
        MpLeadIdentitySupport.applyMpOpenIdColumns(lead, "", "wx1");
        assertNull(lead.getWechatMpOpenid());
    }
}
