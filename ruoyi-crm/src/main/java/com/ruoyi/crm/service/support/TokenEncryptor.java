package com.ruoyi.crm.service.support;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Token AES-256-GCM 加密/解密工具（WhatsApp / Messenger 共用）。
 *
 * <p>用于加密存储 access_token、webhook_verify_token、app_secret。
 * 密钥优先 {@code messenger.encryption-key}；未配置时回退 {@code whatsapp.encryption-key}
 *（运维需知悉回退，避免静默用错环境密钥）。
 *
 * <p>存储格式：Base64(IV 12 bytes + ciphertext + GCM tag 128 bits)
 */
@Component
public class TokenEncryptor
{
    private static final Logger log = LoggerFactory.getLogger(TokenEncryptor.class);

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final byte[] encryptionKey;

    public TokenEncryptor(
            @Value("${messenger.encryption-key:}") String messengerKey,
            @Value("${whatsapp.encryption-key:}") String whatsappKey)
    {
        String base64Key = (messengerKey != null && !messengerKey.isEmpty()) ? messengerKey : whatsappKey;
        String keySource = (messengerKey != null && !messengerKey.isEmpty())
                ? "messenger.encryption-key"
                : "whatsapp.encryption-key";
        if (base64Key == null || base64Key.isEmpty())
        {
            log.warn("Neither messenger.encryption-key nor whatsapp.encryption-key is configured. "
                    + "Token encryption/decryption will not be available until "
                    + "MESSENGER_ENCRYPTION_KEY / WHATSAPP_ENCRYPTION_KEY env or yaml is set.");
            this.encryptionKey = null;
            return;
        }
        byte[] decoded = Base64.getDecoder().decode(base64Key);
        if (decoded.length != 32)
        {
            log.error("{} must be a 32-byte (256-bit) base64-encoded key, got {} bytes. "
                    + "Token encryption unavailable.", keySource, decoded.length);
            this.encryptionKey = null;
            return;
        }
        this.encryptionKey = decoded;
        if (messengerKey == null || messengerKey.isEmpty())
        {
            log.info("TokenEncryptor using fallback whatsapp.encryption-key "
                    + "(messenger.encryption-key not set)");
        }
    }

    /**
     * 检查加密密钥是否已配置。
     */
    private void ensureKeyConfigured()
    {
        if (encryptionKey == null)
        {
            throw new IllegalStateException(
                "encryption-key is not configured. "
                + "Please set messenger.encryption-key (preferred) or whatsapp.encryption-key "
                + "(MESSENGER_ENCRYPTION_KEY / WHATSAPP_ENCRYPTION_KEY)");
        }
    }

    /**
     * 加密明文 token，返回 Base64(IV + ciphertext+tag)。
     *
     * @param plaintext 明文 token
     * @return Base64 编码的密文
     */
    public String encrypt(String plaintext)
    {
        if (plaintext == null || plaintext.isEmpty())
        {
            return null;
        }
        ensureKeyConfigured();
        try
        {
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            SecretKey key = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] combined = ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();

            return Base64.getEncoder().encodeToString(combined);
        }
        catch (Exception e)
        {
            log.error("Failed to encrypt WhatsApp token", e);
            throw new RuntimeException("Token encryption failed", e);
        }
    }

    /**
     * 解密密文，返回明文 token。
     *
     * @param encryptedBase64 Base64 编码的密文
     * @return 明文 token
     */
    public String decrypt(String encryptedBase64)
    {
        if (encryptedBase64 == null || encryptedBase64.isEmpty())
        {
            return null;
        }
        ensureKeyConfigured();
        try
        {
            byte[] combined = Base64.getDecoder().decode(encryptedBase64);

            ByteBuffer buffer = ByteBuffer.wrap(combined);
            byte[] iv = new byte[GCM_IV_LENGTH];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            SecretKey key = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            log.error("Failed to decrypt WhatsApp token", e);
            throw new RuntimeException("Token decryption failed", e);
        }
    }
}
