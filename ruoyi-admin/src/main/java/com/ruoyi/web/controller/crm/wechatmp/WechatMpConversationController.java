package com.ruoyi.web.controller.crm.wechatmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.dto.WechatMpConversationQuery;
import com.ruoyi.crm.wechatmp.service.IWechatMpConversationService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpConversationController extends BaseController
{
    @Autowired
    private IWechatMpConversationService conversationService;

    /**
     * 会话列表。
     * <ul>
     *   <li>传 {@code scope}：坐席池（open/mine/team），appId 可选（缺省合并全部可见公众号）</li>
     *   <li>不传 scope 且传 appId：旧营销页兼容，不按归属过滤</li>
     * </ul>
     */
    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:conversations') or @ss.hasPermi('crm:wechat_mp:message')")
    @GetMapping("/conversations")
    public TableDataInfo listConversations(
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String appId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(required = false) String keyword)
    {
        WechatMpConversationQuery query = new WechatMpConversationQuery();
        boolean legacy = !StringUtils.hasText(scope);
        if (legacy)
        {
            if (!StringUtils.hasText(appId))
            {
                throw new ServiceException("兼容模式需要 appId，或传入 scope", HttpStatus.BAD_REQUEST);
            }
            query.setLegacyAll(true);
            query.setAppId(appId);
        }
        else
        {
            query.setScope(scope);
            if (StringUtils.hasText(appId))
            {
                query.setAppId(appId);
            }
        }
        query.setKeyword(keyword);

        PageHelper.startPage(pageNum, pageSize);
        List<CrmWechatMpConversation> list = conversationService.selectConversationList(query);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmWechatMpConversation conv : list)
        {
            rows.add(conversationService.toApiMap(conv));
        }
        TableDataInfo result = getDataTable(list);
        result.setRows(rows);
        return result;
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:conversations')")
    @GetMapping("/conversations/{id:\\d+}")
    public AjaxResult getConversation(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize)
    {
        Map<String, Object> detail = conversationService.getConversationDetail(id, pageNum, pageSize);
        if (detail == null)
        {
            return AjaxResult.error(404, "Conversation not found");
        }
        return AjaxResult.success(detail);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:conversations')")
    @PostMapping("/conversations/{id:\\d+}/read")
    public AjaxResult markRead(@PathVariable Long id)
    {
        Map<String, Object> data = conversationService.markRead(id);
        if (data == null)
        {
            return AjaxResult.error(404, "Conversation not found");
        }
        return AjaxResult.success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:conversations')")
    @PostMapping("/conversations/{id:\\d+}/claim")
    public AjaxResult claim(@PathVariable Long id)
    {
        try
        {
            return AjaxResult.success(conversationService.claim(id));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:conversations') or @ss.hasPermi('crm:wechat_mp:assign')")
    @PostMapping("/conversations/{id:\\d+}/release")
    public AjaxResult release(@PathVariable Long id)
    {
        try
        {
            return AjaxResult.success(conversationService.release(id));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:assign')")
    @PostMapping("/conversations/{id:\\d+}/assign")
    public AjaxResult assign(@PathVariable Long id, @RequestBody Map<String, Object> body)
    {
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
            return AjaxResult.success(conversationService.assign(id, userId));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:conversations')")
    @PatchMapping("/conversations/{id:\\d+}")
    public AjaxResult patchConversation(@PathVariable Long id, @RequestBody Map<String, Object> body)
    {
        if (body == null)
        {
            return AjaxResult.error(400, "body is required");
        }
        if (body.containsKey("hostedByAi"))
        {
            try
            {
                return AjaxResult.success(conversationService.updateHostedByAi(id, parseHostedByAi(body.get("hostedByAi"))));
            }
            catch (ServiceException e)
            {
                return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
            }
        }
        if (body.get("leadId") == null)
        {
            return AjaxResult.error(400, "leadId is required");
        }
        try
        {
            return AjaxResult.success(conversationService.linkLead(id, String.valueOf(body.get("leadId"))));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:conversations')")
    @PatchMapping("/conversations/{id:\\d+}/hosting")
    public AjaxResult patchHosting(@PathVariable Long id, @RequestBody Map<String, Object> body)
    {
        if (body == null || !body.containsKey("hostedByAi"))
        {
            return AjaxResult.error(400, "hostedByAi is required");
        }
        try
        {
            return AjaxResult.success(conversationService.updateHostedByAi(id, parseHostedByAi(body.get("hostedByAi"))));
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

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:conversations')")
    @PostMapping("/contact/link")
    public AjaxResult linkContact(@RequestBody Map<String, Object> body)
    {
        if (body == null)
        {
            return AjaxResult.error(400, "body is required");
        }
        String appId = body.get("appId") != null ? String.valueOf(body.get("appId")) : null;
        String openId = body.get("openId") != null ? String.valueOf(body.get("openId")) : null;
        String contactId = body.get("contactId") != null ? String.valueOf(body.get("contactId")) : null;
        boolean setPrimary = Boolean.TRUE.equals(body.get("setPrimary"))
                || "true".equalsIgnoreCase(String.valueOf(body.get("setPrimary")));
        try
        {
            return AjaxResult.success(conversationService.linkContact(appId, openId, contactId, setPrimary));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }
}
