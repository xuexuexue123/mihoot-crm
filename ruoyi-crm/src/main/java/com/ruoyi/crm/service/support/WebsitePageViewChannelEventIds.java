package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * website.page.view 的 channel_event_id 生成。
 * crm_activity.channel_event_id 为 varchar(128)；长 pageUrl 时对完整键做 SHA-256。
 */
public final class WebsitePageViewChannelEventIds
{
    public static final int MAX_LENGTH = 128;

    private WebsitePageViewChannelEventIds()
    {
    }

    /**
     * 形如 {@code pv:{sessionId}:{pageUrl}:{subjectId}}；超长则 {@code pv:{sha256Hex}}。
     */
    public static String of(String sessionId, String pageUrl, String subjectId)
    {
        String raw = "pv:"
                + nullToEmpty(sessionId) + ":"
                + nullToEmpty(pageUrl) + ":"
                + nullToEmpty(subjectId);
        if (raw.length() <= MAX_LENGTH)
        {
            return raw;
        }
        return "pv:" + sha256Hex(raw);
    }

    private static String nullToEmpty(String value)
    {
        return value == null ? "" : value;
    }

    private static String sha256Hex(String value)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }
}
