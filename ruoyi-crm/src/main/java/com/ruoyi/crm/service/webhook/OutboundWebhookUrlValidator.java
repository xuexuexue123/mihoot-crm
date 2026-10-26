package com.ruoyi.crm.service.webhook;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * Reject private / loopback / link-local / metadata targets to reduce SSRF risk.
 */
public final class OutboundWebhookUrlValidator
{
    private OutboundWebhookUrlValidator()
    {
    }

    public static URI validateOrThrow(String url, boolean allowHttp)
    {
        String reason = validate(url, allowHttp);
        if (reason != null)
        {
            throw new ServiceException(reason, HttpStatus.BAD_REQUEST);
        }
        try
        {
            return URI.create(url.trim());
        }
        catch (Exception ex)
        {
            throw new ServiceException("url 不合法", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @return null when allowed, otherwise error code / message
     */
    public static String validate(String url, boolean allowHttp)
    {
        if (StringUtils.isBlank(url))
        {
            return "url 不能为空";
        }
        URI uri;
        try
        {
            uri = URI.create(url.trim());
        }
        catch (Exception ex)
        {
            return "url 不合法";
        }
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase();
        if ("https".equals(scheme))
        {
            // ok
        }
        else if ("http".equals(scheme))
        {
            if (!allowHttp)
            {
                return "生产环境仅允许 https://";
            }
        }
        else
        {
            return "url 仅支持 http/https";
        }
        String host = uri.getHost();
        if (StringUtils.isBlank(host))
        {
            return "url 缺少主机名";
        }
        if (isBlockedHostName(host))
        {
            return OutboundWebhookConstants.ERROR_SSRF;
        }
        InetAddress[] addresses;
        try
        {
            addresses = InetAddress.getAllByName(host);
        }
        catch (UnknownHostException ex)
        {
            return "url 主机无法解析";
        }
        if (addresses == null || addresses.length == 0)
        {
            return "url 主机无法解析";
        }
        for (InetAddress address : addresses)
        {
            if (isBlockedAddress(address))
            {
                return OutboundWebhookConstants.ERROR_SSRF;
            }
        }
        return null;
    }

    static boolean isBlockedHostName(String host)
    {
        String h = host.trim().toLowerCase();
        if ("localhost".equals(h) || "metadata.google.internal".equals(h)
                || h.endsWith(".internal") || "metadata".equals(h))
        {
            return true;
        }
        return "169.254.169.254".equals(h) || "metadata.google.internal".equals(h);
    }

    static boolean isBlockedAddress(InetAddress address)
    {
        if (address == null)
        {
            return true;
        }
        return address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()
                || isUniqueLocalIPv6(address)
                || isMetadataIpv4(address);
    }

    private static boolean isUniqueLocalIPv6(InetAddress address)
    {
        byte[] bytes = address.getAddress();
        if (bytes == null || bytes.length != 16)
        {
            return false;
        }
        return (bytes[0] & 0xfe) == 0xfc;
    }

    private static boolean isMetadataIpv4(InetAddress address)
    {
        byte[] bytes = address.getAddress();
        if (bytes == null || bytes.length != 4)
        {
            return false;
        }
        int a = bytes[0] & 0xff;
        int b = bytes[1] & 0xff;
        int c = bytes[2] & 0xff;
        int d = bytes[3] & 0xff;
        return a == 169 && b == 254 && c == 169 && d == 254;
    }
}
