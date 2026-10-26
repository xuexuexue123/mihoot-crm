package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.domain.SmartPushExecutionLog;
import com.ruoyi.crm.mapper.SmartPushExecutionLogMapper;
import com.ruoyi.crm.service.ISmartPushExecutionLogService;

/**
 * 自动推送执行日志Service业务层处理
 *
 * @author ruoyi
 * @date 2026-07-13
 */
@Service
public class SmartPushExecutionLogServiceImpl implements ISmartPushExecutionLogService
{
    @Autowired
    private SmartPushExecutionLogMapper smartPushExecutionLogMapper;

    @Override
    public List<SmartPushExecutionLog> selectPage(SmartPushExecutionLog query)
    {
        return smartPushExecutionLogMapper.selectPage(query);
    }

    @Override
    public int insert(SmartPushExecutionLog log)
    {
        return smartPushExecutionLogMapper.insert(log);
    }
}
