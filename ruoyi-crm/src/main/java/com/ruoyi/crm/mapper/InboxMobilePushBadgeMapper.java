package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 聚合四渠道 mine 未读数，供移动推送角标近似值。
 */
public interface InboxMobilePushBadgeMapper
{
    int sumMineUnread(@Param("tenantId") String tenantId, @Param("userId") Long userId);
}
