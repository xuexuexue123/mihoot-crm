package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.SmartPushStrategy;

/**
 * 智能推送策略Service接口
 *
 * @author ruoyi
 * @date 2026-07-13
 */
public interface ISmartPushStrategyService
{
    List<SmartPushStrategy> selectListByType(String strategyType);

    SmartPushStrategy selectById(Long id);

    List<SmartPushStrategy> selectEnabledByType(String strategyType);

    List<SmartPushStrategy> selectEnabledBirthday();

    List<SmartPushStrategy> selectEnabledScheduled();

    List<SmartPushStrategy> selectEnabledOnEvent(String triggerEvent);

    int insert(SmartPushStrategy strategy);

    int update(SmartPushStrategy strategy);
}
