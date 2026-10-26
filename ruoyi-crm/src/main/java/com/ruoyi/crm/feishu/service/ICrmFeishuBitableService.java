package com.ruoyi.crm.feishu.service;

import java.util.Map;

import com.ruoyi.crm.feishu.dto.FeishuBitableExportResult;
import com.ruoyi.crm.feishu.dto.FeishuBitableRequest;

/**
 * 飞书多维表格代理：注入 token、映射响应、token 失效重试一次。
 */
public interface ICrmFeishuBitableService
{
    Map<String, Object> createApp(FeishuBitableRequest request);

    Map<String, Object> createTable(FeishuBitableRequest request);

    Map<String, Object> listTables(FeishuBitableRequest request);

    Map<String, Object> listFields(FeishuBitableRequest request);

    Map<String, Object> listRecords(FeishuBitableRequest request);

    Map<String, Object> createRecord(FeishuBitableRequest request);

    Map<String, Object> batchCreateRecords(FeishuBitableRequest request);

    Map<String, Object> updateRecord(FeishuBitableRequest request);

    Map<String, Object> searchRecords(FeishuBitableRequest request);

    Map<String, Object> deleteRecord(FeishuBitableRequest request);

    Map<String, Object> batchDeleteRecords(FeishuBitableRequest request);

    /**
     * 分页拉取记录并拼 CSV / Excel 文件。
     */
    FeishuBitableExportResult exportRecords(FeishuBitableRequest request);
}
