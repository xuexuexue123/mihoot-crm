package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;

/**
 * 兼容 sevencrm / Cryptr 的凭证解密（AES-256-GCM + PBKDF2-SHA512）。
 * <p>
 * 密文布局（hex）：salt(64B) + iv(16B) + authTag(16B) + ciphertext。
 * 密钥来源：{@code crm.credential.encryption-key} 或环境变量 {@code ENCRYPTION_KEY}
 * （须与 Next 保存凭证时一致）。
 */
@Component
public class CryptrCompatDecryptor
{
    private static final int SALT_LENGTH = 64;
    private static final int IV_LENGTH = 16;
    private static final int TAG_LENGTH = 16;
    private static final int KEY_LENGTH_BYTES = 32;
    private static final int PBKDF2_ITERATIONS = 100_000;

    @Value("${crm.credential.encryption-key:${ENCRYPTION_KEY:}}")
    private String encryptionKey;

    public boolean isConfigured()
    {
        return StringUtils.isNotEmpty(encryptionKey);
    }

    /**
     * 若可识别为 Cryptr hex 密文则解密；否则返回 null（调用方按明文/其它策略处理）。
     */
    public String tryDecrypt(String stored)
    {
        if (StringUtils.isEmpty(stored) || StringUtils.isEmpty(encryptionKey))
        {
            return null;
        }
        String trimmed = stored.trim();
        if (!looksLikeCryptrHex(trimmed))
        {
            return null;
        }
        try
        {
            return decrypt(trimmed, encryptionKey.trim());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    static boolean looksLikeCryptrHex(String value)
    {
        if (value == null || value.length() < (SALT_LENGTH + IV_LENGTH + TAG_LENGTH + 1) * 2)
        {
            return false;
        }
        for (int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);
            boolean hex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
            if (!hex)
            {
                return false;
            }
        }
        return true;
    }

    static String decrypt(String hexCiphertext, String secret) throws GeneralSecurityException
    {
        byte[] all = hexToBytes(hexCiphertext);
        int min = SALT_LENGTH + IV_LENGTH + TAG_LENGTH + 1;
        if (all.length < min)
        {
            throw new GeneralSecurityException("ciphertext too short");
        }
        byte[] salt = Arrays.copyOfRange(all, 0, SALT_LENGTH);
        byte[] iv = Arrays.copyOfRange(all, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
        byte[] tag = Arrays.copyOfRange(all, SALT_LENGTH + IV_LENGTH, SALT_LENGTH + IV_LENGTH + TAG_LENGTH);
        byte[] encrypted = Arrays.copyOfRange(all, SALT_LENGTH + IV_LENGTH + TAG_LENGTH, all.length);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BYTES * 8);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        // Java GCM API expects ciphertext || tag
        byte[] cipherWithTag = new byte[encrypted.length + tag.length];
        System.arraycopy(encrypted, 0, cipherWithTag, 0, encrypted.length);
        System.arraycopy(tag, 0, cipherWithTag, encrypted.length, tag.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH * 8, iv));
        byte[] plain = cipher.doFinal(cipherWithTag);
        return new String(plain, StandardCharsets.UTF_8);
    }

    private static byte[] hexToBytes(String hex)
    {
        int len = hex.length();
        if ((len & 1) != 0)
        {
            throw new IllegalArgumentException("odd hex length");
        }
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return out;
    }
}
