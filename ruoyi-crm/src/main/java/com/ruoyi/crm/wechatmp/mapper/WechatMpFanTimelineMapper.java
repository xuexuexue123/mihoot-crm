package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;

/**
 * 公众号粉丝操作时间线查询
 */
public interface WechatMpFanTimelineMapper
{
    List<CrmActivity> selectActivitiesByOpenId(
        @Param("appId") String appId,
        @Param("openId") String openId
    );

    List<ActivityUnmatched> selectUnmatchedByOpenId(
        @Param("appId") String appId,
        @Param("openId") String openId
    );
}
