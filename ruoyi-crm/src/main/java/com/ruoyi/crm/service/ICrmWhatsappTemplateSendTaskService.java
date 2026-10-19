package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.CrmWhatsappTemplateSendItem;
import com.ruoyi.crm.domain.CrmWhatsappTemplateSendTask;

/**
 * WhatsApp 模板群发任务（营销异步通道）
 */
public interface ICrmWhatsappTemplateSendTaskService
{
    /**
     * 创建任务并入队。有 tagIds/segmentId 时优先服务端解析受众，忽略 waIds。
     *
     * @return taskId / totalCount / status
     */
    Map<String, Object> enqueueSend(String tenantId, String templateName, String language,
                                    List<String> waIds, List<String> tagIds, String segmentId,
                                    String phoneNumberId, List<Map<String, Object>> components);

    /**
     * 自动化 / 内部：带 ruleId + businessKey 的单人（或少量）入队。
     * 若 (ruleId, businessKey) 已存在则跳过并返回 null。
     */
    Map<String, Object> enqueueSendForAutomation(String tenantId, String templateName, String language,
                                                 String waId, String contactId, String phoneNumberId,
                                                 List<Map<String, Object>> components,
                                                 Long ruleId, String businessKey);

    List<CrmWhatsappTemplateSendTask> listTasks(String tenantId, String status);

    CrmWhatsappTemplateSendTask getTask(String tenantId, Long taskId);

    List<CrmWhatsappTemplateSendItem> listItems(String tenantId, Long taskId, boolean failedFirst);

    /**
     * 仅重试 failed items → pending 后重新入队。
     *
     * @return taskId / retriedCount / status
     */
    Map<String, Object> retryFailed(String tenantId, Long taskId);
}
