package com.ruoyi.crm.wechatmp.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface WechatMpStatsMapper
{
    int countEvents(@Param("appId") String appId, @Param("eventType") String eventType,
            @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    List<Map<String, Object>> selectTopMenuKeys(@Param("appId") String appId,
            @Param("beginTime") Date beginTime, @Param("endTime") Date endTime,
            @Param("limit") int limit);
}
