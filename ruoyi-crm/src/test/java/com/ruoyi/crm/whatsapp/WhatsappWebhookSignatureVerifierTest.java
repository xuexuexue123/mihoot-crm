package com.ruoyi.crm.whatsapp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WhatsappWebhookSignatureVerifierTest
{
    private final WhatsappWebhookSignatureVerifier verifier = new WhatsappWebhookSignatureVerifier();

    @Test
    void verify_acceptsValidSignature()
    {
        String secret = "test_app_secret";
        String body = "{\"object\":\"whatsapp_business_account\",\"entry\":[]}";
        String hex = verifier.hmacSha256Hex(secret, body);
        assertTrue(verifier.verify(secret, body, "sha256=" + hex));
    }

    @Test
    void verify_rejectsWrongSignature()
    {
        String secret = "test_app_secret";
        String body = "{\"object\":\"whatsapp_business_account\"}";
        assertFalse(verifier.verify(secret, body, "sha256=deadbeef"));
        assertFalse(verifier.verify(secret, body, null));
        assertFalse(verifier.verify(null, body, "sha256=abc"));
        assertFalse(verifier.verify(secret, body, "sha256=" + verifier.hmacSha256Hex("other", body)));
    }
}
