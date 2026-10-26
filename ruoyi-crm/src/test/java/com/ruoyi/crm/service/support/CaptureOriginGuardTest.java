package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class CaptureOriginGuardTest
{
    @Test
    void serializeOrigins_nullOrEmpty_returnsNull()
    {
        assertNull(CaptureOriginGuard.serializeOrigins(null));
        assertNull(CaptureOriginGuard.serializeOrigins(Collections.emptyList()));
        assertNull(CaptureOriginGuard.serializeOrigins(Arrays.asList("  ", "")));
    }

    @Test
    void isOriginAllowed_emptyWhitelist_allowsAny()
    {
        assertTrue(CaptureOriginGuard.isOriginAllowed(null, null));
        assertTrue(CaptureOriginGuard.isOriginAllowed("[]", "https://evil.example"));
        assertTrue(CaptureOriginGuard.isOriginAllowed(
                CaptureOriginGuard.serializeOrigins(Collections.emptyList()), "https://a.com"));
    }

    @Test
    void isOriginAllowed_whitelist_requiresExactMatch()
    {
        String json = CaptureOriginGuard.serializeOrigins(Arrays.asList("https://app.example.com"));
        assertTrue(CaptureOriginGuard.isOriginAllowed(json, "https://app.example.com"));
        assertFalse(CaptureOriginGuard.isOriginAllowed(json, "https://other.example.com"));
        assertFalse(CaptureOriginGuard.isOriginAllowed(json, null));
    }
}
