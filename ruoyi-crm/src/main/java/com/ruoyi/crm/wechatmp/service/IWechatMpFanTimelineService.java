package com.ruoyi.crm.wechatmp.service;

import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 公众号粉丝操作时间线
 */
public interface IWechatMpFanTimelineService
{
    TableDataInfo listTimeline(String appId, String openId, int pageNum, int pageSize);
}
