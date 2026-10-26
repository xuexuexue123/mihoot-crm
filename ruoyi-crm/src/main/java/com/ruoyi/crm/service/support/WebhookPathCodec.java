package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * Webhook 公网 path：AES-GCM 加密 {@code tenantId|workflowId|nodeId}，URL-safe Base64 输出。
 */
@Component
public class WebhookPathCodec
{
    private static final String PAYLOAD_VERSION = "1";
    private static final String DELIMITER = "|";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final String CIPHER = "AES/GCM/NoPadding";

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public WebhookPathCodec(
            @Value("${webhook.path-secret:}") String pathSecret,
            @Value("${webhook.bff-secret:}") String bffSecret)
    {
        String secret = StringUtils.isNotBlank(pathSecret) ? pathSecret.trim() : bffSecret;
        if (StringUtils.isBlank(secret))
        {
            throw new IllegalStateException("webhook.path-secret or webhook.bff-secret must be configured");
        }
        this.secretKey = deriveKey(secret);
    }

    public String encode(String tenantId, String workflowId, String nodeId)
    {
        requireNonBlank(tenantId, "tenantId");
        requireNonBlank(workflowId, "workflowId");
        requireNonBlank(nodeId, "nodeId");

        String plain = PAYLOAD_VERSION + DELIMITER + tenantId.trim() + DELIMITER
                + workflowId.trim() + DELIMITER + nodeId.trim();
        try
        {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] cipherText = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
        }
        catch (Exception ex)
        {
            throw new ServiceException("Failed to encode webhook path", HttpStatus.ERROR);
        }
    }

    public DecodedWebhookPath decode(String encryptedPath)
    {
        if (StringUtils.isBlank(encryptedPath))
        {
            throw new ServiceException("path is required", HttpStatus.BAD_REQUEST);
        }
        try
        {
            byte[] combined = Base64.getUrlDecoder().decode(encryptedPath.trim());
            if (combined.length <= GCM_IV_LENGTH)
            {
                throw invalidPath();
            }
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            String plain = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);

            String[] parts = plain.split("\\|", 4);
            if (parts.length != 4 || !PAYLOAD_VERSION.equals(parts[0]))
            {
                throw invalidPath();
            }
            return new DecodedWebhookPath(parts[1], parts[2], parts[3]);
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw invalidPath();
        }
    }

    private ServiceException invalidPath()
    {
        return new ServiceException("Webhook endpoint not found", HttpStatus.NOT_FOUND);
    }

    private static void requireNonBlank(String value, String field)
    {
        if (StringUtils.isBlank(value))
        {
            throw new ServiceException(field + " is required", HttpStatus.BAD_REQUEST);
        }
    }

    private static SecretKeySpec deriveKey(String secret)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] key = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(key, "AES");
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Failed to derive webhook path key", ex);
        }
    }

    public static final class DecodedWebhookPath
    {
        private final String tenantId;
        private final String workflowId;
        private final String nodeId;

        public DecodedWebhookPath(String tenantId, String workflowId, String nodeId)
        {
            this.tenantId = tenantId;
            this.workflowId = workflowId;
            this.nodeId = nodeId;
        }

        public String getTenantId()
        {
            return tenantId;
        }

        public String getWorkflowId()
        {
            return workflowId;
        }

        public String getNodeId()
        {
            return nodeId;
        }
    }
}
