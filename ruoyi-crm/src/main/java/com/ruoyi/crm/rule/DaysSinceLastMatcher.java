package com.ruoyi.crm.rule;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.SmartPushStrategy;
import com.ruoyi.crm.mapper.CrmAppointmentMapper;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CustomerMetricsMapper;

/**
 * 距今超过N天匹配器：查找距上次指定事件已超过N天的联系人。
 * config_json 需包含 triggerEvent 和 daysThreshold 字段。
 * 策略类型: strategy_type = 'scheduled', ruleType = 'days_since_last_event'
 * <p>
 * 当前支持的事件类型：
 * <ul>
 *   <li>last_visit —— 最近一次到访超过N天（crm_contact.last_visit_at）</li>
 *   <li>last_wecom_message —— 最近一次企微消息超过N天（customer_metrics.wecom_last_message_at）</li>
 *   <li>visit_completed —— 最近一次完成预约超过N天（crm_appointment appointment_status='completed'）</li>
 * </ul>
 */
@Order(3)
@Component
public class DaysSinceLastMatcher implements RuleMatcher
{
    private static final Logger log = LoggerFactory.getLogger(DaysSinceLastMatcher.class);

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CustomerMetricsMapper customerMetricsMapper;

    @Autowired
    private CrmAppointmentMapper crmAppointmentMapper;

    @Override
    public List<CrmContact> findMatchedContacts(SmartPushStrategy strategy)
    {
        String configJson = strategy.getConfigJson();
        if (StringUtils.isBlank(configJson))
        {
            log.warn("DaysSinceLastMatcher: config_json is blank for strategy [{}]", strategy.getName());
            return Collections.emptyList();
        }

        JSONObject config = JSONObject.parseObject(configJson);
        String triggerEvent = config.getString("triggerEvent");
        Integer daysThreshold = config.getInteger("daysThreshold");

        if (StringUtils.isBlank(triggerEvent) || daysThreshold == null)
        {
            log.warn("DaysSinceLastMatcher: invalid config for strategy [{}], " +
                    "triggerEvent={}, daysThreshold={}", strategy.getName(), triggerEvent, daysThreshold);
            return Collections.emptyList();
        }

        log.info("DaysSinceLastMatcher: matching contacts for event={}, daysThreshold={}, strategy=[{}]",
                triggerEvent, daysThreshold, strategy.getName());

        List<CrmContact> contacts;

        switch (triggerEvent)
        {
            case "last_visit":
                contacts = crmContactMapper.selectContactsWithNoRecentVisit(daysThreshold);
                break;

            case "last_wecom_message":
                contacts = customerMetricsMapper.selectContactsWithNoRecentWecomMessage(daysThreshold);
                break;

            case "visit_completed":
                contacts = crmAppointmentMapper.selectContactsWithNoRecentCompletedAppointment(daysThreshold);
                break;

            default:
                log.warn("DaysSinceLastMatcher: unsupported triggerEvent [{}] for strategy [{}]",
                        triggerEvent, strategy.getName());
                return Collections.emptyList();
        }

        log.info("DaysSinceLastMatcher: found {} contacts with no {} in {} day(s) for strategy [{}]",
                contacts != null ? contacts.size() : 0, triggerEvent, daysThreshold, strategy.getName());
        return contacts != null ? contacts : Collections.emptyList();
    }

    @Override
    public boolean supports(SmartPushStrategy strategy)
    {
        if (!"scheduled".equals(strategy.getStrategyType()))
        {
            return false;
        }
        String configJson = strategy.getConfigJson();
        if (StringUtils.isBlank(configJson))
        {
            return false;
        }
        JSONObject config = JSONObject.parseObject(configJson);
        return "days_since_last_event".equals(config.getString("ruleType"));
    }
}
