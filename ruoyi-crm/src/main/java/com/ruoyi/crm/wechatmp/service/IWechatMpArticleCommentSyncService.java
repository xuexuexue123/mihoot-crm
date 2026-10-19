package com.ruoyi.crm.wechatmp.service;

import java.util.Map;

/**
 * 微信公众号图文留言同步（comment/list → 本地 upsert）
 */
public interface IWechatMpArticleCommentSyncService
{
    /**
     * 同步当前租户留言。
     *
     * @param appId     可选；缺省 = 全部配置公众号
     * @param msgDataId 可选；指定则只 sync 该图文
     * @param index     可选；与 msgDataId 联用，缺省循环任务 index
     * @return syncedArticles / upserted / skipped / errors
     */
    Map<String, Object> sync(String appId, String msgDataId, Integer index);
}
