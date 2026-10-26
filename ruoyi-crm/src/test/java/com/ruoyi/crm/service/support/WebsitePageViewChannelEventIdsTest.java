package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WebsitePageViewChannelEventIdsTest
{
    @Test
    void shortUrl_keepsReadableForm()
    {
        String id = WebsitePageViewChannelEventIds.of("sess-1", "/events", "contact-1");
        assertEquals("pv:sess-1:/events:contact-1", id);
        assertTrue(id.length() <= WebsitePageViewChannelEventIds.MAX_LENGTH);
    }

    @Test
    void longUrl_hashesToFitVarchar128()
    {
        StringBuilder longUrl = new StringBuilder("/events?");
        for (int i = 0; i < 40; i++)
        {
            longUrl.append("utm_content=").append(i).append("&");
        }
        String sessionId = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
        String subjectId = "ffffffff-1111-2222-3333-444444444444";
        String id = WebsitePageViewChannelEventIds.of(sessionId, longUrl.toString(), subjectId);

        assertTrue(id.length() <= WebsitePageViewChannelEventIds.MAX_LENGTH);
        assertTrue(id.startsWith("pv:"));
        assertEquals(67, id.length()); // "pv:" + 64 hex
    }

    @Test
    void sameInputs_sameHash()
    {
        StringBuilder xs = new StringBuilder(200);
        for (int i = 0; i < 200; i++)
        {
            xs.append('x');
        }
        String url = "/path?" + xs;
        String a = WebsitePageViewChannelEventIds.of("s", url, "c");
        String b = WebsitePageViewChannelEventIds.of("s", url, "c");
        assertEquals(a, b);
    }
}
