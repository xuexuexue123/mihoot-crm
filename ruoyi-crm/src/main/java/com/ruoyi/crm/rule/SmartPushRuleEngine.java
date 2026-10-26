package com.ruoyi.crm.rule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.SmartPushStrategy;
import com.ruoyi.crm.service.ISmartPushStrategyService;

/**
 * 智能推送规则引擎，负责调度规则匹配器执行定时推送策略。
 * 通过 Spring 注入所有 RuleMatcher 实现，自动发现匹配的匹配器。
 */
@Service
public class SmartPushRuleEngine
{
    private static final Logger log = LoggerFactory.getLogger(SmartPushRuleEngine.class);

    @Autowired
    private ISmartPushStrategyService strategyService;

    @Autowired
    private SmartPushExecutor executor;

    @Autowired
    private List<RuleMatcher> matchers;

    /**
     * 执行所有启用的 scheduled 类型定时推送策略（不含 birthday）。
     */
    public void executeScheduled()
    {
        List<SmartPushStrategy> strategies = strategyService.selectEnabledScheduled();
        if (strategies == null || strategies.isEmpty())
        {
            log.info("SmartPushRuleEngine: no enabled scheduled strategies found");
            return;
        }

        log.info("SmartPushRuleEngine: executing {} enabled scheduled strategies", strategies.size());

        for (SmartPushStrategy strategy : strategies)
        {
            executeStrategy(strategy);
        }
    }

    /**
     * 执行所有启用的生日推送策略。
     * 仅由每天 08:00 定时任务调用，手动触发 API 不调用此方法。
     */
    public void executeBirthday()
    {
        List<SmartPushStrategy> strategies = strategyService.selectEnabledBirthday();
        if (strategies == null || strategies.isEmpty())
        {
            log.info("SmartPushRuleEngine: no enabled birthday strategies found");
            return;
        }

        log.info("SmartPushRuleEngine: executing {} enabled birthday strategies", strategies.size());

        for (SmartPushStrategy strategy : strategies)
        {
            executeStrategy(strategy);
        }
    }

    /**
     * 执行单条策略。
     */
    public void executeStrategy(SmartPushStrategy strategy)
    {
        RuleMatcher matcher = findMatcher(strategy);
        if (matcher == null)
        {
            log.warn("SmartPushRuleEngine: no matcher found for strategy [{}] (type={})",
                    strategy.getName(), strategy.getStrategyType());
            return;
        }

        log.info("SmartPushRuleEngine: executing strategy [{}] with matcher [{}]",
                strategy.getName(), matcher.getClass().getSimpleName());

        List<CrmContact> contacts = matcher.findMatchedContacts(strategy);
        if (contacts == null || contacts.isEmpty())
        {
            log.info("SmartPushRuleEngine: no matched contacts for strategy [{}]", strategy.getName());
            return;
        }

        log.info("SmartPushRuleEngine: found {} matched contacts for strategy [{}]",
                contacts.size(), strategy.getName());

        for (CrmContact contact : contacts)
        {
            executor.pushOneContact(strategy, contact, strategy.getStrategyType());
        }
    }

    /**
     * 根据策略 ID 执行单条策略。
     */
    public void executeStrategyById(Long strategyId)
    {
        SmartPushStrategy strategy = strategyService.selectById(strategyId);
        if (strategy == null)
        {
            log.warn("SmartPushRuleEngine: strategy not found for id={}", strategyId);
            return;
        }
        if (strategy.getEnabled() == null || !strategy.getEnabled())
        {
            log.warn("SmartPushRuleEngine: strategy [{}] (id={}) is disabled, skip", strategy.getName(), strategyId);
            return;
        }
        executeStrategy(strategy);
    }

    private RuleMatcher findMatcher(SmartPushStrategy strategy)
    {
        for (RuleMatcher matcher : matchers)
        {
            if (matcher.supports(strategy))
            {
                return matcher;
            }
        }
        return null;
    }
}
