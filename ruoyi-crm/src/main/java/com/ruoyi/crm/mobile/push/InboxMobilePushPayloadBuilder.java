package com.ruoyi.crm.mobile.push;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * 移动推送 notification 文案与脱敏。
 */
public final class InboxMobilePushPayloadBuilder
{
    private static final int MAX_BODY_LEN = 100;
    private static final Pattern PHONE_LIKE = Pattern.compile("^\\+?[0-9\\s\\-()]{7,20}$");

    private InboxMobilePushPayloadBuilder()
    {
    }

    public static String channelLabel(String channel)
    {
        if (InboxMobilePushRequest.CHANNEL_WHATSAPP.equals(channel))
        {
            return "WhatsApp";
        }
        if (InboxMobilePushRequest.CHANNEL_MESSENGER.equals(channel))
        {
            return "Messenger";
        }
        if (InboxMobilePushRequest.CHANNEL_WECHAT_MP.equals(channel))
        {
            return "公众号";
        }
        if (InboxMobilePushRequest.CHANNEL_WEBCHAT.equals(channel))
        {
            return "Web Chat";
        }
        return "Inbox";
    }

    public static String buildTitle(String channel, String peerDisplayName)
    {
        String label = channelLabel(channel);
        String peer = sanitizePeerDisplayName(peerDisplayName);
        if (StringUtils.hasText(peer))
        {
            return label + " · " + peer;
        }
        return label;
    }

    public static String buildBody(String messageType, String messageContent)
    {
        String type = messageType != null ? messageType.trim().toLowerCase() : "text";
        if ("text".equals(type) || "chat".equals(type))
        {
            return truncate(stripControls(messageContent), MAX_BODY_LEN);
        }
        return placeholderForType(type);
    }

    public static String sanitizePeerDisplayName(String raw)
    {
        if (!StringUtils.hasText(raw))
        {
            return "";
        }
        String trimmed = stripControls(raw.trim());
        if (PHONE_LIKE.matcher(trimmed.replace(" ", "")).matches())
        {
            return maskPhone(trimmed);
        }
        if (trimmed.length() > 40)
        {
            return trimmed.substring(0, 40) + "…";
        }
        return trimmed;
    }

    public static String maskPhone(String phone)
    {
        if (!StringUtils.hasText(phone))
        {
            return "访客";
        }
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() >= 4)
        {
            return "****" + digits.substring(digits.length() - 4);
        }
        return "访客";
    }

    static String placeholderForType(String type)
    {
        switch (type)
        {
            case "image":
            case "sticker":
                return "[Image]";
            case "audio":
            case "voice":
                return "[Audio]";
            case "video":
                return "[Video]";
            case "document":
            case "file":
                return "[File]";
            case "location":
                return "[Location]";
            case "contacts":
            case "contact":
                return "[Contact]";
            default:
                return "[Message]";
        }
    }

    static String stripControls(String s)
    {
        if (s == null)
        {
            return "";
        }
        return s.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ").trim();
    }

    static String truncate(String s, int max)
    {
        if (s == null || s.isEmpty())
        {
            return "";
        }
        if (s.length() <= max)
        {
            return s;
        }
        return s.substring(0, max - 1) + "…";
    }
}
