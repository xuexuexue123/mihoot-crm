package com.ruoyi.crm.wechatmp.service;

import com.ruoyi.common.core.page.TableDataInfo;

public interface IWechatMpArticleService
{
    TableDataInfo listPublishedArticles(String appId, int offset, int count);
}
