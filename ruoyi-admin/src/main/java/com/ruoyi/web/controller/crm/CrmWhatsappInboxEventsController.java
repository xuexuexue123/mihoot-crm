package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.whatsapp.realtime.WhatsAppInboxSseHub;

/**
 * WhatsApp Inbox SSE stream (multi-instance via Redis pub/sub).
 */
@RestController
@RequestMapping("/crm/whatsapp")
public class CrmWhatsappInboxEventsController extends BaseController
{
    @Autowired
    private WhatsAppInboxSseHub sseHub;

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:conversations')")
    @GetMapping(value = "/inbox/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            throw new ServiceException("Missing tenant header", 400);
        }
        return sseHub.register(tenant);
    }
}
