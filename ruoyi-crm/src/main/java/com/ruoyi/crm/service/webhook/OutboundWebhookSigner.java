package com.ruoyi.crm.service.webhook;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class OutboundWebhookSigner
{
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private OutboundWebhookSigner()
    {
    }

    public static String signHeaderValue(String secret, long unixSeconds, String rawBody)
    {
        return "sha256=" + hmacSha256Hex(secret, unixSeconds + "." + (rawBody == null ? "" : rawBody));
    }

    public static String hmacSha256Hex(String secret, String message)
    {
        try
        {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("HmacSHA256 unavailable", ex);
        }
    }

    public static boolean matches(String secret, long unixSeconds, String rawBody, String header)
    {
        if (secret == null || header == null)
        {
            return false;
        }
        String expected = signHeaderValue(secret, unixSeconds, rawBody);
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = header.trim().getBytes(StandardCharsets.UTF_8);
        return expectedBytes.length == actualBytes.length
                && MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private static String toHex(byte[] bytes)
    {
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++)
        {
            out[i * 2] = HEX[(bytes[i] >> 4) & 0xf];
            out[i * 2 + 1] = HEX[bytes[i] & 0xf];
        }
        return new String(out);
    }
}
