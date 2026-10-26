package com.ruoyi.web.controller.crm;

import java.util.Map;

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
import com.ruoyi.crm.service.ICrmWebchatMessageService;

@RestController
@RequestMapping("/crm/webchat")
public class CrmWebchatMessageController extends BaseController
{
    @Autowired
    private ICrmWebchatMessageService messageService;

    @PreAuthorize("@ss.hasPermi('crm:webchat:send')")
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
            Long conversationId = toLong(body != null ? body.get("conversationId") : null);
            String content = body != null && body.get("content") != null
                    ? String.valueOf(body.get("content")) : null;
            String clientMessageId = body != null && body.get("clientMessageId") != null
                    ? String.valueOf(body.get("clientMessageId")) : null;
            Map<String, Object> data = messageService.sendText(
                    tenant, conversationId, content, clientMessageId, false);
            return AjaxResult.success("Message sent", data);
        }
        catch (ServiceException e)
        {
            int code = e.getCode() != null ? e.getCode() : 400;
            return AjaxResult.error(code, e.getMessage());
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
