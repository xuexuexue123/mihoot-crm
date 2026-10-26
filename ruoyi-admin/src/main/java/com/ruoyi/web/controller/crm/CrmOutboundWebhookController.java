package com.ruoyi.web.controller.crm;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.OutboundWebhookSaveRequest;
import com.ruoyi.crm.domain.vo.OutboundWebhookDeliveryVo;
import com.ruoyi.crm.domain.vo.OutboundWebhookVo;
import com.ruoyi.crm.service.ICrmOutboundWebhookService;

@RestController
@RequestMapping("/crm/outbound-webhook")
public class CrmOutboundWebhookController extends BaseController
{
    @Autowired
    private ICrmOutboundWebhookService outboundWebhookService;

    @PreAuthorize("@ss.hasPermi('crm:outboundwebhook:query')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "enabled", required = false) String enabled)
    {
        startPage();
        List<OutboundWebhookVo> list = outboundWebhookService.selectList(tenant, eventType, enabled);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:outboundwebhook:query')")
    @GetMapping("/{webhookId}")
    public AjaxResult getById(@PathVariable("webhookId") String webhookId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(outboundWebhookService.getById(tenant, webhookId));
    }

    @PreAuthorize("@ss.hasPermi('crm:outboundwebhook:add')")
    @Log(title = "出站Webhook", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OutboundWebhookSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        outboundWebhookService.add(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:outboundwebhook:edit')")
    @Log(title = "出站Webhook", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody OutboundWebhookSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        outboundWebhookService.update(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:outboundwebhook:remove')")
    @Log(title = "出站Webhook", businessType = BusinessType.DELETE)
    @DeleteMapping("/{webhookId}")
    public AjaxResult remove(@PathVariable("webhookId") String webhookId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        outboundWebhookService.remove(tenant, webhookId);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:outboundwebhook:edit')")
    @Log(title = "出站Webhook测试", businessType = BusinessType.OTHER)
    @PostMapping("/{webhookId}/test")
    public AjaxResult test(@PathVariable("webhookId") String webhookId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        return success(outboundWebhookService.test(tenant, webhookId));
    }

    @PreAuthorize("@ss.hasPermi('crm:outboundwebhook:query')")
    @GetMapping("/{webhookId}/deliveries")
    public TableDataInfo deliveries(@PathVariable("webhookId") String webhookId,
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(value = "status", required = false) String status)
    {
        if (outboundWebhookService.getById(tenant, webhookId) == null)
        {
            return getDataTable(java.util.Collections.emptyList());
        }
        startPage();
        List<OutboundWebhookDeliveryVo> list = outboundWebhookService.selectDeliveries(tenant, webhookId, status);
        return getDataTable(list);
    }
}
