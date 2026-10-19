package com.ruoyi.web.controller.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWhatsappTemplate;
import com.ruoyi.crm.domain.CrmWhatsappTemplateSendItem;
import com.ruoyi.crm.domain.CrmWhatsappTemplateSendTask;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateMapper;
import com.ruoyi.crm.service.ICrmWhatsappTemplateSendTaskService;
import com.ruoyi.crm.service.ICrmWhatsappTemplateService;
import com.ruoyi.crm.service.whatsapp.WhatsappAudienceResolveService;

@RestController
@RequestMapping("/crm/whatsapp")
public class CrmWhatsappTemplateController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappTemplateController.class);

    @Autowired
    private CrmWhatsappTemplateMapper templateMapper;

    @Autowired
    private ICrmWhatsappTemplateService templateService;

    @Autowired
    private ICrmWhatsappTemplateSendTaskService sendTaskService;

    @Autowired
    private WhatsappAudienceResolveService audienceResolveService;

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:templates') or @ss.hasPermi('crm:whatsapp:marketing')")
    @GetMapping("/templates")
    public Object listTemplates(
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean sync)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return getDataTable(java.util.Collections.emptyList());
        }

        if (sync)
        {
            try
            {
                templateService.syncTemplates(tenant);
            }
            catch (ServiceException e)
            {
                log.warn("GET /crm/whatsapp/templates sync failed tenant={} msg={}", tenant, e.getMessage());
                return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
            }
        }

        PageHelper.startPage(pageNum, pageSize);
        List<CrmWhatsappTemplate> list = templateMapper.selectPage(
                tenant, status, category, language, keyword);
        return getDataTable(list);
    }

    /**
     * 营销群发：异步入队，立即返回 taskId。支持 waIds 或 tagIds/segmentId。
     */
    @PreAuthorize("@ss.hasPermi('crm:whatsapp:templates') or @ss.hasPermi('crm:whatsapp:campaign')")
    @PostMapping("/template/send")
    public AjaxResult sendTemplate(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            String templateName = (String) body.get("templateName");
            String language = (String) body.get("language");
            List<String> waIds = toStringList(body.get("waIds"));
            List<String> tagIds = toStringList(body.get("tagIds"));
            String segmentId = body.get("segmentId") != null ? String.valueOf(body.get("segmentId")) : null;
            if (segmentId != null && ("null".equalsIgnoreCase(segmentId) || segmentId.isEmpty()))
            {
                segmentId = null;
            }
            String phoneNumberId = (String) body.get("phoneNumberId");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> components = (List<Map<String, Object>>) body.get("components");

            log.info("POST /crm/whatsapp/template/send tenant={} template={} lang={} waIdCount={} "
                            + "tagIdCount={} segmentId={} hasComponents={}",
                    tenant, templateName, language, waIds != null ? waIds.size() : 0,
                    tagIds != null ? tagIds.size() : 0, segmentId,
                    components != null && !components.isEmpty());

            Map<String, Object> result = sendTaskService.enqueueSend(
                    tenant, templateName, language, waIds, tagIds, segmentId, phoneNumberId, components);
            return AjaxResult.success("Template send task created", result);
        }
        catch (ServiceException e)
        {
            log.warn("POST /crm/whatsapp/template/send rejected tenant={} msg={}", tenant, e.getMessage());
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    /**
     * 受众预估：不入队。
     */
    @PreAuthorize("@ss.hasPermi('crm:whatsapp:templates') or @ss.hasPermi('crm:whatsapp:campaign')")
    @PostMapping("/audience/resolve")
    public AjaxResult resolveAudience(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            List<String> tagIds = toStringList(body.get("tagIds"));
            String segmentId = body.get("segmentId") != null ? String.valueOf(body.get("segmentId")) : null;
            if (segmentId != null && ("null".equalsIgnoreCase(segmentId) || segmentId.isEmpty()))
            {
                segmentId = null;
            }
            Map<String, Object> resolved = audienceResolveService.resolve(tenant, tagIds, segmentId);
            Map<String, Object> data = new HashMap<>();
            data.put("waIds", resolved.get("waIds"));
            data.put("skipped", resolved.get("skipped"));
            return AjaxResult.success(data);
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:templates') or @ss.hasPermi('crm:whatsapp:marketing') or @ss.hasPermi('crm:whatsapp:campaign')")
    @GetMapping("/template/send/tasks")
    public TableDataInfo listSendTasks(
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String status)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return getDataTable(java.util.Collections.emptyList());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<CrmWhatsappTemplateSendTask> list = sendTaskService.listTasks(tenant, status);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:templates') or @ss.hasPermi('crm:whatsapp:marketing') or @ss.hasPermi('crm:whatsapp:campaign')")
    @GetMapping("/template/send/tasks/{id}")
    public AjaxResult getSendTask(
            @PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(defaultValue = "true") boolean failedFirst)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        CrmWhatsappTemplateSendTask task = sendTaskService.getTask(tenant, id);
        if (task == null)
        {
            return AjaxResult.error(404, "Task not found");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<CrmWhatsappTemplateSendItem> items = sendTaskService.listItems(tenant, id, failedFirst);
        TableDataInfo itemsPage = getDataTable(items);

        Map<String, Object> data = new HashMap<>();
        data.put("task", task);
        data.put("items", itemsPage.getRows());
        data.put("itemTotal", itemsPage.getTotal());
        return AjaxResult.success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:templates') or @ss.hasPermi('crm:whatsapp:campaign')")
    @PostMapping("/template/send/tasks/{id}/retry-failed")
    public AjaxResult retryFailed(
            @PathVariable("id") Long id,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            Map<String, Object> result = sendTaskService.retryFailed(tenant, id);
            return AjaxResult.success("Retry submitted", result);
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    private static List<String> toStringList(Object raw)
    {
        if (!(raw instanceof List))
        {
            return null;
        }
        List<?> list = (List<?>) raw;
        List<String> out = new ArrayList<>();
        for (Object o : list)
        {
            if (o != null)
            {
                out.add(String.valueOf(o));
            }
        }
        return out;
    }
}
