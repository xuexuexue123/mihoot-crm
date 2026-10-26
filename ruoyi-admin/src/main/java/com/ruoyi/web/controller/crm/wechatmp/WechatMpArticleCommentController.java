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

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleComment;
import com.ruoyi.crm.wechatmp.dto.WechatMpArticleCommentQuery;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleCommentService;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleCommentSyncService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpArticleCommentController extends BaseController
{
    @Autowired
    private IWechatMpArticleCommentService commentService;

    @Autowired
    private IWechatMpArticleCommentSyncService syncService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @GetMapping("/comments")
    public TableDataInfo list(
            @RequestParam(required = false) String appId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String msgDataId,
            @RequestParam(required = false) Integer index,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize)
    {
        WechatMpArticleCommentQuery query = new WechatMpArticleCommentQuery();
        if (StringUtils.hasText(appId))
        {
            query.setAppId(appId);
        }
        if (StringUtils.hasText(status))
        {
            query.setStatus(status);
        }
        if (StringUtils.hasText(msgDataId))
        {
            query.setMsgDataId(msgDataId);
        }
        if (index != null)
        {
            query.setIndexInMsg(index);
        }
        if (StringUtils.hasText(keyword))
        {
            query.setKeyword(keyword);
        }
        // resolveAppFilter 必须在 PageHelper 之前，否则 selectByAppId 的 LIMIT 1 会被再叠一层
        List<CrmWechatMpArticleComment> list = commentService.selectList(query, pageNum, pageSize);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmWechatMpArticleComment row : list)
        {
            rows.add(commentService.toApiMap(row));
        }
        TableDataInfo result = getDataTable(list);
        result.setRows(rows);
        return result;
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @GetMapping("/comments/{id}")
    public AjaxResult detail(@PathVariable Long id)
    {
        return AjaxResult.success(commentService.toApiMap(commentService.selectById(id)));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comments/sync")
    public AjaxResult sync(
            @RequestParam(required = false) String appId,
            @RequestParam(required = false) String msgDataId,
            @RequestParam(required = false) Integer index,
            @RequestBody(required = false) Map<String, Object> body)
    {
        if (body != null)
        {
            if (!StringUtils.hasText(appId) && body.get("appId") != null)
            {
                appId = String.valueOf(body.get("appId"));
            }
            if (!StringUtils.hasText(msgDataId) && body.get("msgDataId") != null)
            {
                msgDataId = String.valueOf(body.get("msgDataId"));
            }
            if (index == null && body.get("index") != null)
            {
                Object idx = body.get("index");
                if (idx instanceof Number)
                {
                    index = ((Number) idx).intValue();
                }
                else if (StringUtils.hasText(String.valueOf(idx)))
                {
                    index = Integer.parseInt(String.valueOf(idx));
                }
            }
        }
        return AjaxResult.success(syncService.sync(appId, msgDataId, index));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comments/{id}/reply")
    public AjaxResult reply(@PathVariable Long id, @RequestBody Map<String, Object> body)
    {
        String content = body != null && body.get("content") != null ? String.valueOf(body.get("content")) : null;
        commentService.reply(id, content);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comments/open")
    public AjaxResult open(@RequestBody Map<String, Object> body)
    {
        String appId = bodyStr(body, "appId");
        String msgDataId = bodyStr(body, "msgDataId");
        Integer index = bodyInt(body, "index");
        commentService.openComment(appId, msgDataId, index);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comments/close")
    public AjaxResult close(@RequestBody Map<String, Object> body)
    {
        String appId = bodyStr(body, "appId");
        String msgDataId = bodyStr(body, "msgDataId");
        Integer index = bodyInt(body, "index");
        commentService.closeComment(appId, msgDataId, index);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comments/{id}/markelect")
    public AjaxResult markelect(@PathVariable Long id)
    {
        commentService.markelect(id);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comments/{id}/unmarkelect")
    public AjaxResult unmarkelect(@PathVariable Long id)
    {
        commentService.unmarkelect(id);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comments/{id}/delete")
    public AjaxResult delete(@PathVariable Long id)
    {
        commentService.deleteComment(id);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comments/{id}/reply/delete")
    public AjaxResult deleteReply(@PathVariable Long id)
    {
        commentService.deleteReply(id);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comments/{id}/read")
    public AjaxResult read(@PathVariable Long id)
    {
        commentService.markRead(id);
        return AjaxResult.success();
    }

    /**
     * 侧栏回写联系人 / 线索。字段均可选；显式传 null 或空串表示清空。
     */
    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PatchMapping("/comments/{id}")
    public AjaxResult patch(@PathVariable Long id, @RequestBody Map<String, Object> body)
    {
        if (body == null)
        {
            return AjaxResult.error(400, "body is required");
        }
        boolean touchContact = body.containsKey("contactId");
        boolean touchLead = body.containsKey("leadId");
        if (!touchContact && !touchLead)
        {
            return AjaxResult.error(400, "至少提供 contactId 或 leadId");
        }
        try
        {
            String contactId = touchContact ? nullableBodyStr(body, "contactId") : null;
            String leadId = touchLead ? nullableBodyStr(body, "leadId") : null;
            return AjaxResult.success(commentService.patchCrmLinks(id, touchContact, contactId, touchLead, leadId));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 500, e.getMessage());
        }
    }

    /** 允许显式 null / 空串（清空）；有值则返回字符串 */
    private static String nullableBodyStr(Map<String, Object> body, String key)
    {
        if (body == null || !body.containsKey(key) || body.get(key) == null)
        {
            return null;
        }
        String v = String.valueOf(body.get(key));
        return StringUtils.hasText(v) ? v.trim() : null;
    }

    private static String bodyStr(Map<String, Object> body, String key)
    {
        if (body == null || body.get(key) == null)
        {
            return null;
        }
        String v = String.valueOf(body.get(key));
        return StringUtils.hasText(v) ? v : null;
    }

    private static Integer bodyInt(Map<String, Object> body, String key)
    {
        if (body == null || body.get(key) == null)
        {
            return null;
        }
        Object v = body.get(key);
        if (v instanceof Number)
        {
            return ((Number) v).intValue();
        }
        String s = String.valueOf(v);
        if (!StringUtils.hasText(s))
        {
            return null;
        }
        return Integer.parseInt(s);
    }
}
