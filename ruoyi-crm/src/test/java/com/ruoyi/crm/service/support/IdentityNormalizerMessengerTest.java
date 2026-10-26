package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class IdentityNormalizerMessengerTest
{
    @Test
    void normalize_messengerPsid_trimsOnly()
    {
        assertEquals("PSID_ABC",
                IdentityNormalizer.normalize(IdentityNormalizer.TYPE_MESSENGER_PSID, "  PSID_ABC  "));
    }

    @Test
    void normalize_messengerPsid_null()
    {
        assertNull(IdentityNormalizer.normalize(IdentityNormalizer.TYPE_MESSENGER_PSID, null));
    }
}
