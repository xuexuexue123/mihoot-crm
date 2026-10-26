package com.ruoyi.crm.support;

import java.util.Optional;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.enums.InboxChannel;

/**
 * Maps request URI to an Inbox channel when agent-facing APIs should be gated.
 */
public final class InboxChannelPathRules
{
    private InboxChannelPathRules()
    {
    }

    /**
     * @return channel to enforce when URI is an agent-facing Inbox API; empty when no gate applies
     */
    public static Optional<InboxChannel> resolveGatedChannel(String requestUri)
    {
        if (StringUtils.isBlank(requestUri))
        {
            return Optional.empty();
        }
        String uri = normalize(requestUri);
        if ("/crm/inbox/channels".equals(uri))
        {
            return Optional.empty();
        }
        Optional<InboxChannel> whatsapp = matchWhatsapp(uri);
        if (whatsapp.isPresent())
        {
            return whatsapp;
        }
        Optional<InboxChannel> messenger = matchMessenger(uri);
        if (messenger.isPresent())
        {
            return messenger;
        }
        Optional<InboxChannel> webchat = matchWebchat(uri);
        if (webchat.isPresent())
        {
            return webchat;
        }
        Optional<InboxChannel> wechatMp = matchWechatMp(uri);
        if (wechatMp.isPresent())
        {
            return wechatMp;
        }
        return matchWecomKf(uri);
    }

    private static Optional<InboxChannel> matchWhatsapp(String uri)
    {
        if (!startsWithChannelPrefix(uri, "/crm/whatsapp/", "/whatsapp/"))
        {
            return Optional.empty();
        }
        if (isWhatsappExcluded(uri))
        {
            return Optional.empty();
        }
        return Optional.of(InboxChannel.WHATSAPP);
    }

    private static Optional<InboxChannel> matchMessenger(String uri)
    {
        if (!startsWithChannelPrefix(uri, "/crm/messenger/", "/messenger/"))
        {
            return Optional.empty();
        }
        if (isMessengerExcluded(uri))
        {
            return Optional.empty();
        }
        return Optional.of(InboxChannel.MESSENGER);
    }

    private static Optional<InboxChannel> matchWebchat(String uri)
    {
        if (!startsWithChannelPrefix(uri, "/crm/webchat/", "/webchat/"))
        {
            return Optional.empty();
        }
        if (uri.contains("/config") || uri.contains("/public/"))
        {
            return Optional.empty();
        }
        return Optional.of(InboxChannel.WEBCHAT);
    }

    private static Optional<InboxChannel> matchWechatMp(String uri)
    {
        if (!startsWithChannelPrefix(uri, "/crm/wechat_mp/", "/wechat_mp/"))
        {
            return Optional.empty();
        }
        if (isWechatMpExcluded(uri))
        {
            return Optional.empty();
        }
        if (isWechatMpInbox(uri))
        {
            return Optional.of(InboxChannel.WECHAT_MP);
        }
        return Optional.empty();
    }

    static boolean isWhatsappExcluded(String uri)
    {
        return uri.contains("/config")
                || uri.contains("/webhook")
                || uri.contains("/automation/")
                || uri.contains("/templates")
                || uri.contains("/template/")
                || uri.contains("/audience/")
                || uri.endsWith("/stats")
                || uri.contains("/links");
    }

    static boolean isMessengerExcluded(String uri)
    {
        return uri.contains("/config")
                || uri.contains("/webhook")
                || uri.contains("/automation/")
                || uri.contains("/templates")
                || uri.contains("/template/")
                || uri.contains("/audience/")
                || uri.endsWith("/stats");
    }

    static boolean isWechatMpExcluded(String uri)
    {
        return uri.contains("/config")
                || uri.contains("/callback/")
                || uri.contains("/token/")
                || uri.contains("/mass")
                || uri.contains("/menu")
                || uri.contains("/fans")
                || uri.contains("/fan/")
                || uri.contains("/tags")
                || uri.contains("/tag/")
                || uri.contains("/material")
                || uri.contains("/qrcode")
                || uri.contains("/auto-reply")
                || uri.contains("/autoreply")
                || uri.contains("/stats")
                || uri.contains("/article")
                || uri.contains("/preset")
                || uri.contains("/task");
    }

    static boolean isWechatMpInbox(String uri)
    {
        return uri.contains("/conversations")
                || uri.contains("/inbox/events")
                || uri.contains("/messages/reply")
                || uri.contains("/messages/media")
                || uri.contains("/contact/link");
    }

    private static Optional<InboxChannel> matchWecomKf(String uri)
    {
        if (!startsWithChannelPrefix(uri, "/crm/wecom/kf/", "/wecom/kf/"))
        {
            return Optional.empty();
        }
        if (isWecomKfExcluded(uri))
        {
            return Optional.empty();
        }
        if (isWecomKfInbox(uri))
        {
            return Optional.of(InboxChannel.WECOM_KF);
        }
        return Optional.empty();
    }

    static boolean isWecomKfExcluded(String uri)
    {
        return uri.contains("/config")
                || uri.endsWith("/stats")
                || uri.endsWith("/messages")
                || uri.contains("/messages/by-")
                || uri.contains("/message/reply")
                || uri.contains("/message/reply/");
    }

    static boolean isWecomKfInbox(String uri)
    {
        return uri.contains("/conversations")
                || uri.contains("/inbox/events")
                || uri.contains("/messages/media")
                || uri.contains("/contact/link")
                || uri.contains("/contact/unlink")
                || uri.contains("/contact/sync");
    }

    private static boolean startsWithChannelPrefix(String uri, String... prefixes)
    {
        for (String prefix : prefixes)
        {
            if (uri.startsWith(prefix) || uri.equals(prefix.substring(0, prefix.length() - 1)))
            {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String uri)
    {
        String path = uri.trim();
        int q = path.indexOf('?');
        if (q >= 0)
        {
            path = path.substring(0, q);
        }
        if (path.length() > 1 && path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
