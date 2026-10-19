package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadConvertCopyOptions;

class LeadToContactFieldMapperTest
{
    private final LeadToContactFieldMapper mapper = new LeadToContactFieldMapper();

    @Test
    void mapNewContact_copiesSidAndWecomFollowUseridWhenCopyAttribution()
    {
        CrmLead lead = new CrmLead();
        lead.setLeadSource("wecom:poster");
        lead.setSid("event-uuid-1");
        lead.setWecomExternalUserid("wm_abc");
        lead.setWecomFollowUserid("staff_1");

        LeadConvertCopyOptions options = LeadConvertCopyOptions.defaults();
        CrmContact contact = mapper.mapNewContact(lead, null, "张三", null, options);

        assertEquals("event-uuid-1", contact.getSid());
        assertEquals("wecom:poster", contact.getOriginalSource());
        assertEquals("wm_abc", contact.getWecomExternalUserid());
        assertEquals("staff_1", contact.getWecomFollowUserid());
    }

    @Test
    void mapMergePatch_doesNotOverwriteExistingSid()
    {
        CrmLead lead = new CrmLead();
        lead.setSid("new-event");
        lead.setLeadSource("wecom:live");

        CrmContact existing = new CrmContact();
        existing.setContactId("c1");
        existing.setSid("existing-event");

        LeadConvertCopyOptions options = LeadConvertCopyOptions.defaults();
        CrmContact patch = mapper.mapMergePatch(lead, existing, options);

        assertNull(patch.getSid());
    }
}
