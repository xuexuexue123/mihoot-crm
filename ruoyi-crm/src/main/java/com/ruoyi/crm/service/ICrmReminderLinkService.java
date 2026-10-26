package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.ReminderLinkCreateRequest;
import com.ruoyi.crm.domain.vo.ReminderLinkCreateResultVo;
import com.ruoyi.crm.domain.vo.ReminderLinkPublicVo;

public interface ICrmReminderLinkService
{
    /**
     * 鉴权创建短链（当前租户数据源已由 TenantInterceptor 绑定）。
     */
    ReminderLinkCreateResultVo create(String tenantId, String createdBy, ReminderLinkCreateRequest request);

    /**
     * 公开查询：按 website slug 解析租户后查 (tenant_id, token)。
     * 租户解析方式 A：query tenant / header X-Tenant-Slug。
     */
    ReminderLinkPublicVo getPublicByToken(String token, String tenantSlug);
}
