package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.ruoyi.common.exception.ServiceException;

public class WebhookPathCodecTest
{
    private WebhookPathCodec codec;

    @BeforeEach
    public void setUp()
    {
        codec = new WebhookPathCodec("test-path-secret", "");
    }

    @Test
    public void encodeDecodeRoundTrip()
    {
        String encrypted = codec.encode("tenant-a", "wf-1", "node-1");
        assertNotNull(encrypted);

        WebhookPathCodec.DecodedWebhookPath decoded = codec.decode(encrypted);
        assertEquals("tenant-a", decoded.getTenantId());
        assertEquals("wf-1", decoded.getWorkflowId());
        assertEquals("node-1", decoded.getNodeId());
    }

    @Test
    public void decodeInvalidTokenThrowsNotFound()
    {
        assertThrows(ServiceException.class, () -> codec.decode("not-valid-base64!!!"));
    }

    @Test
    public void decodeTamperedTokenThrowsNotFound()
    {
        String encrypted = codec.encode("tenant-a", "wf-1", "node-1");
        String tampered = encrypted.substring(0, encrypted.length() - 2) + "xx";
        assertThrows(ServiceException.class, () -> codec.decode(tampered));
    }
}
