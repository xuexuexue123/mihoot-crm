package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.service.ICrmCustomerIdentityService;

@ExtendWith(MockitoExtension.class)
class WecomIdentityResolutionSupportTest
{
    @Mock
    private ICrmCustomerIdentityService identityService;

    @InjectMocks
    private WecomIdentityResolutionSupport support;

    @Test
    void resolveContactId_prefersUnionId()
    {
        when(identityService.findContactByIdentity("wechat_unionid", "u1")).thenReturn("C001");

        assertEquals("C001", support.resolveContactId("wm_abc", "u1"));
        verify(identityService, never()).findContactByIdentity(eq("wecom_external_userid"), eq("wm_abc"));
    }

    @Test
    void resolveContactId_fallsBackToExternalUserid()
    {
        when(identityService.findContactByIdentity("wechat_unionid", "u1")).thenReturn(null);
        when(identityService.findContactByIdentity("wecom_external_userid", "wm_abc")).thenReturn("C002");

        assertEquals("C002", support.resolveContactId("wm_abc", "u1"));
    }

    @Test
    void resolveContactId_returnsNullWhenUnresolved()
    {
        when(identityService.findContactByIdentity("wecom_external_userid", "wm_new")).thenReturn(null);

        assertNull(support.resolveContactId("wm_new", null));
    }

    @Test
    void bindWecomIdentities_skipsWhenContactIdMissing()
    {
        support.bindWecomIdentities("wm_abc", "u1", null);

        verify(identityService, never()).bindIdentity(eq("C001"), eq("wecom_external_userid"), eq("wm_abc"), eq("wecom_callback"));
    }
}
