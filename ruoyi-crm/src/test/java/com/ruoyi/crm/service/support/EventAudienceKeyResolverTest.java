package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.dto.AudienceKeyHints;

class EventAudienceKeyResolverTest
{
    private final EventAudienceKeyResolver resolver = new EventAudienceKeyResolver();

    @Test
    void resolve_contactIdFirst()
    {
        String key = resolver.resolve(AudienceKeyHints.create()
                .contactId("c1")
                .mobilePhone("13800138000")
                .leadId("l1"));
        assertEquals("contact:c1", key);
    }

    @Test
    void resolve_mobileBeforeLead()
    {
        String key = resolver.resolve(AudienceKeyHints.create()
                .mobilePhone("13800138000")
                .leadId("l1"));
        assertEquals("mobile:13800138000", key);
    }

    @Test
    void resolve_unionidBeforeH5()
    {
        String key = resolver.resolve(AudienceKeyHints.create()
                .unionid("u1")
                .leadId("l1"));
        assertEquals("unionid:u1", key);
    }

    @Test
    void resolve_h5BeforeWecom()
    {
        String key = resolver.resolve(AudienceKeyHints.create()
                .leadId("l1")
                .wecomExternalUserid("wm1"));
        assertEquals("h5:l1", key);
    }

    @Test
    void resolve_wecomBeforeMp()
    {
        String key = resolver.resolve(AudienceKeyHints.create()
                .wecomExternalUserid("wm1")
                .wechatMpOpenid("o1"));
        assertEquals("wecom:wm1", key);
    }

    @Test
    void resolve_mpLast()
    {
        String key = resolver.resolve(AudienceKeyHints.create().wechatMpOpenid("o1"));
        assertEquals("mp:o1", key);
    }
}
