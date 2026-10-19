package com.ruoyi.crm.wechatmp.service;

import java.util.Date;
import java.util.Map;

public interface IWechatMpStatsService
{
    Map<String, Object> getStats(String appId, Date beginTime, Date endTime);

    void syncAccountInfo(String appId);
}
