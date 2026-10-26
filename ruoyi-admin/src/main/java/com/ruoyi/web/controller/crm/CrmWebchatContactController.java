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
import com.ruoyi.crm.service.ICrmWebchatContactService;

@RestController
@RequestMapping("/crm/webchat")
public class CrmWebchatContactController extends BaseController
{
    @Autowired
    private ICrmWebchatContactService contactService;

    @PreAuthorize("@ss.hasPermi('crm:webchat:conversations')")
    @PostMapping("/contact/link")
    public AjaxResult linkContact(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            Long conversationId = toLong(body != null ? body.get("conversationId") : null);
            String contactId = body != null && body.get("contactId") != null
                    ? String.valueOf(body.get("contactId")) : null;
            return AjaxResult.success(contactService.linkContact(tenant, conversationId, contactId));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:webchat:conversations')")
    @DeleteMapping("/contact/link")
    public AjaxResult unlinkContact(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            Long conversationId = toLong(body != null ? body.get("conversationId") : null);
            String contactId = body != null && body.get("contactId") != null
                    ? String.valueOf(body.get("contactId")) : null;
            return AjaxResult.success(contactService.unlinkContact(tenant, conversationId, contactId));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    private static Long toLong(Object raw)
    {
        if (raw == null)
        {
            return null;
        }
        if (raw instanceof Number)
        {
            return ((Number) raw).longValue();
        }
        try
        {
            return Long.parseLong(String.valueOf(raw).trim());
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
}
