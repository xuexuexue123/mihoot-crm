package com.ruoyi.crm.whatsapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class WhatsAppMediaResponseSupportTest
{
    @Test
    void isLikelyDocument_byFilename()
    {
        assertTrue(WhatsAppMediaResponseSupport.isLikelyDocument(MediaType.IMAGE_JPEG, "a.pdf"));
        assertFalse(WhatsAppMediaResponseSupport.isLikelyDocument(MediaType.IMAGE_JPEG, "a.jpg"));
    }

    @Test
    void isLikelyDocument_byMime()
    {
        assertTrue(WhatsAppMediaResponseSupport.isLikelyDocument(MediaType.APPLICATION_PDF, null));
        assertFalse(WhatsAppMediaResponseSupport.isLikelyDocument(MediaType.IMAGE_PNG, null));
    }

    @Test
    void sanitizeFilename_stripsPath()
    {
        assertEquals("report.pdf", WhatsAppMediaResponseSupport.sanitizeFilename("../../report.pdf"));
        assertEquals("file", WhatsAppMediaResponseSupport.sanitizeFilename("  \"\"  "));
    }
}
