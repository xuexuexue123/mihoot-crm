package com.ruoyi.crm.service.whatsapp;

import java.security.SecureRandom;

/**
 * Reminder Link 短 token：URL-safe、不可猜、长度 10–12（禁止 UUID）。
 */
public final class ReminderLinkTokenGenerator
{
    private static final char[] ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int DEFAULT_LENGTH = 11;
    private static final SecureRandom RANDOM = new SecureRandom();

    private ReminderLinkTokenGenerator()
    {
    }

    public static String nextToken()
    {
        return nextToken(DEFAULT_LENGTH);
    }

    public static String nextToken(int length)
    {
        if (length < 10 || length > 12)
        {
            throw new IllegalArgumentException("token length must be 10-12");
        }
        char[] buf = new char[length];
        for (int i = 0; i < length; i++)
        {
            buf[i] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return new String(buf);
    }

    public static boolean isValidFormat(String token)
    {
        if (token == null || token.length() < 10 || token.length() > 12)
        {
            return false;
        }
        for (int i = 0; i < token.length(); i++)
        {
            char c = token.charAt(i);
            boolean ok = (c >= '0' && c <= '9')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z');
            if (!ok)
            {
                return false;
            }
        }
        return true;
    }
}
