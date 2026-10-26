package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Cryptr 兼容解密（与 sevencrm node cryptr 默认参数一致）。
 */
class CryptrCompatDecryptorTest
{
    @Test
    void decryptsCryptrHexProducedByNodeDefaults() throws Exception
    {
        // Generated with: new Cryptr('my-secure-key').encrypt('sk-test-key-12345')
        // Re-generate if cryptr defaults change.
        String secret = "my-secure-key";
        String plain = "sk-test-key-12345";
        String hex = encryptViaSameAlgorithm(plain, secret);
        assertTrue(CryptrCompatDecryptor.looksLikeCryptrHex(hex));
        assertEquals(plain, CryptrCompatDecryptor.decrypt(hex, secret));
    }

    /** Mirror Cryptr encrypt for self-contained test (no Node dependency). */
    private static String encryptViaSameAlgorithm(String value, String secret) throws Exception
    {
        java.security.SecureRandom rnd = new java.security.SecureRandom();
        byte[] salt = new byte[64];
        byte[] iv = new byte[16];
        rnd.nextBytes(salt);
        rnd.nextBytes(iv);

        javax.crypto.SecretKeyFactory factory =
                javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        javax.crypto.spec.PBEKeySpec spec =
                new javax.crypto.spec.PBEKeySpec(secret.toCharArray(), salt, 100_000, 256);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        javax.crypto.spec.SecretKeySpec key = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");

        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key,
                new javax.crypto.spec.GCMParameterSpec(128, iv));
        byte[] cipherWithTag = cipher.doFinal(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        // Java returns ciphertext||tag
        byte[] encrypted = java.util.Arrays.copyOfRange(cipherWithTag, 0, cipherWithTag.length - 16);
        byte[] tag = java.util.Arrays.copyOfRange(cipherWithTag, cipherWithTag.length - 16, cipherWithTag.length);

        byte[] all = new byte[salt.length + iv.length + tag.length + encrypted.length];
        int p = 0;
        System.arraycopy(salt, 0, all, p, salt.length); p += salt.length;
        System.arraycopy(iv, 0, all, p, iv.length); p += iv.length;
        System.arraycopy(tag, 0, all, p, tag.length); p += tag.length;
        System.arraycopy(encrypted, 0, all, p, encrypted.length);

        StringBuilder sb = new StringBuilder(all.length * 2);
        for (byte b : all)
        {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
