package com.ruoyi.crm.wechatmp.service;

import com.ruoyi.common.core.domain.AjaxResult;

public interface IWechatMpMessageStatsService
{
    /**
     * 获取公众号消息数据统计。
     *
     * @param appId     公众号 AppID
     * @param beginDate 起始日期 yyyy-MM-dd
     * @param endDate   结束日期 yyyy-MM-dd
     * @param endpoint  datacube 接口路径名，如 getupstreammsg
     * @return AjaxResult，data.list 为微信原始 list 数组
     */
    AjaxResult getMessageStats(String appId, String beginDate, String endDate, String endpoint);
}
