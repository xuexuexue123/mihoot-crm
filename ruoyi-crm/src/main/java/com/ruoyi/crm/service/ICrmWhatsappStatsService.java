package com.ruoyi.crm.service;

import java.util.Map;

public interface ICrmWhatsappStatsService
{
    Map<String, Object> getStats(String tenantId, Long startTime, Long endTime, String phoneNumberId);
}
