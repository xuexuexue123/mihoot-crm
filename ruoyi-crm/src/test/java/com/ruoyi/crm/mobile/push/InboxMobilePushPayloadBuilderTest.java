package com.ruoyi.crm.mobile.push;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InboxMobilePushPayloadBuilderTest
{
    @Test
    void buildTitle_masksPhoneLikePeerName()
    {
        String title = InboxMobilePushPayloadBuilder.buildTitle(
                InboxMobilePushRequest.CHANNEL_WHATSAPP, "+86 13800138000");
        assertEquals("WhatsApp · ****8000", title);
    }

    @Test
    void buildBody_truncatesLongText()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 120; i++)
        {
            sb.append('a');
        }
        String body = InboxMobilePushPayloadBuilder.buildBody("text", sb.toString());
        assertTrue(body.length() <= 100);
        assertTrue(body.endsWith("…"));
    }

    @Test
    void buildBody_usesPlaceholderForImage()
    {
        assertEquals("[Image]", InboxMobilePushPayloadBuilder.buildBody("image", null));
    }

    @Test
    void maskPhone_shortDigits_returnsVisitor()
    {
        assertEquals("访客", InboxMobilePushPayloadBuilder.maskPhone("123"));
    }
}
