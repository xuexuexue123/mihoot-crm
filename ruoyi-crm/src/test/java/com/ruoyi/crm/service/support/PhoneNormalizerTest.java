package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class PhoneNormalizerTest
{
    @Test
    public void normalizesPlainMobile()
    {
        assertEquals("13800138000", PhoneNormalizer.normalize("13800138000"));
    }

    @Test
    public void stripsPlus86Prefix()
    {
        assertEquals("13800138000", PhoneNormalizer.normalize("+8613800138000"));
    }

    @Test
    public void stripsSpacesAndDashes()
    {
        assertEquals("13800138000", PhoneNormalizer.normalize("138 0013-8000"));
    }

    @Test
    public void rejectsInvalidNumbers()
    {
        assertNull(PhoneNormalizer.normalize("12345"));
        assertNull(PhoneNormalizer.normalize(null));
        assertNull(PhoneNormalizer.normalize(""));
    }
}
