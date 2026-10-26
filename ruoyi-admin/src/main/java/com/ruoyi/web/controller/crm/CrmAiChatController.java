package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.AiChatMessageBatchRequest;
import com.ruoyi.crm.domain.dto.AiChatSessionCreateRequest;
import com.ruoyi.crm.domain.vo.AiChatMessageVo;
import com.ruoyi.crm.domain.vo.AiChatSessionVo;
import com.ruoyi.crm.service.ICrmAiChatService;

/**
 * AI Assistant 对话历史持久化
 */
@RestController
@RequestMapping("/crm/aichat")
public class CrmAiChatController extends BaseController
{
    @Autowired
    private ICrmAiChatService crmAiChatService;

    @PreAuthorize("@ss.hasPermi('crm:aichat:query')")
    @GetMapping("/session")
    public TableDataInfo listSessions(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        startPage();
        List<AiChatSessionVo> list = crmAiChatService.listSessions(tenant);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:aichat:add')")
    @Log(title = "AI Chat 会话", businessType = BusinessType.INSERT)
    @PostMapping("/session")
    public AjaxResult createOrTouchSession(@RequestBody AiChatSessionCreateRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        AiChatSessionVo data = crmAiChatService.createOrTouchSession(tenant, body);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:aichat:query')")
    @GetMapping("/session/{sessionId}/messages")
    public AjaxResult listMessages(@PathVariable("sessionId") String sessionId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<AiChatMessageVo> data = crmAiChatService.listMessages(tenant, sessionId);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:aichat:add')")
    @Log(title = "AI Chat 消息", businessType = BusinessType.INSERT)
    @PostMapping("/session/{sessionId}/messages")
    public AjaxResult appendMessages(@PathVariable("sessionId") String sessionId,
            @RequestBody AiChatMessageBatchRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmAiChatService.appendMessages(tenant, sessionId, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:aichat:remove')")
    @Log(title = "AI Chat 会话", businessType = BusinessType.DELETE)
    @DeleteMapping("/session/{sessionId}")
    public AjaxResult deleteSession(@PathVariable("sessionId") String sessionId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmAiChatService.deleteSession(tenant, sessionId);
        return success();
    }
}
