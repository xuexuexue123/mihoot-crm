package com.ruoyi.crm.feishu.service;

import java.util.Map;

import com.ruoyi.crm.feishu.domain.CrmFeishuConfig;
import com.ruoyi.crm.feishu.dto.FeishuConfigSaveRequest;

public interface ICrmFeishuConfigService
{
    /**
     * 当前租户脱敏配置；无记录返回 null。
     */
    Map<String, Object> getConfigMasked();

    /**
     * Upsert 当前租户唯一配置；secret 缺省则保留原密文；保存后换票探针。
     */
    Map<String, Object> saveOrUpdate(FeishuConfigSaveRequest body);

    /**
     * 使用已存配置强制换票探测连通性。
     */
    Map<String, Object> testConnection();

    /**
     * 删除当前租户配置（单例行）并清除 token 缓存。
     */
    void delete();

    /**
     * 要求已配置且 connected；否则抛业务异常。
     */
    CrmFeishuConfig requireConnected();

    /**
     * 当前租户原始配置行（含密文）；无记录返回 null。
     */
    CrmFeishuConfig getRawConfig();
}
