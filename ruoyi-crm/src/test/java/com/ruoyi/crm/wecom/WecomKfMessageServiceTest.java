package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmWecomKfConfig;
import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.CrmWecomKfMessage;

class WecomKfMessageServiceTest
{
    @Test
    void openKfidAllowed_emptyMeansAll()
    {
        CrmWecomKfConfig cfg = new CrmWecomKfConfig();
        cfg.setOpenKfids(null);
        assertTrue(WecomKfMessageService.isOpenKfidAllowed(cfg, "wk1"));

        cfg.setOpenKfids("[]");
        assertTrue(WecomKfMessageService.isOpenKfidAllowed(cfg, "wk1"));
    }

    @Test
    void openKfidAllowed_filtersConfigured()
    {
        CrmWecomKfConfig cfg = new CrmWecomKfConfig();
        cfg.setOpenKfids("[\"wk1\",\"wk2\"]");
        assertTrue(WecomKfMessageService.isOpenKfidAllowed(cfg, "wk1"));
        assertFalse(WecomKfMessageService.isOpenKfidAllowed(cfg, "wk9"));
    }

    @Test
    void buildPreview_textAndImage()
    {
        JSONObject textMsg = new JSONObject();
        JSONObject text = new JSONObject();
        text.put("content", "hello");
        textMsg.put("text", text);
        assertEquals("hello", WecomKfMessageService.buildPreview("text", textMsg));
        assertEquals("[图片]", WecomKfMessageService.buildPreview("image", new JSONObject()));
    }

    @Test
    void extractMediaId_fromImagePayload()
    {
        JSONObject msg = new JSONObject();
        JSONObject image = new JSONObject();
        image.put("media_id", "MEDIA123");
        image.put("pic_url", "https://example.com/a.jpg");
        msg.put("image", image);

        assertEquals("MEDIA123", WecomKfMessageService.extractMediaId("image", msg));
        assertEquals("https://example.com/a.jpg", WecomKfMessageService.extractPicUrl("image", msg));
        assertNull(WecomKfMessageService.extractMediaId("text", msg));
    }

    @Test
    void extractMediaId_fromFilePayload()
    {
        JSONObject msg = new JSONObject();
        JSONObject file = new JSONObject();
        file.put("media_id", "FILE456");
        msg.put("file", file);
        assertEquals("FILE456", WecomKfMessageService.extractMediaId("file", msg));
        assertNull(WecomKfMessageService.extractPicUrl("file", msg));
    }

    @Test
    void shouldDispatchInboxAgent_passesForTextInbound()
    {
        CrmWecomKfMessage msg = new CrmWecomKfMessage();
        msg.setId(10L);
        CrmWecomKfConversation conv = new CrmWecomKfConversation();
        conv.setId(20L);
        conv.setHostedByAi(null);
        conv.setCanReply(1);
        assertTrue(WecomKfMessageService.shouldDispatchInboxAgent(true, "text", "hi", msg, conv));
        conv.setHostedByAi(Boolean.TRUE);
        assertTrue(WecomKfMessageService.shouldDispatchInboxAgent(true, "text", "hi", msg, conv));
    }

    @Test
    void shouldDispatchInboxAgent_skipsHostedFalseNonTextCanReplyZero()
    {
        CrmWecomKfMessage msg = new CrmWecomKfMessage();
        msg.setId(10L);
        CrmWecomKfConversation conv = new CrmWecomKfConversation();
        conv.setId(20L);

        conv.setHostedByAi(Boolean.FALSE);
        assertFalse(WecomKfMessageService.shouldDispatchInboxAgent(true, "text", "hi", msg, conv));

        conv.setHostedByAi(null);
        assertFalse(WecomKfMessageService.shouldDispatchInboxAgent(true, "image", "hi", msg, conv));
        assertFalse(WecomKfMessageService.shouldDispatchInboxAgent(false, "text", "hi", msg, conv));
        assertFalse(WecomKfMessageService.shouldDispatchInboxAgent(true, "text", "  ", msg, conv));

        conv.setCanReply(0);
        assertFalse(WecomKfMessageService.shouldDispatchInboxAgent(true, "text", "hi", msg, conv));
    }
}
