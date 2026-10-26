package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.SmartPushExecutionLog;

/**
 * 自动推送执行日志Service接口
 *
 * @author ruoyi
 * @date 2026-07-13
 */
public interface ISmartPushExecutionLogService
{
    List<SmartPushExecutionLog> selectPage(SmartPushExecutionLog query);

    int insert(SmartPushExecutionLog log);
}
