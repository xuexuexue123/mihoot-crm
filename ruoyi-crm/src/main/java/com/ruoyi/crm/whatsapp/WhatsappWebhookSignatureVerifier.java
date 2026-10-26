package com.ruoyi.crm.whatsapp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Meta WhatsApp Webhook {@code X-Hub-Signature-256} 校验。
 *
 * <p>expected = {@code sha256=} + HMAC_SHA256(app_secret, raw_body)
 */
@Component
public class WhatsappWebhookSignatureVerifier
{
    private static final String PREFIX = "sha256=";
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    /**
     * @param appSecret Meta App Secret（明文）
     * @param rawBody   原始请求体（须与 Meta 发送字节一致）
     * @param signatureHeader {@code X-Hub-Signature-256} 头值
     * @return true 当签名匹配
     */
    public boolean verify(String appSecret, String rawBody, String signatureHeader)
    {
        if (!StringUtils.hasText(appSecret) || rawBody == null || !StringUtils.hasText(signatureHeader))
        {
            return false;
        }
        String expected = PREFIX + hmacSha256Hex(appSecret, rawBody);
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = signatureHeader.trim().getBytes(StandardCharsets.UTF_8);
        return expectedBytes.length == actualBytes.length
                && MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    public String hmacSha256Hex(String appSecret, String rawBody)
    {
        try
        {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("HmacSHA256 unavailable", e);
        }
    }

    private static String toHex(byte[] bytes)
    {
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++)
        {
            int v = bytes[i] & 0xFF;
            out[i * 2] = HEX[v >>> 4];
            out[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(out);
    }
}
