package com.ruoyi.crm.mobile.push;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FcmPushClientTest
{
    @Test
    void isUnregisteredToken_detectsNotFound()
    {
        assertTrue(FcmPushClient.isUnregisteredToken(404,
                "{\"error\":{\"status\":\"NOT_FOUND\",\"message\":\"Requested entity was not found.\"}}"));
    }

    @Test
    void isUnregisteredToken_detectsUnregistered()
    {
        assertTrue(FcmPushClient.isUnregisteredToken(400,
                "{\"error\":{\"status\":\"INVALID_ARGUMENT\",\"details\":[{\"errorCode\":\"UNREGISTERED\"}]}}"));
    }

    @Test
    void isUnregisteredToken_ignoresOtherErrors()
    {
        assertFalse(FcmPushClient.isUnregisteredToken(500, "internal error"));
    }
}
