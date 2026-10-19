package com.ruoyi.crm.wecom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * 企微 {@code externalcontact/get} raw_json 入库前简单脱敏（手机号）。
 */
public final class WecomRawJsonSanitizer
{
    private static final Pattern MOBILE_PATTERN = Pattern.compile("1[3-9]\\d{9}");

    private WecomRawJsonSanitizer()
    {
    }

    public static String sanitize(String rawJson)
    {
        if (!StringUtils.hasText(rawJson))
        {
            return rawJson;
        }
        Matcher matcher = MOBILE_PATTERN.matcher(rawJson);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            matcher.appendReplacement(sb, maskMobile(matcher.group()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String maskMobile(String mobile)
    {
        if (mobile == null || mobile.length() < 7)
        {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }
}
