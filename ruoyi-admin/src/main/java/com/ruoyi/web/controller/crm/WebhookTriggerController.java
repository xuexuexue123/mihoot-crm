package com.ruoyi.web.controller.crm;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.dto.WebhookTriggerResolveRequest;
import com.ruoyi.crm.service.IWebhookTriggerResolveService;
import com.ruoyi.crm.service.support.WebhookBffSecretValidator;
import com.ruoyi.crm.service.support.WebhookPathIndexSupport;

/**
 * WEBHOOK_TRIGGER BFF 专用 API（非 Flowable {@code WebhookFlowController}）
 */
@RestController
@RequestMapping("/crm/webhook-trigger")
public class WebhookTriggerController extends BaseController
{
    @Autowired
    private IWebhookTriggerResolveService webhookTriggerResolveService;

    @Autowired
    private WebhookBffSecretValidator webhookBffSecretValidator;

    @Autowired
    private WebhookPathIndexSupport webhookPathIndexSupport;

    /**
     * 根据 path 解析 WEBHOOK_TRIGGER 节点，返回 crmAuth 供 Next.js 触发 Inngest
     */
    @PostMapping("/resolve")
    public AjaxResult resolve(@RequestBody WebhookTriggerResolveRequest request, HttpServletRequest httpRequest)
    {
        webhookBffSecretValidator.validate(httpRequest);
        return success(webhookTriggerResolveService.resolve(request));
    }

    /**
     * 为当前租户所有 WEBHOOK_TRIGGER 节点重新生成加密 publicPath（历史数据迁移）
     */
    @org.springframework.security.access.prepost.PreAuthorize("@ss.hasPermi('crm:wworkflow:edit')")
    @PostMapping("/rebuild-index")
    public AjaxResult rebuildIndex()
    {
        String tenantId = webhookPathIndexSupport.requireCurrentTenantId();
        int count = webhookPathIndexSupport.rebuildPublicPathsForCurrentTenant(tenantId);
        return success(java.util.Collections.singletonMap("rebuiltNodes", count));
    }
}
