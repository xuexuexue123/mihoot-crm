package com.ruoyi.crm.wechatmp.service;

import com.ruoyi.common.core.domain.AjaxResult;

public interface IWechatMpArticleStatService
{
    /**
     * 拉取文章统计数据（调用 getarticletotaldetail 并落库）
     *
     * @param appId     公众号 AppID
     * @param beginDate 起始日期 yyyy-MM-dd
     * @param endDate   结束日期 yyyy-MM-dd（微信限制最大为昨日，单次仅支持 1 天）
     */
    AjaxResult fetchArticleStat(String appId, String beginDate, String endDate);
}
