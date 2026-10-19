package com.ruoyi.web.controller.crm;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.mapper.CrmWhatsappConversationMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;

@RestController
@RequestMapping("/crm/whatsapp")
public class CrmWhatsappContactController extends BaseController
{
    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private CrmWhatsappConversationMapper conversationMapper;

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:contact')")
    @PostMapping("/contact/link")
    public AjaxResult linkContact(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        String waId = (String) body.get("waId");
        String contactId = (String) body.get("contactId");

        if (waId == null || waId.isEmpty())
        {
            return AjaxResult.error(400, "waId is required");
        }
        if (contactId == null || contactId.isEmpty())
        {
            return AjaxResult.error(400, "contactId is required");
        }

        try
        {
            identityService.bindIdentity(contactId, "whatsapp_wa_id", waId, "whatsapp_sidebar");
            conversationMapper.updateContactId(tenant, waId, contactId);
            return AjaxResult.success("WhatsApp contact linked");
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:contact')")
    @DeleteMapping("/contact/link")
    public AjaxResult unlinkContact(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        String waId = (String) body.get("waId");
        String contactId = (String) body.get("contactId");

        if (waId == null || waId.isEmpty())
        {
            return AjaxResult.error(400, "waId is required");
        }
        if (contactId == null || contactId.isEmpty())
        {
            return AjaxResult.error(400, "contactId is required");
        }

        identityService.unbindIdentity(contactId, "whatsapp_wa_id", waId);
        conversationMapper.updateContactId(tenant, waId, null);
        return AjaxResult.success("WhatsApp contact unlinked");
    }
}
