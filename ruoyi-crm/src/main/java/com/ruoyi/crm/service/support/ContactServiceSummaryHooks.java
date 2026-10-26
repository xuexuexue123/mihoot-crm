package com.ruoyi.crm.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.IContactServiceSummaryService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * P1 挂点：渠道消息 / contact-link → 服务摘要入队（P0 已提供 Debouncer + Worker）。
 */
@Component
public class ContactServiceSummaryHooks
{
    private static final Logger log = LoggerFactory.getLogger(ContactServiceSummaryHooks.class);

    @Autowired(required = false)
    private IContactServiceSummaryService summaryService;

    public void onMessageForContact(String tenantId, String contactId)
    {
        if (summaryService == null || StringUtils.isEmpty(contactId))
        {
            return;
        }
        String tid = StringUtils.isNotEmpty(tenantId) ? tenantId : DynamicDataSourceContextHolder.getDataSourceKey();
        try
        {
            summaryService.enqueueIncremental(tid, contactId);
        }
        catch (Exception e)
        {
            log.debug("summary incremental enqueue skipped: {}", e.getMessage());
        }
    }

    public void onContactLinked(String tenantId, String contactId)
    {
        onContactLinked(tenantId, contactId, null, null);
    }

    public void onContactLinked(String tenantId, String contactId, String channel, String conversationKey)
    {
        if (summaryService == null || StringUtils.isEmpty(contactId))
        {
            return;
        }
        String tid = StringUtils.isNotEmpty(tenantId) ? tenantId : DynamicDataSourceContextHolder.getDataSourceKey();
        try
        {
            if (StringUtils.isNotEmpty(channel) && StringUtils.isNotEmpty(conversationKey))
            {
                summaryService.onContactLinked(tid, contactId, channel, conversationKey);
            }
            else
            {
                summaryService.enqueueBootstrap(tid, contactId, "link", true);
            }
        }
        catch (Exception e)
        {
            log.debug("summary link bootstrap skipped: {}", e.getMessage());
        }
    }
}
