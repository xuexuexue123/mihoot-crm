package com.ruoyi.crm.sfexpress;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 丰桥统一接入平台 msgDigest 签名。
 */
public final class SfExpressSignUtil
{
    private SfExpressSignUtil()
    {
    }

    /**
     * msgDigest = Base64( MD5( URLEncode(msgData + timestamp + checkword, UTF-8) ) )
     * 丰桥要求 URLEncode 后十六进制为大写。
     */
    public static String sign(String msgData, String timestamp, String checkword)
    {
        String raw = (msgData != null ? msgData : "") + timestamp + (checkword != null ? checkword : "");
        String encoded = urlEncodeUpperHex(raw, StandardCharsets.UTF_8.name());
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(encoded.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        }
        catch (Exception e)
        {
            throw new RuntimeException("SF Express sign failed", e);
        }
    }

    static String urlEncodeUpperHex(String value, String charset)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);
            String single;
            try
            {
                single = String.valueOf(c);
                String encoded = URLEncoder.encode(single, charset);
                if (single.equals(encoded))
                {
                    sb.append(c);
                }
                else
                {
                    sb.append(encoded.toUpperCase());
                }
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
}
