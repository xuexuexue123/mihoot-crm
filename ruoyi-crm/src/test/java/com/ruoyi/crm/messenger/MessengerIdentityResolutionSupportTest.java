package com.ruoyi.crm.messenger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.IdentityNormalizer;

@ExtendWith(MockitoExtension.class)
class MessengerIdentityResolutionSupportTest
{
    @Mock
    private ICrmCustomerIdentityService identityService;

    @InjectMocks
    private MessengerIdentityResolutionSupport support;

    @Test
    void resolveContactId_hitsMessengerPsid()
    {
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_MESSENGER_PSID, "PSID_001"))
                .thenReturn("C001");

        assertEquals("C001", support.resolveContactId("PSID_001"));
        verify(identityService, never()).bindIdentity(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void resolveContactId_returnsNullWhenUnresolved()
    {
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_MESSENGER_PSID, "UNKNOWN"))
                .thenReturn(null);

        assertNull(support.resolveContactId("UNKNOWN"));
    }

    @Test
    void resolveContactId_trimsPsid()
    {
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_MESSENGER_PSID, "PSID_002"))
                .thenReturn("C002");

        assertEquals("C002", support.resolveContactId("  PSID_002  "));
    }

    @Test
    void bindPsidIfNeeded_skipsWhenAlreadyBound()
    {
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_MESSENGER_PSID, "PSID_003"))
                .thenReturn("C001");

        support.bindPsidIfNeeded("PSID_003", "C001");

        verify(identityService, never()).bindIdentity(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void bindPsidIfNeeded_bindsWhenMissing()
    {
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_MESSENGER_PSID, "PSID_004"))
                .thenReturn(null);

        support.bindPsidIfNeeded("PSID_004", "C004", MessengerIdentityResolutionSupport.SOURCE_MESSENGER_SIDEBAR);

        verify(identityService).bindIdentity("C004", IdentityNormalizer.TYPE_MESSENGER_PSID, "PSID_004",
                MessengerIdentityResolutionSupport.SOURCE_MESSENGER_SIDEBAR);
    }
}
