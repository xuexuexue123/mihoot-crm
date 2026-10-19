package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.CrmLeadPoolConfig;

public interface ICrmLeadPoolConfigService
{
    CrmLeadPoolConfig getConfig();

    /**
     * 保存公海/线索配置。
     *
     * @param config 请求体反序列化结果（claim/protect 等按 body 写入）
     * @param qualifyTemplateIdPresent body 是否包含 qualifyTemplateId 键；false 时保留库中原值
     */
    CrmLeadPoolConfig saveConfig(CrmLeadPoolConfig config, boolean qualifyTemplateIdPresent);

    void assertClaimAllowed(Long userId);

    void assertHoldAllowed(Long userId);
}
