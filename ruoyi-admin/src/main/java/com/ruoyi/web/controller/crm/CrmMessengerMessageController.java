package com.ruoyi.web.controller.crm;

import java.util.HashMap;
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
import com.ruoyi.crm.domain.dto.messenger.MessengerSendResult;
import com.ruoyi.crm.service.ICrmMessengerMessageService;

@RestController
@RequestMapping("/crm/messenger")
public class CrmMessengerMessageController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CrmMessengerMessageController.class);

    @Autowired
    private ICrmMessengerMessageService messageService;

    @PreAuthorize("@ss.hasPermi('crm:messenger:send')")
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
            String psid = body != null && body.get("psid") != null ? String.valueOf(body.get("psid")) : null;
            String pageId = body != null && body.get("pageId") != null ? String.valueOf(body.get("pageId")) : null;
            String messageType = body != null && body.get("messageType") != null
                    ? String.valueOf(body.get("messageType")) : "text";
            String content = body != null && body.get("content") != null
                    ? String.valueOf(body.get("content")) : null;
            String mediaUrl = body != null && body.get("mediaUrl") != null
                    ? String.valueOf(body.get("mediaUrl")) : null;
            String filename = body != null && body.get("filename") != null
                    ? String.valueOf(body.get("filename")) : null;
            String caption = body != null && body.get("caption") != null
                    ? String.valueOf(body.get("caption")) : null;
            String messagingType = body != null && body.get("messagingType") != null
                    ? String.valueOf(body.get("messagingType")) : null;
            String tag = body != null && body.get("tag") != null
                    ? String.valueOf(body.get("tag")) : null;

            if (messageType == null || messageType.isEmpty())
            {
                messageType = "text";
            }

            log.info("POST /crm/messenger/send tenant={} psid={} messageType={}", tenant, psid, messageType);

            MessengerSendResult result = messageService.sendMessage(
                    tenant, psid, pageId, messageType, content, mediaUrl, filename, caption,
                    messagingType, tag, false);

            if (result.isSuccess())
            {
                Map<String, Object> data = new HashMap<>();
                data.put("messageId", result.getMessageId());
                data.put("psid", result.getPsid());
                return AjaxResult.success("Message sent", data);
            }
            else
            {
                log.error("POST /crm/messenger/send failed tenant={} psid={} error={}",
                        tenant, psid, result.getErrorMessage());
                return AjaxResult.error(500, "Meta API error: " + result.getErrorMessage());
            }
        }
        catch (ServiceException e)
        {
            log.warn("POST /crm/messenger/send rejected tenant={} msg={}", tenant, e.getMessage());
            int code = e.getCode() != null ? e.getCode() : 400;
            return AjaxResult.error(code, e.getMessage());
        }
    }
}
