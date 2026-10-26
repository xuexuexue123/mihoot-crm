package com.ruoyi.crm.rule;

import java.util.List;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.SmartPushStrategy;

/**
 * 规则匹配器接口，每种规则类型对应一个实现。
 */
public interface RuleMatcher
{
    /**
     * 根据策略配置查找匹配的联系人列表。
     */
    List<CrmContact> findMatchedContacts(SmartPushStrategy strategy);

    /**
     * 判断当前匹配器是否支持该策略。
     */
    boolean supports(SmartPushStrategy strategy);
}
