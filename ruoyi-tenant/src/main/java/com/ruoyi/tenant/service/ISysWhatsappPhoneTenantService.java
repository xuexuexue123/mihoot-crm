package com.ruoyi.tenant.service;

import com.ruoyi.tenant.domain.SysWhatsappPhoneTenant;

import java.util.List;

/**
 * 主库 WhatsApp phone_number_id -> 租户映射
 */
public interface ISysWhatsappPhoneTenantService
{
    /**
     * 根据 phone_number_id 解析租户标识（回调切库）
     */
    SysWhatsappPhoneTenant selectByPhoneNumberId(String phoneNumberId);

    /**
     * 获取所有 phone_number_id → tenant 映射（用于 Webhook verify 遍历）
     */
    List<SysWhatsappPhoneTenant> selectAll();

    /**
     * 保存配置时同步：先删该租户旧映射，再写入 phone_number_id -> tenant
     */
    void replaceMappingForTenant(String tenantId, String phoneNumberId);
}
