package com.ruoyi.crm.service.whatsapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappTemplate;
import com.ruoyi.crm.domain.CrmWhatsappTemplateSendItem;
import com.ruoyi.crm.domain.CrmWhatsappTemplateSendTask;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateSendItemMapper;
import com.ruoyi.crm.mapper.CrmWhatsappTemplateSendTaskMapper;
import com.ruoyi.crm.service.ICrmWhatsappTemplateSendTaskService;
import com.ruoyi.crm.whatsapp.WhatsAppTemplateStatuses;

@Service
public class CrmWhatsappTemplateSendTaskServiceImpl implements ICrmWhatsappTemplateSendTaskService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappTemplateSendTaskServiceImpl.class);

    /** WhatsApp wa_id：纯数字，至少 8 位 */
    private static final Pattern WA_ID_PATTERN = Pattern.compile("^[0-9]{8,20}$");

    @Autowired
    private CrmWhatsappTemplateSendTaskMapper taskMapper;

    @Autowired
    private CrmWhatsappTemplateSendItemMapper itemMapper;

    @Autowired
    private CrmWhatsappTemplateMapper templateMapper;

    @Autowired
    private CrmWhatsappConfigMapper configMapper;

    @Autowired
    private WhatsappTemplateSendWorker sendWorker;

    @Autowired
    private WhatsappAudienceResolveService audienceResolveService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> enqueueSend(String tenantId, String templateName, String language,
                                           List<String> waIds, List<String> tagIds, String segmentId,
                                           String phoneNumberId, List<Map<String, Object>> components)
    {
        validateTemplateAndConfig(tenantId, templateName, language);

        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
        String actualPhoneNumberId = StringUtils.isNotEmpty(phoneNumberId)
                ? phoneNumberId : config.getPhoneNumberId();

        boolean useAudienceResolve = (tagIds != null && !tagIds.isEmpty())
                || StringUtils.isNotEmpty(segmentId);

        String audienceType;
        Map<String, Object> audienceRef = new HashMap<>();
        List<CrmWhatsappTemplateSendItem> items = new ArrayList<>();
        Date now = new Date();
        int skipped = 0;

        if (useAudienceResolve)
        {
            Map<String, Object> resolved = audienceResolveService.resolve(tenantId, tagIds, segmentId);
            List<WhatsappAudienceResolveService.ResolvedRecipient> recipients =
                    audienceResolveService.toRecipients(resolved);
            List<Map<String, Object>> skippedRows = audienceResolveService.skippedFrom(resolved);

            if (tagIds != null && !tagIds.isEmpty())
            {
                audienceType = "tag";
                audienceRef.put("tagIds", tagIds);
                if (StringUtils.isNotEmpty(segmentId))
                {
                    audienceRef.put("segmentId", segmentId);
                }
            }
            else
            {
                audienceType = "segment";
                audienceRef.put("segmentId", segmentId);
            }

            for (WhatsappAudienceResolveService.ResolvedRecipient r : recipients)
            {
                CrmWhatsappTemplateSendItem item = newItem(tenantId, null, r.waId, r.contactId, now);
                item.setStatus("pending");
                items.add(item);
            }
            for (Map<String, Object> skip : skippedRows)
            {
                String contactId = skip.get("contactId") != null ? String.valueOf(skip.get("contactId")) : null;
                CrmWhatsappTemplateSendItem item = newItem(tenantId, null, "unknown", contactId, now);
                item.setStatus("skipped");
                item.setErrorCode(WhatsappAudienceResolveService.REASON_NO_WHATSAPP_IDENTITY);
                item.setErrorMessage("No WhatsApp identity");
                // wa_id NOT NULL — use placeholder for skipped without wa
                item.setWaId(contactId != null ? ("skipped:" + contactId) : ("skipped:" + items.size()));
                items.add(item);
                skipped++;
            }

            if (recipients.isEmpty())
            {
                throw new ServiceException("No sendable WhatsApp recipients after audience resolve", 400);
            }
        }
        else
        {
            audienceType = "wa_ids";
            Set<String> unique = new LinkedHashSet<>();
            if (waIds != null)
            {
                for (String raw : waIds)
                {
                    if (raw != null)
                    {
                        unique.add(raw.trim());
                    }
                }
            }
            if (unique.isEmpty())
            {
                throw new ServiceException("waIds must not be empty (or provide tagIds/segmentId)", 400);
            }
            audienceRef.put("waIds", new ArrayList<>(unique));

            for (String waId : unique)
            {
                CrmWhatsappTemplateSendItem item = newItem(tenantId, null, waId, null, now);
                if (!isValidWaId(waId))
                {
                    item.setStatus("skipped");
                    item.setErrorCode("invalid_wa_id");
                    item.setErrorMessage("Invalid WhatsApp wa_id");
                    skipped++;
                }
                else
                {
                    item.setStatus("pending");
                }
                items.add(item);
            }
        }

        CrmWhatsappTemplateSendTask task = new CrmWhatsappTemplateSendTask();
        task.setTenantId(tenantId);
        task.setPhoneNumberId(actualPhoneNumberId);
        task.setTemplateName(templateName);
        task.setLanguage(language);
        task.setComponents(components != null ? JSON.toJSONString(components) : null);
        task.setAudienceType(audienceType);
        task.setAudienceRef(JSON.toJSONString(audienceRef));
        task.setStatus("pending");
        task.setTotalCount(items.size());
        task.setSuccessCount(0);
        task.setFailedCount(0);
        task.setSkippedCount(skipped);
        try
        {
            task.setCreatedBy(SecurityUtils.getUsername());
        }
        catch (Exception ignored)
        {
            task.setCreatedBy(null);
        }
        task.setCreatedAt(now);
        taskMapper.insert(task);

        for (CrmWhatsappTemplateSendItem item : items)
        {
            item.setTaskId(task.getId());
        }
        // batch in chunks to avoid oversized SQL
        final int batchSize = 500;
        for (int i = 0; i < items.size(); i += batchSize)
        {
            itemMapper.insertBatch(items.subList(i, Math.min(i + batchSize, items.size())));
        }

        int pendingCount = items.size() - skipped;
        if (pendingCount == 0)
        {
            taskMapper.markFinished(task.getId(), "failed", "No valid recipients to send", now, 0, 0, skipped);
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", task.getId());
            data.put("totalCount", items.size());
            data.put("status", "failed");
            return data;
        }

        scheduleSendAfterCommit(task.getId(), tenantId);

        Map<String, Object> data = new HashMap<>();
        data.put("taskId", task.getId());
        data.put("totalCount", items.size());
        data.put("status", "pending");
        log.info("WhatsApp template send task enqueued tenant={} taskId={} total={} audienceType={}",
                tenantId, task.getId(), items.size(), audienceType);
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> enqueueSendForAutomation(String tenantId, String templateName, String language,
                                                        String waId, String contactId, String phoneNumberId,
                                                        List<Map<String, Object>> components,
                                                        Long ruleId, String businessKey)
    {
        if (ruleId != null && StringUtils.isNotEmpty(businessKey)
                && itemMapper.countByRuleAndBusinessKey(ruleId, businessKey) > 0)
        {
            log.info("Skip automation send: already sent ruleId={} businessKey={}", ruleId, businessKey);
            return null;
        }

        validateTemplateAndConfig(tenantId, templateName, language);
        if (!isValidWaId(waId))
        {
            throw new ServiceException("Invalid WhatsApp wa_id: " + waId, 400);
        }

        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
        String actualPhoneNumberId = StringUtils.isNotEmpty(phoneNumberId)
                ? phoneNumberId : config.getPhoneNumberId();

        Date now = new Date();
        CrmWhatsappTemplateSendTask task = new CrmWhatsappTemplateSendTask();
        task.setTenantId(tenantId);
        task.setPhoneNumberId(actualPhoneNumberId);
        task.setTemplateName(templateName);
        task.setLanguage(language);
        task.setComponents(components != null ? JSON.toJSONString(components) : null);
        task.setAudienceType("wa_ids");
        Map<String, Object> audienceRef = new HashMap<>();
        audienceRef.put("waIds", java.util.Collections.singletonList(waId));
        audienceRef.put("ruleId", ruleId);
        audienceRef.put("businessKey", businessKey);
        task.setAudienceRef(JSON.toJSONString(audienceRef));
        task.setStatus("pending");
        task.setTotalCount(1);
        task.setSuccessCount(0);
        task.setFailedCount(0);
        task.setSkippedCount(0);
        task.setCreatedBy("automation");
        task.setCreatedAt(now);
        taskMapper.insert(task);

        CrmWhatsappTemplateSendItem item = newItem(tenantId, task.getId(), waId, contactId, now);
        item.setRuleId(ruleId);
        item.setBusinessKey(businessKey);
        item.setStatus("pending");
        try
        {
            itemMapper.insert(item);
        }
        catch (Exception ex)
        {
            log.info("Skip automation send on conflict ruleId={} businessKey={}: {}",
                    ruleId, businessKey, ex.getMessage());
            taskMapper.markFinished(task.getId(), "cancelled", "duplicate business_key", now, 0, 0, 1);
            return null;
        }

        scheduleSendAfterCommit(task.getId(), tenantId);

        Map<String, Object> data = new HashMap<>();
        data.put("taskId", task.getId());
        data.put("totalCount", 1);
        data.put("status", "pending");
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> retryFailed(String tenantId, Long taskId)
    {
        CrmWhatsappTemplateSendTask task = taskMapper.selectById(taskId, tenantId);
        if (task == null)
        {
            throw new ServiceException("Task not found", 404);
        }

        int failedBefore = itemMapper.countByTaskAndStatus(taskId, "failed");
        if (failedBefore == 0)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            data.put("retriedCount", 0);
            data.put("status", task.getStatus());
            return data;
        }

        int reset = itemMapper.resetFailedToPending(taskId, tenantId);
        int remainingFailed = Math.max(0,
                (task.getFailedCount() != null ? task.getFailedCount() : failedBefore) - reset);
        taskMapper.reopenForRetry(taskId, tenantId, remainingFailed);

        scheduleSendAfterCommit(taskId, tenantId);

        Map<String, Object> data = new HashMap<>();
        data.put("taskId", taskId);
        data.put("retriedCount", reset);
        data.put("status", "running");
        log.info("WhatsApp template send retry-failed tenant={} taskId={} retried={}",
                tenantId, taskId, reset);
        return data;
    }

    private void scheduleSendAfterCommit(Long taskId, String tenantId)
    {
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    sendWorker.sendAsync(taskId, tenantId);
                }
            });
        }
        else
        {
            sendWorker.sendAsync(taskId, tenantId);
        }
    }

    @Override
    public List<CrmWhatsappTemplateSendTask> listTasks(String tenantId, String status)
    {
        return taskMapper.selectPage(tenantId, status);
    }

    @Override
    public CrmWhatsappTemplateSendTask getTask(String tenantId, Long taskId)
    {
        return taskMapper.selectById(taskId, tenantId);
    }

    @Override
    public List<CrmWhatsappTemplateSendItem> listItems(String tenantId, Long taskId, boolean failedFirst)
    {
        return itemMapper.selectByTaskId(taskId, tenantId, failedFirst);
    }

    private static CrmWhatsappTemplateSendItem newItem(String tenantId, Long taskId, String waId,
                                                       String contactId, Date now)
    {
        CrmWhatsappTemplateSendItem item = new CrmWhatsappTemplateSendItem();
        item.setTenantId(tenantId);
        item.setTaskId(taskId);
        item.setWaId(waId);
        item.setContactId(contactId);
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        return item;
    }

    private void validateTemplateAndConfig(String tenantId, String templateName, String language)
    {
        CrmWhatsappTemplate template = templateMapper.selectByTemplate(tenantId, templateName, language);
        if (template == null || !WhatsAppTemplateStatuses.isApproved(template.getStatus()))
        {
            throw new ServiceException("Template not found or not approved: " + templateName, 400);
        }
        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null || config.getAccessTokenEnc() == null)
        {
            throw new ServiceException("WhatsApp configuration not found", 400);
        }
    }

    static boolean isValidWaId(String waId)
    {
        return waId != null && WA_ID_PATTERN.matcher(waId.trim()).matches();
    }
}
