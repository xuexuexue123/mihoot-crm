package com.ruoyi.crm.whatsapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.IdentityNormalizer;

@ExtendWith(MockitoExtension.class)
class WhatsappIdentityResolutionSupportTest
{
    @Mock
    private ICrmCustomerIdentityService identityService;

    @InjectMocks
    private WhatsappIdentityResolutionSupport support;

    @Test
    void resolveContactId_hitsWhatsappWaId()
    {
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, "8613800138001"))
                .thenReturn("C001");

        assertEquals("C001", support.resolveContactId("8613800138001"));
        verify(identityService, never()).findContactByIdentity(
                eq(IdentityNormalizer.TYPE_MOBILE_PHONE), anyString());
        verify(identityService, never()).bindIdentity(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void resolveContactId_hitsMobilePhoneAndBindsWaId()
    {
        String waId = "8613800138001";
        String phone = "13800138001";

        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, waId))
                .thenReturn(null);
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_PHONE, phone))
                .thenReturn(null);
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_MOBILE_PHONE, phone))
                .thenReturn("C002");

        assertEquals("C002", support.resolveContactId(waId));
        verify(identityService).bindIdentity("C002", IdentityNormalizer.TYPE_WHATSAPP_WA_ID, waId,
                WhatsappIdentityResolutionSupport.SOURCE_WHATSAPP_WEBHOOK);
    }

    @Test
    void resolveContactId_returnsNullWhenUnresolved()
    {
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, "999"))
                .thenReturn(null);
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_PHONE, "999"))
                .thenReturn(null);
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_MOBILE_PHONE, "999"))
                .thenReturn(null);

        assertNull(support.resolveContactId("999"));
        verify(identityService, never()).bindIdentity(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void resolveContactId_bindConflictPrefersExistingWaBinding()
    {
        String waId = "8613800138001";
        String phone = "13800138001";

        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, waId))
                .thenReturn(null)
                .thenReturn(null)
                .thenReturn("CA");
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_PHONE, phone))
                .thenReturn(null);
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_MOBILE_PHONE, phone))
                .thenReturn("CB");
        doThrow(new ServiceException("already bound", 409))
                .when(identityService).bindIdentity("CB", IdentityNormalizer.TYPE_WHATSAPP_WA_ID, waId,
                        WhatsappIdentityResolutionSupport.SOURCE_WHATSAPP_WEBHOOK);

        assertEquals("CA", support.resolveContactId(waId));
        verify(identityService).recordIdentityConflict(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, waId,
                "CB", WhatsappIdentityResolutionSupport.SOURCE_WHATSAPP_WEBHOOK);
    }

    @Test
    void bindWaIdIfNeeded_skipsWhenAlreadyBound()
    {
        when(identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, "86138"))
                .thenReturn("C001");

        support.bindWaIdIfNeeded("86138", "C001");

        verify(identityService, never()).bindIdentity(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void bindWaIdIfNeeded_skipsWhenContactIdMissing()
    {
        support.bindWaIdIfNeeded("86138", null);

        verify(identityService, never()).findContactByIdentity(anyString(), anyString());
        verify(identityService, never()).bindIdentity(anyString(), anyString(), anyString(), anyString());
    }
}
