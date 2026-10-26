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
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CustomerMetricsMapper;

/**
 * 事件周年匹配器：查找指定事件发生在今天（同日不同年）的联系人。
 * config_json 需包含 triggerEvent 字段。
 * 策略类型: strategy_type = 'scheduled', ruleType = 'event_anniversary'
 * <p>
 * 当前支持的事件类型：
 * <ul>
 *   <li>first_visit —— 首次到访周年（crm_contact.first_visit_at）</li>
 *   <li>wecom_added —— 企微添加周年（customer_metrics.wecom_added_at）</li>
 * </ul>
 */
@Order(4)
@Component
public class EventAnniversaryMatcher implements RuleMatcher
{
    private static final Logger log = LoggerFactory.getLogger(EventAnniversaryMatcher.class);

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CustomerMetricsMapper customerMetricsMapper;

    @Override
    public List<CrmContact> findMatchedContacts(SmartPushStrategy strategy)
    {
        String configJson = strategy.getConfigJson();
        if (StringUtils.isBlank(configJson))
        {
            log.warn("EventAnniversaryMatcher: config_json is blank for strategy [{}]", strategy.getName());
            return Collections.emptyList();
        }

        JSONObject config = JSONObject.parseObject(configJson);
        String triggerEvent = config.getString("triggerEvent");

        if (StringUtils.isBlank(triggerEvent))
        {
            log.warn("EventAnniversaryMatcher: invalid config for strategy [{}], " +
                    "triggerEvent is blank", strategy.getName());
            return Collections.emptyList();
        }

        log.info("EventAnniversaryMatcher: matching contacts for event={}, strategy=[{}]",
                triggerEvent, strategy.getName());

        List<CrmContact> contacts;

        switch (triggerEvent)
        {
            case "first_visit":
                contacts = crmContactMapper.selectContactsByFirstVisitAnniversary();
                break;

            case "wecom_added":
                contacts = customerMetricsMapper.selectContactsByWecomAddedAnniversary();
                break;

            default:
                log.warn("EventAnniversaryMatcher: unsupported triggerEvent [{}] for strategy [{}]",
                        triggerEvent, strategy.getName());
                return Collections.emptyList();
        }

        log.info("EventAnniversaryMatcher: found {} contacts for {} anniversary, strategy=[{}]",
                contacts != null ? contacts.size() : 0, triggerEvent, strategy.getName());
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
        return "event_anniversary".equals(config.getString("ruleType"));
    }
}
