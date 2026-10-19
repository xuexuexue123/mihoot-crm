package com.ruoyi.web.controller.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmMessengerConversation;
import com.ruoyi.crm.domain.dto.CrmMessengerConversationQuery;
import com.ruoyi.crm.service.ICrmMessengerConversationService;

@RestController
@RequestMapping("/crm/messenger")
public class CrmMessengerConversationController extends BaseController
{
    @Autowired
    private ICrmMessengerConversationService conversationService;

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations')")
    @GetMapping("/conversations")
    public TableDataInfo listConversations(
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return getDataTable(java.util.Collections.emptyList());
        }
        CrmMessengerConversationQuery query = new CrmMessengerConversationQuery();
        query.setTenantId(tenant);
        query.setScope(scope);
        query.setStatus(status);
        query.setKeyword(keyword);

        PageHelper.startPage(pageNum, pageSize);
        List<CrmMessengerConversation> list = conversationService.selectConversationList(query);

        List<Map<String, Object>> enriched = new ArrayList<>();
        for (CrmMessengerConversation conv : list)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", conv.getId());
            item.put("psid", conv.getPsid());
            item.put("pageId", conv.getPageId());
            item.put("profileName", conv.getProfileName());
            item.put("contactId", conv.getContactId());
            item.put("leadId", conv.getLeadId());
            item.put("lastMessageAt", conv.getLastMessageAt());
            item.put("lastMessagePreview", conv.getLastMessagePreview());
            item.put("unreadCount", conv.getUnreadCount());
            item.put("status", conv.getStatus());
            item.put("lastOutboundAt", conv.getLastOutboundAt());
            item.put("lastCustomerMessageAt", conv.getLastCustomerMessageAt());
            conversationService.putAssignmentFields(conv, item);

            if (conv.getLastCustomerMessageAt() != null)
            {
                long windowEnd = conv.getLastCustomerMessageAt().getTime() + 86400_000L;
                long remaining = (windowEnd - System.currentTimeMillis()) / 1000;
                item.put("messagingWindowRemainingSec", remaining > 0 ? remaining : 0);
            }
            else
            {
                item.put("messagingWindowRemainingSec", null);
            }

            conversationService.enrichContactDisplay(conv.getContactId(), item);

            item.put("createdAt", conv.getCreateTime());
            item.put("updatedAt", conv.getUpdateTime());
            enriched.add(item);
        }

        TableDataInfo result = getDataTable(list);
        result.setRows(enriched);
        return result;
    }

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations')")
    @GetMapping("/conversation/{id}")
    public AjaxResult getConversation(
            @PathVariable Long id,
            @RequestParam(required = false) Long beforeMsgId,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        Map<String, Object> detail = conversationService.getConversationDetail(id, beforeMsgId, pageSize);
        if (detail == null)
        {
            return AjaxResult.error(404, "Conversation not found");
        }
        return AjaxResult.success(detail);
    }

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations')")
    @PostMapping("/conversation/{id}/read")
    public AjaxResult markRead(
            @PathVariable Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        Map<String, Object> data = conversationService.markRead(id, tenant);
        if (data == null)
        {
            return AjaxResult.error(404, "Conversation not found");
        }
        return AjaxResult.success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations')")
    @PatchMapping("/conversation/{id}/hosting")
    public AjaxResult patchHosting(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        if (body == null || !body.containsKey("hostedByAi"))
        {
            return AjaxResult.error(400, "hostedByAi is required");
        }
        try
        {
            return AjaxResult.success(conversationService.updateHostedByAi(id, tenant,
                    parseHostedByAi(body.get("hostedByAi"))));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations')")
    @PostMapping("/conversation/{id}/claim")
    public AjaxResult claim(
            @PathVariable Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            return AjaxResult.success(conversationService.claim(id, tenant));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations') or @ss.hasPermi('crm:messenger:assign')")
    @PostMapping("/conversation/{id}/release")
    public AjaxResult release(
            @PathVariable Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            return AjaxResult.success(conversationService.release(id, tenant));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:messenger:assign')")
    @PostMapping("/conversation/{id}/assign")
    public AjaxResult assign(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        Long userId = null;
        if (body != null && body.get("userId") != null)
        {
            Object raw = body.get("userId");
            if (raw instanceof Number)
            {
                userId = ((Number) raw).longValue();
            }
            else
            {
                try
                {
                    userId = Long.parseLong(String.valueOf(raw));
                }
                catch (NumberFormatException ex)
                {
                    return AjaxResult.error(400, "userId invalid");
                }
            }
        }
        try
        {
            return AjaxResult.success(conversationService.assign(id, tenant, userId));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    private static Boolean parseHostedByAi(Object raw)
    {
        if (raw == null)
        {
            return null;
        }
        if (raw instanceof Boolean)
        {
            return (Boolean) raw;
        }
        String s = String.valueOf(raw).trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s))
        {
            return null;
        }
        if ("true".equalsIgnoreCase(s) || "1".equals(s))
        {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(s) || "0".equals(s))
        {
            return Boolean.FALSE;
        }
        throw new ServiceException("hostedByAi must be true, false, or null", HttpStatus.BAD_REQUEST);
    }
}
