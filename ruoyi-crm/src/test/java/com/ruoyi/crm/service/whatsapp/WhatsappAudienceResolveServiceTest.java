package com.ruoyi.crm.service.whatsapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.service.support.IdentityNormalizer;

class WhatsappAudienceResolveServiceTest
{
    @Test
    void pickWaId_usesWhatsappWaIdOnly()
    {
        CrmCustomerIdentity wa = identity(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, "8613800138001");
        CrmCustomerIdentity phone = identity(IdentityNormalizer.TYPE_WHATSAPP_PHONE, "13800138001");
        assertEquals("8613800138001", WhatsappAudienceResolveService.pickWaId(Arrays.asList(phone, wa)));
    }

    @Test
    void pickWaId_ignoresPhoneIdentities()
    {
        List<CrmCustomerIdentity> onlyPhones = Arrays.asList(
                identity(IdentityNormalizer.TYPE_WHATSAPP_PHONE, "8613800138001"),
                identity(IdentityNormalizer.TYPE_MOBILE_PHONE, "13800138001"));
        assertNull(WhatsappAudienceResolveService.pickWaId(onlyPhones));
    }

    @Test
    void pickWaId_nullOrEmpty()
    {
        assertNull(WhatsappAudienceResolveService.pickWaId(null));
        assertNull(WhatsappAudienceResolveService.pickWaId(Collections.emptyList()));
        assertNull(WhatsappAudienceResolveService.pickWaId(
                Collections.singletonList(identity(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, "  "))));
    }

    private static CrmCustomerIdentity identity(String type, String value)
    {
        CrmCustomerIdentity id = new CrmCustomerIdentity();
        id.setIdentityType(type);
        id.setIdentityValue(value);
        return id;
    }
}
