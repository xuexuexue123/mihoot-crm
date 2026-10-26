package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class IdentityNormalizerWhatsappTest
{
    @Test
    void whatsappPhone_usesMobileNormalization()
    {
        assertEquals("13800138001",
                IdentityNormalizer.normalize(IdentityNormalizer.TYPE_WHATSAPP_PHONE, "8613800138001"));
        assertEquals("13800138001",
                IdentityNormalizer.normalize(IdentityNormalizer.TYPE_MOBILE_PHONE, "+86 138-0013-8001"));
    }

    @Test
    void whatsappWaId_onlyTrims()
    {
        assertEquals("8613800138001",
                IdentityNormalizer.normalize(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, " 8613800138001 "));
    }

    @Test
    void nullValue_returnsNull()
    {
        assertNull(IdentityNormalizer.normalize(IdentityNormalizer.TYPE_WHATSAPP_PHONE, null));
    }
}
