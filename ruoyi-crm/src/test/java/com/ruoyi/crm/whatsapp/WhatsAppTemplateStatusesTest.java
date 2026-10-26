package com.ruoyi.crm.whatsapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WhatsAppTemplateStatusesTest
{
    @Test
    void normalizeAndApprove()
    {
        assertEquals("approved", WhatsAppTemplateStatuses.normalize("APPROVED"));
        assertTrue(WhatsAppTemplateStatuses.isApproved("APPROVED"));
        assertTrue(WhatsAppTemplateStatuses.isApproved("approved"));
        assertFalse(WhatsAppTemplateStatuses.isApproved("pending"));
        assertFalse(WhatsAppTemplateStatuses.isApproved(null));
    }
}
