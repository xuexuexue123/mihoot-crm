package com.ruoyi.web.controller.crm;

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

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.dto.WecomKfConversationQuery;
import com.ruoyi.crm.service.IWecomKfConversationService;

/**
 * 微信客服坐席收件箱会话 API。
 */
@RestController
@RequestMapping("/crm/wecom/kf")
public class CrmWecomKfConversationController extends BaseController
{
    @Autowired
    private IWecomKfConversationService conversationService;

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations')")
    @GetMapping("/conversations")
    public TableDataInfo listConversations(
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String openKfid,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(required = false) String keyword)
    {
        WecomKfConversationQuery query = new WecomKfConversationQuery();
        query.setScope(scope);
        query.setOpenKfid(openKfid);
        query.setKeyword(keyword);

        // resolveOpenKfidFilter(selectFirst LIMIT 1) 必须在 PageHelper 之前，见 Service 内实现
        List<CrmWecomKfConversation> list = conversationService.selectConversationList(query, pageNum, pageSize);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmWecomKfConversation conv : list)
        {
            rows.add(conversationService.toApiMap(conv));
        }
        TableDataInfo result = getDataTable(list);
        result.setRows(rows);
        return result;
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations')")
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

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations')")
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

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations')")
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

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations') or @ss.hasPermi('crm:wecom_kf:assign')")
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

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:assign')")
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

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations')")
    @PostMapping("/conversations/{id:\\d+}/send")
    public AjaxResult send(@PathVariable Long id, @RequestBody Map<String, Object> body)
    {
        String content = null;
        String msgType = "text";
        if (body != null)
        {
            if (body.get("content") != null)
            {
                content = String.valueOf(body.get("content"));
            }
            if (body.get("msgType") != null)
            {
                msgType = String.valueOf(body.get("msgType"));
            }
        }
        if (!StringUtils.hasText(msgType))
        {
            msgType = "text";
        }
        try
        {
            return AjaxResult.success(conversationService.send(id, msgType, content));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations')")
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
}
