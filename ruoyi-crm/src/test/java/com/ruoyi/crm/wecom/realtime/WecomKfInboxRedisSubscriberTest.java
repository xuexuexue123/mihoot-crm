package com.ruoyi.crm.wecom.realtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class WecomKfInboxRedisSubscriberTest
{
    @Test
    void extractTenantId_fromChannel()
    {
        assertEquals("acme", WecomKfInboxRedisSubscriber.extractTenantId("wecom_kf:inbox:acme"));
        assertEquals("tenant-1", WecomKfInboxRedisSubscriber.extractTenantId("wecom_kf:inbox:tenant-1"));
        assertNull(WecomKfInboxRedisSubscriber.extractTenantId("other:channel"));
        assertNull(WecomKfInboxRedisSubscriber.extractTenantId(null));
        assertNull(WecomKfInboxRedisSubscriber.extractTenantId("wecom_kf:inbox:"));
    }
}
