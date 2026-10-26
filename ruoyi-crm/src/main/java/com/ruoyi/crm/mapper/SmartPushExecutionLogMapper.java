package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.SmartPushExecutionLog;

/**
 * 自动推送执行日志Mapper接口
 *
 * @author ruoyi
 * @date 2026-07-13
 */
public interface SmartPushExecutionLogMapper
{
    List<SmartPushExecutionLog> selectPage(SmartPushExecutionLog query);

    int insert(SmartPushExecutionLog log);
}
