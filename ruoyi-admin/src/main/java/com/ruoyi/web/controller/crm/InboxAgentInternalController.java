package com.ruoyi.web.controller.crm;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.IInboxAgentChannelService;
import com.ruoyi.crm.service.support.RecordTriggerBffSecretValidator;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * Inbox AI Agent 内部 API（Next worker → 9090）。
 * 鉴权：X-Record-Trigger-BFF-Secret（与 event/record trigger 同源）。
 */
@RestController
@RequestMapping("/crm/inbox-agent")
public class InboxAgentInternalController extends BaseController
{
    @Autowired
    private RecordTriggerBffSecretValidator bffSecretValidator;

    @Autowired
    private IInboxAgentChannelService inboxAgentChannelService;

    @PostMapping("/whatsapp/send")
    public AjaxResult sendWhatsApp(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenantHeader,
            HttpServletRequest request)
    {
        bffSecretValidator.validate(request);
        String tenant = resolveTenant(tenantHeader, body);
        Long conversationId = toLong(body != null ? body.get("conversationId") : null);
        String text = body != null && body.get("text") != null ? String.valueOf(body.get("text")) : null;
        try
        {
            TenantDataSourceSupport.bindTenantKey(tenant);
            return AjaxResult.success(inboxAgentChannelService.sendWhatsAppText(tenant, conversationId, text));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 500, e.getMessage());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping("/wechat_mp/reply")
    public AjaxResult replyWechatMp(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenantHeader,
            HttpServletRequest request)
    {
        bffSecretValidator.validate(request);
        String tenant = resolveTenant(tenantHeader, body);
        Long conversationId = toLong(body != null ? body.get("conversationId") : null);
        String text = body != null && body.get("text") != null ? String.valueOf(body.get("text")) : null;
        try
        {
            TenantDataSourceSupport.bindTenantKey(tenant);
            return AjaxResult.success(inboxAgentChannelService.replyWechatMpText(conversationId, text));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 500, e.getMessage());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping("/messenger/send")
    public AjaxResult sendMessenger(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenantHeader,
            HttpServletRequest request)
    {
        bffSecretValidator.validate(request);
        String tenant = resolveTenant(tenantHeader, body);
        Long conversationId = toLong(body != null ? body.get("conversationId") : null);
        String text = body != null && body.get("text") != null ? String.valueOf(body.get("text")) : null;
        try
        {
            TenantDataSourceSupport.bindTenantKey(tenant);
            return AjaxResult.success(inboxAgentChannelService.sendMessengerText(tenant, conversationId, text));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 500, e.getMessage());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping("/webchat/send")
    public AjaxResult sendWebchat(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenantHeader,
            HttpServletRequest request)
    {
        bffSecretValidator.validate(request);
        String tenant = resolveTenant(tenantHeader, body);
        Long conversationId = toLong(body != null ? body.get("conversationId") : null);
        String text = body != null && body.get("text") != null ? String.valueOf(body.get("text")) : null;
        try
        {
            TenantDataSourceSupport.bindTenantKey(tenant);
            return AjaxResult.success(inboxAgentChannelService.sendWebchatText(tenant, conversationId, text));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 500, e.getMessage());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping("/wecom_kf/send")
    public AjaxResult sendWecomKf(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenantHeader,
            HttpServletRequest request)
    {
        bffSecretValidator.validate(request);
        String tenant = resolveTenant(tenantHeader, body);
        Long conversationId = toLong(body != null ? body.get("conversationId") : null);
        String text = body != null && body.get("text") != null ? String.valueOf(body.get("text")) : null;
        try
        {
            TenantDataSourceSupport.bindTenantKey(tenant);
            return AjaxResult.success(inboxAgentChannelService.sendWecomKfText(tenant, conversationId, text));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 500, e.getMessage());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private static String resolveTenant(String tenantHeader, Map<String, Object> body)
    {
        if (StringUtils.isNotEmpty(tenantHeader))
        {
            return tenantHeader.trim();
        }
        if (body != null && body.get("tenant") != null)
        {
            return String.valueOf(body.get("tenant")).trim();
        }
        throw new ServiceException("tenant is required", 400);
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
        String s = String.valueOf(raw).trim();
        if (s.isEmpty())
        {
            return null;
        }
        try
        {
            return Long.parseLong(s);
        }
        catch (NumberFormatException e)
        {
            throw new ServiceException("conversationId invalid", 400);
        }
    }
}
