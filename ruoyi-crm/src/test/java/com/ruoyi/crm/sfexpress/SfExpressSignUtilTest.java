package com.ruoyi.crm.sfexpress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class SfExpressSignUtilTest
{
    @Test
    public void signProducesNonEmptyDigest()
    {
        String digest = SfExpressSignUtil.sign("{\"orderId\":\"test\"}", "1710000000000", "checkword123");
        assertNotNull(digest);
        assertFalse(digest.isEmpty());
    }

    @Test
    public void signIsDeterministic()
    {
        String a = SfExpressSignUtil.sign("msg", "123", "word");
        String b = SfExpressSignUtil.sign("msg", "123", "word");
        assertEquals(a, b);
    }
}
