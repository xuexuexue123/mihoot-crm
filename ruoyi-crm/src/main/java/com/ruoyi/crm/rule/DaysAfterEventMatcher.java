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
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.mapper.CustomerMetricsMapper;

/**
 * 事件后N天匹配器：查找在指定事件发生恰好N天后的联系人。
 * config_json 需包含 triggerEvent 和 daysAfter 字段。
 * 策略类型: strategy_type = 'scheduled', ruleType = 'days_after_event'
 * <p>
 * 当前支持的事件类型：
 * <ul>
 *   <li>opty_won —— 商机赢单（crm_opty stage='closed_won', close_date）</li>
 *   <li>lead_converted —— 线索转化（crm_lead status='converted', convert_time）</li>
 *   <li>wecom_added —— 添加企微好友（customer_metrics.wecom_added_at）</li>
 *   <li>wechat_mp_followed —— 关注公众号（customer_metrics.wechat_mp_followed_at）</li>
 * </ul>
 */
@Order(2)
@Component
public class DaysAfterEventMatcher implements RuleMatcher
{
    private static final Logger log = LoggerFactory.getLogger(DaysAfterEventMatcher.class);

    @Autowired
    private CrmOptyMapper crmOptyMapper;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CustomerMetricsMapper customerMetricsMapper;

    @Override
    public List<CrmContact> findMatchedContacts(SmartPushStrategy strategy)
    {
        String configJson = strategy.getConfigJson();
        if (StringUtils.isBlank(configJson))
        {
            log.warn("DaysAfterEventMatcher: config_json is blank for strategy [{}]", strategy.getName());
            return Collections.emptyList();
        }

        JSONObject config = JSONObject.parseObject(configJson);
        String triggerEvent = config.getString("triggerEvent");
        Integer daysAfter = config.getInteger("daysAfter");

        if (StringUtils.isBlank(triggerEvent) || daysAfter == null)
        {
            log.warn("DaysAfterEventMatcher: invalid config for strategy [{}], " +
                    "triggerEvent={}, daysAfter={}", strategy.getName(), triggerEvent, daysAfter);
            return Collections.emptyList();
        }

        log.info("DaysAfterEventMatcher: matching contacts for event={}, daysAfter={}, strategy=[{}]",
                triggerEvent, daysAfter, strategy.getName());

        List<CrmContact> contacts;

        switch (triggerEvent)
        {
            case "opty_won":
                contacts = crmOptyMapper.selectContactsByWonOptyDaysAgo(daysAfter);
                break;

            case "lead_converted":
                contacts = crmLeadMapper.selectContactsByConvertedLeadDaysAgo(daysAfter);
                break;

            case "wecom_added":
                contacts = customerMetricsMapper.selectContactsByWecomAddedDaysAgo(daysAfter);
                break;

            case "wechat_mp_followed":
                contacts = customerMetricsMapper.selectContactsByWechatMpFollowedDaysAgo(daysAfter);
                break;

            default:
                log.warn("DaysAfterEventMatcher: unsupported triggerEvent [{}] for strategy [{}]",
                        triggerEvent, strategy.getName());
                return Collections.emptyList();
        }

        log.info("DaysAfterEventMatcher: found {} contacts for event={} {} day(s) ago, strategy=[{}]",
                contacts != null ? contacts.size() : 0, triggerEvent, daysAfter, strategy.getName());
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
        return "days_after_event".equals(config.getString("ruleType"));
    }
}
