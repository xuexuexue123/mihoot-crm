package com.ruoyi.web.controller.crm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppSendResult;
import com.ruoyi.crm.service.ICrmWhatsappMessageService;

@RestController
@RequestMapping("/crm/whatsapp")
public class CrmWhatsappMessageController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappMessageController.class);

    @Autowired
    private ICrmWhatsappMessageService messageService;

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:send')")
    @PostMapping("/send")
    public AjaxResult sendMessage(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            String waId = (String) body.get("waId");
            String phoneNumberId = (String) body.get("phoneNumberId");
            String messageType = (String) body.get("messageType");
            String content = (String) body.get("content");
            String mediaId = (String) body.get("mediaId");
            String mediaUrl = (String) body.get("mediaUrl");
            String caption = (String) body.get("caption");
            @SuppressWarnings("unchecked")
            Map<String, Object> template = (Map<String, Object>) body.get("template");

            log.info("POST /crm/whatsapp/send tenant={} waId={} messageType={} hasMediaId={} hasMediaUrl={} keys={}",
                    tenant, waId, messageType,
                    mediaId != null && !mediaId.isEmpty(),
                    mediaUrl != null && !mediaUrl.isEmpty(),
                    body.keySet());

            WhatsAppSendResult result = messageService.sendMessage(
                    tenant, waId, phoneNumberId, messageType, content, mediaId, mediaUrl, caption, template);

            if (result.isSuccess())
            {
                Map<String, Object> data = new java.util.HashMap<>();
                data.put("messageId", result.getMessageId());
                data.put("waId", result.getWaId());
                return AjaxResult.success("Message sent", data);
            }
            else
            {
                log.error("POST /crm/whatsapp/send failed tenant={} waId={} messageType={} error={}",
                        tenant, waId, messageType, result.getErrorMessage());
                return AjaxResult.error(500,
                        "Meta API error: " + result.getErrorMessage());
            }
        }
        catch (ServiceException e)
        {
            log.warn("POST /crm/whatsapp/send rejected tenant={} msg={}", tenant, e.getMessage());
            return AjaxResult.error(400, e.getMessage());
        }
    }
}
