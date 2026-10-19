package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WecomRawJsonSanitizerTest
{
    @Test
    void masksMobileInJson()
    {
        String raw = "{\"remark_mobiles\":[\"13812345678\"],\"name\":\"test\"}";
        String sanitized = WecomRawJsonSanitizer.sanitize(raw);
        assertTrue(sanitized.contains("138****5678"));
        assertFalse(sanitized.contains("13812345678"));
    }
}
