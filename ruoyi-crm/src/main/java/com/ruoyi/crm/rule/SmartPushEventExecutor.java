package com.ruoyi.crm.rule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.SmartPushStrategy;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.service.ISmartPushStrategyService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 实时事件推送执行器，在业务方法中埋点触发，@Async 异步执行。
 * <p>
 * 支持的事件类型：
 * <ul>
 *   <li>lead_created —— lead 创建后</li>
 *   <li>lead_converted —— lead 转化后</li>
 *   <li>opty_won —— 商机赢单后</li>
 *   <li>appointment_booked —— 预约创建后</li>
 *   <li>payment_completed —— 付款完成后</li>
 * </ul>
 */
@Service
public class SmartPushEventExecutor
{
    private static final Logger log = LoggerFactory.getLogger(SmartPushEventExecutor.class);

    @Autowired
    private ISmartPushStrategyService strategyService;

    @Autowired
    private SmartPushExecutor executor;

    @Autowired
    private CrmContactMapper crmContactMapper;

    /**
     * 异步处理实时事件推送。
     * 由于 @Async 运行在独立线程中，需显式传递 tenantKey 以恢复数据源上下文。
     *
     * @param eventType 事件类型 (lead_created, lead_converted, opty_won, etc.)
     * @param contactId 关联的联系人 ID（可为 null）
     * @param tenantKey 租户数据源 key（来自 DynamicDataSourceContextHolder）
     */
    @Async("smartPushTaskExecutor")
    public void onEvent(String eventType, String contactId, String tenantKey)
    {
        if (StringUtils.isNotBlank(tenantKey))
        {
            DynamicDataSourceContextHolder.setDataSourceKey(tenantKey);
        }
        try
        {
            doOnEvent(eventType, contactId);
        }
        finally
        {
            if (StringUtils.isNotBlank(tenantKey))
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
    }

    private void doOnEvent(String eventType, String contactId)
    {
        if (StringUtils.isBlank(contactId))
        {
            log.debug("SmartPushEventExecutor.onEvent({}): contactId is blank, skip", eventType);
            return;
        }

        CrmContact contact = crmContactMapper.selectCrmContactByContactId(contactId);
        if (contact == null)
        {
            log.debug("SmartPushEventExecutor.onEvent({}): contact not found for contactId={}, skip",
                    eventType, contactId);
            return;
        }

        log.info("SmartPushEventExecutor: received event [{}] for contact [{}] (id={})",
                eventType, contact.getName(), contact.getContactId());

        List<SmartPushStrategy> strategies = strategyService.selectEnabledOnEvent(eventType);
        if (strategies == null || strategies.isEmpty())
        {
            log.debug("SmartPushEventExecutor: no enabled on_event strategies for event [{}]", eventType);
            return;
        }

        log.info("SmartPushEventExecutor: executing {} strategies for event [{}] on contact [{}]",
                strategies.size(), eventType, contact.getName());

        for (SmartPushStrategy strategy : strategies)
        {
            executor.pushOneContact(strategy, contact, eventType);
        }
    }
}
