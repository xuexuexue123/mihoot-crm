package com.ruoyi.crm.inbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.support.InboxChannelPathRules;

class InboxChannelPathRulesTest
{
    @Test
    void skipsSettingsAndConfigPaths()
    {
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/inbox/channels").isPresent());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/whatsapp/config").isPresent());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/messenger/webhook").isPresent());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/webchat/public/widget.js").isPresent());
    }

    @Test
    void gatesWhatsappInboxRoutes()
    {
        assertEquals(InboxChannel.WHATSAPP,
                InboxChannelPathRules.resolveGatedChannel("/crm/whatsapp/conversations").get());
        assertEquals(InboxChannel.WHATSAPP,
                InboxChannelPathRules.resolveGatedChannel("/crm/whatsapp/inbox/events").get());
        assertEquals(InboxChannel.WHATSAPP,
                InboxChannelPathRules.resolveGatedChannel("/crm/whatsapp/send").get());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/whatsapp/templates").isPresent());
    }

    @Test
    void gatesWechatMpInboxOnly()
    {
        assertEquals(InboxChannel.WECHAT_MP,
                InboxChannelPathRules.resolveGatedChannel("/crm/wechat_mp/conversations").get());
        assertEquals(InboxChannel.WECHAT_MP,
                InboxChannelPathRules.resolveGatedChannel("/crm/wechat_mp/messages/reply").get());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/wechat_mp/mass/send").isPresent());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/wechat_mp/menu").isPresent());
    }

    @Test
    void gatesWecomKfInboxOnly()
    {
        assertEquals(InboxChannel.WECOM_KF,
                InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/conversations").get());
        assertEquals(InboxChannel.WECOM_KF,
                InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/conversations/1/send").get());
        assertEquals(InboxChannel.WECOM_KF,
                InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/contact/link").get());
        assertEquals(InboxChannel.WECOM_KF,
                InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/inbox/events").get());
        assertEquals(InboxChannel.WECOM_KF,
                InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/messages/media/abc").get());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/config").isPresent());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/send_msg").isPresent());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/messages").isPresent());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/messages/by-date").isPresent());
        assertFalse(InboxChannelPathRules.resolveGatedChannel("/crm/wecom/kf/message/reply").isPresent());
    }
}
