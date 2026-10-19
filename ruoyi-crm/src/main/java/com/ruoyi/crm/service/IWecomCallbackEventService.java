package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.CrmWecomCallbackEvent;

/**
 * 企业微信回调事件记录服务
 */
public interface IWecomCallbackEventService
{
    /**
     * 从回调明文 XML 中提取事件信息并记录到数据库。
     * 记录失败不抛异常（不影响主回调流程）。
     */
    void logEvent(String plainXml);

    /**
     * 按条件分页查询事件列表。
     * 调用方需先执行 PageHelper.startPage()。
     */
    List<CrmWecomCallbackEvent> selectList(CrmWecomCallbackEvent query);

    /**
     * 按 event + change_type 分组统计事件数量。
     */
    List<Map<String, Object>> countByEventType(CrmWecomCallbackEvent query);
}
