package com.ruoyi.crm.rule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.SmartPushStrategy;
import com.ruoyi.crm.mapper.CrmContactMapper;

/**
 * 生日匹配器：查找当天生日的联系人。
 * 支持 strategy_type = 'birthday' 的策略。
 */
@Order(1)
@Component
public class BirthdayMatcher implements RuleMatcher
{
    private static final Logger log = LoggerFactory.getLogger(BirthdayMatcher.class);

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Override
    public List<CrmContact> findMatchedContacts(SmartPushStrategy strategy)
    {
        CrmContact query = new CrmContact();
        List<CrmContact> contacts = crmContactMapper.selectCrmContactBirthdayList(query);
        log.info("BirthdayMatcher: found {} birthday contacts for strategy [{}]",
                contacts != null ? contacts.size() : 0, strategy.getName());
        return contacts;
    }

    @Override
    public boolean supports(SmartPushStrategy strategy)
    {
        return "birthday".equals(strategy.getStrategyType());
    }
}
