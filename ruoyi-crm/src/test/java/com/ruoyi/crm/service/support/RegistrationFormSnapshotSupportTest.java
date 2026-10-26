package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOutreachlead;

class RegistrationFormSnapshotSupportTest
{
    @Test
    void fromSubmission_includesStandardAndCustomFields()
    {
        CrmOutreachlead submission = new CrmOutreachlead();
        submission.setLeadName("陈大发");
        submission.setMobilePhone("13922009933");
        submission.setWorkEmail("chen@soul.vn");
        submission.setFullName("Soul");
        submission.setShortDescription("热情");
        submission.setCustomField("[{\"fieldName\":\"interest\",\"value\":\"种植\"}]");

        RegistrationFormSnapshotSupport.Snapshot snapshot =
                RegistrationFormSnapshotSupport.fromSubmission(submission);

        assertNotNull(snapshot.getFormDataJson());
        assertTrue(snapshot.getFormDataJson().contains("陈大发"));
        assertTrue(snapshot.getFormDataJson().contains("interest"));
        assertEquals("[{\"fieldName\":\"interest\",\"value\":\"种植\"}]", snapshot.getCustomField());
    }

    @Test
    void assembleFromLead_mergesCustomFieldIntoFormData()
    {
        CrmLead lead = new CrmLead();
        lead.setLeadName("张三");
        lead.setMobilePhone("13800138000");
        lead.setCustomField("[{\"fieldName\":\"q1\",\"value\":\"A\"}]");

        Map<String, Object> formData = RegistrationFormSnapshotSupport.assembleFromLead(lead);

        assertNotNull(formData);
        assertEquals("张三", formData.get("leadName"));
        assertEquals("A", formData.get("q1"));
    }
}
