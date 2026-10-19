package com.ruoyi.common.utils.sign;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY = "91511800MABXM1U5"; // 必须与前端一致
    private static final String IV = "abcdef9876543210"; // 必须与前端一致

    public static String encrypt(String plainText) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedText) throws Exception {
        // Base64 解码
        byte[] encryptedData = Base64.getDecoder().decode(encryptedText);

        // 创建密钥和 IV
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());

        // 初始化 Cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        // 解密
        byte[] decryptedBytes = cipher.doFinal(encryptedData);

        return new String(decryptedBytes, "UTF-8");
    }

    public static void main(String[] args) {
        try {
            // 这里是前端加密后的字符串示例
            String encryptedText = "FVdyohiv/OgncM65uq42Zg==";
            System.out.println("encryptedText = " + encryptedText);
            String decryptedText = decrypt(encryptedText);
            System.out.println("解密结果: " + decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
