package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.domain.SmartPushStrategy;
import com.ruoyi.crm.mapper.SmartPushStrategyMapper;
import com.ruoyi.crm.service.ISmartPushStrategyService;

/**
 * 智能推送策略Service业务层处理
 *
 * @author ruoyi
 * @date 2026-07-13
 */
@Service
public class SmartPushStrategyServiceImpl implements ISmartPushStrategyService
{
    @Autowired
    private SmartPushStrategyMapper smartPushStrategyMapper;

    @Override
    public List<SmartPushStrategy> selectListByType(String strategyType)
    {
        return smartPushStrategyMapper.selectListByType(strategyType);
    }

    @Override
    public SmartPushStrategy selectById(Long id)
    {
        return smartPushStrategyMapper.selectById(id);
    }

    @Override
    public List<SmartPushStrategy> selectEnabledByType(String strategyType)
    {
        return smartPushStrategyMapper.selectEnabledByType(strategyType);
    }

    @Override
    public List<SmartPushStrategy> selectEnabledBirthday()
    {
        return smartPushStrategyMapper.selectEnabledBirthday();
    }

    @Override
    public List<SmartPushStrategy> selectEnabledScheduled()
    {
        return smartPushStrategyMapper.selectEnabledScheduled();
    }

    @Override
    public List<SmartPushStrategy> selectEnabledOnEvent(String triggerEvent)
    {
        return smartPushStrategyMapper.selectEnabledOnEvent(triggerEvent);
    }

    @Override
    public int insert(SmartPushStrategy strategy)
    {
        return smartPushStrategyMapper.insert(strategy);
    }

    @Override
    public int update(SmartPushStrategy strategy)
    {
        return smartPushStrategyMapper.update(strategy);
    }
}
