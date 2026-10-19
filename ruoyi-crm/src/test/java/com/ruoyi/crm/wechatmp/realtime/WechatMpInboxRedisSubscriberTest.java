package com.ruoyi.crm.wechatmp.realtime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WechatMpInboxRedisSubscriberTest
{
    @Test
    void extractTenantId()
    {
        assertEquals("acme", WechatMpInboxRedisSubscriber.extractTenantId("wechat_mp:inbox:acme"));
        assertEquals(null, WechatMpInboxRedisSubscriber.extractTenantId("whatsapp:inbox:acme"));
        assertEquals(null, WechatMpInboxRedisSubscriber.extractTenantId(null));
    }
}
