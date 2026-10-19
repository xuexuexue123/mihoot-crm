package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

/**
 * WhatsApp 模板：同步缓存 + Inbox 单发路径保留在 MessageService；
 * 营销群发走 {@link ICrmWhatsappTemplateSendTaskService}。
 */
public interface ICrmWhatsappTemplateService
{
    void syncTemplates(String tenantId);

    /**
     * @deprecated 营销群发请使用 {@link ICrmWhatsappTemplateSendTaskService#enqueueSend}。
     * 保留仅供兼容；实现已委托异步任务。
     */
    @Deprecated
    Map<String, Object> sendTemplateMessage(String tenantId, String templateName, String language,
                                            List<String> waIds, String phoneNumberId,
                                            List<Map<String, Object>> components);
}
