package com.ruoyi.web.controller.crm;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.ICrmWebchatPublicService;
import com.ruoyi.crm.webchat.WebchatTenantBinder;

@RestController
@RequestMapping("/crm/webchat/public")
public class CrmWebchatPublicController extends BaseController
{
    @Autowired
    private ICrmWebchatPublicService publicService;

    @Autowired
    private WebchatTenantBinder tenantBinder;

    @GetMapping("/config")
    public AjaxResult getConfig(
            @RequestParam("token") String token,
            @RequestHeader(value = "Origin", required = false) String origin,
            @RequestHeader(value = "Referer", required = false) String referer,
            HttpServletRequest request)
    {
        try
        {
            return AjaxResult.success(publicService.getConfig(token, origin, referer, clientIp(request)));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
        finally
        {
            tenantBinder.clear();
        }
    }

    @PostMapping("/session")
    public AjaxResult createSession(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Origin", required = false) String origin,
            @RequestHeader(value = "Referer", required = false) String referer,
            HttpServletRequest request)
    {
        try
        {
            String token = str(body, "token");
            // 先切租户库，再进业务（避免事务/连接钉在主库）
            publicService.resolveAndBindTenant(token);
            String visitorId = str(body, "visitorId");
            String sessionId = str(body, "sessionId");
            String userAgent = str(body, "userAgent");
            if (userAgent == null)
            {
                userAgent = request.getHeader("User-Agent");
            }
            return AjaxResult.success(publicService.createSession(
                    token, origin, referer, clientIp(request), visitorId, sessionId, userAgent));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
        finally
        {
            tenantBinder.clear();
        }
    }

    @PostMapping("/conversations")
    public AjaxResult openConversation(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Origin", required = false) String origin,
            @RequestHeader(value = "Referer", required = false) String referer,
            @RequestHeader(value = "X-Webchat-Session", required = false) String sessionHeader)
    {
        try
        {
            String token = str(body, "token");
            publicService.resolveAndBindTenant(token);
            String sessionToken = firstNonBlank(sessionHeader, str(body, "sessionToken"));
            return AjaxResult.success(publicService.openConversation(sessionToken, token, origin, referer));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
        finally
        {
            tenantBinder.clear();
        }
    }

    @GetMapping("/conversations/{id}/messages")
    public AjaxResult listMessages(
            @PathVariable("id") Long id,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(required = false) Long beforeId,
            @RequestParam(defaultValue = "50") Integer pageSize,
            @RequestHeader(value = "X-Webchat-Session", required = false) String sessionHeader,
            @RequestParam(value = "sessionToken", required = false) String sessionParam)
    {
        try
        {
            String sessionToken = firstNonBlank(sessionHeader, sessionParam);
            return AjaxResult.success(publicService.listMessages(sessionToken, token, id, beforeId, pageSize));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
        finally
        {
            tenantBinder.clear();
        }
    }

    @PostMapping("/conversations/{id}/messages")
    public AjaxResult postMessage(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Origin", required = false) String origin,
            @RequestHeader(value = "Referer", required = false) String referer,
            @RequestHeader(value = "X-Webchat-Session", required = false) String sessionHeader,
            HttpServletRequest request)
    {
        try
        {
            String token = str(body, "token");
            publicService.resolveAndBindTenant(token);
            String sessionToken = firstNonBlank(sessionHeader, str(body, "sessionToken"));
            String content = str(body, "content");
            String messageType = str(body, "messageType");
            String clientMessageId = str(body, "clientMessageId");
            return AjaxResult.success(publicService.postMessage(
                    sessionToken, token, origin, referer, clientIp(request),
                    id, content, messageType, clientMessageId));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
        finally
        {
            tenantBinder.clear();
        }
    }

    @PostMapping("/conversations/{id}/identify")
    public AjaxResult identify(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "Origin", required = false) String origin,
            @RequestHeader(value = "Referer", required = false) String referer,
            @RequestHeader(value = "X-Webchat-Session", required = false) String sessionHeader,
            HttpServletRequest request)
    {
        try
        {
            String token = str(body, "token");
            publicService.resolveAndBindTenant(token);
            String sessionToken = firstNonBlank(sessionHeader, str(body, "sessionToken"));
            return AjaxResult.success(publicService.identify(
                    sessionToken, token, origin, referer, clientIp(request), id,
                    str(body, "displayName"), str(body, "mobilePhone"),
                    str(body, "email"), str(body, "whatsapp")));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
        finally
        {
            tenantBinder.clear();
        }
    }

    @PostMapping("/conversations/{id}/close")
    public AjaxResult closeConversation(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "X-Webchat-Session", required = false) String sessionHeader)
    {
        try
        {
            if (body == null)
            {
                body = java.util.Collections.emptyMap();
            }
            String token = str(body, "token");
            publicService.resolveAndBindTenant(token);
            String sessionToken = firstNonBlank(sessionHeader, str(body, "sessionToken"));
            return AjaxResult.success(publicService.closeConversation(sessionToken, token, id));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
        finally
        {
            tenantBinder.clear();
        }
    }

    @GetMapping(value = "/conversations/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter conversationEvents(
            @PathVariable("id") Long id,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "sessionToken", required = false) String sessionParam,
            @RequestHeader(value = "X-Webchat-Session", required = false) String sessionHeader)
    {
        try
        {
            String sessionToken = firstNonBlank(sessionHeader, sessionParam);
            return publicService.subscribeConversationEvents(sessionToken, token, id);
        }
        catch (ServiceException e)
        {
            tenantBinder.clear();
            throw e;
        }
        // Do not clear DS while SSE is open; hub does not need DS after register.
        // Clear after register succeeds:
        finally
        {
            // SSE emitter already registered; clear thread-local DS
            tenantBinder.clear();
        }
    }

    private static String clientIp(HttpServletRequest request)
    {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.trim().isEmpty())
        {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static String str(Map<String, Object> body, String key)
    {
        if (body == null || body.get(key) == null)
        {
            return null;
        }
        String s = String.valueOf(body.get(key)).trim();
        return s.isEmpty() || "null".equalsIgnoreCase(s) ? null : s;
    }

    private static String firstNonBlank(String a, String b)
    {
        if (a != null && !a.trim().isEmpty())
        {
            return a.trim();
        }
        return b;
    }
}
