package com.ruoyi.crm.service.whatsapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ReminderLinkTokenGeneratorTest
{
    @Test
    void nextToken_defaultLengthAndCharset()
    {
        String token = ReminderLinkTokenGenerator.nextToken();
        assertEquals(11, token.length());
        assertTrue(ReminderLinkTokenGenerator.isValidFormat(token));
    }

    @Test
    void nextToken_rejectsOutOfRangeLength()
    {
        assertThrows(IllegalArgumentException.class, () -> ReminderLinkTokenGenerator.nextToken(9));
        assertThrows(IllegalArgumentException.class, () -> ReminderLinkTokenGenerator.nextToken(13));
    }

    @Test
    void nextToken_producesDistinctValues()
    {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 50; i++)
        {
            seen.add(ReminderLinkTokenGenerator.nextToken());
        }
        assertTrue(seen.size() > 40);
    }

    @Test
    void isValidFormat_rejectsUuidAndShort()
    {
        assertFalse(ReminderLinkTokenGenerator.isValidFormat(null));
        assertFalse(ReminderLinkTokenGenerator.isValidFormat("short"));
        assertFalse(ReminderLinkTokenGenerator.isValidFormat("550e8400-e29b-41d4-a716-446655440000"));
        assertFalse(ReminderLinkTokenGenerator.isValidFormat("bad_token!!"));
        assertTrue(ReminderLinkTokenGenerator.isValidFormat("abcdefghij"));
        assertTrue(ReminderLinkTokenGenerator.isValidFormat("abcdefghijkl"));
    }
}
