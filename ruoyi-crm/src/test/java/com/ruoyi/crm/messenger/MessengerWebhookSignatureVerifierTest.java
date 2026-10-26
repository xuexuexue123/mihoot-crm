package com.ruoyi.crm.messenger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MessengerWebhookSignatureVerifierTest
{
    private final MessengerWebhookSignatureVerifier verifier = new MessengerWebhookSignatureVerifier();

    @Test
    void verify_acceptsValidSignature()
    {
        String secret = "test_app_secret";
        String body = "{\"object\":\"page\",\"entry\":[]}";
        String hex = verifier.hmacSha256Hex(secret, body);
        assertTrue(verifier.verify(secret, body, "sha256=" + hex));
    }

    @Test
    void verify_rejectsWrongSignature()
    {
        String secret = "test_app_secret";
        String body = "{\"object\":\"page\"}";
        assertFalse(verifier.verify(secret, body, "sha256=deadbeef"));
        assertFalse(verifier.verify(secret, body, null));
        assertFalse(verifier.verify(null, body, "sha256=abc"));
        assertFalse(verifier.verify(secret, body, "sha256=" + verifier.hmacSha256Hex("other", body)));
    }
}
