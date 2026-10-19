package com.ruoyi.crm.wechatmp.support;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.dto.WechatMpPublishedArticle;

/**
 * 将微信 freepublish/batchget 响应扁平化为已发布图文列表。
 */
public final class WechatMpPublishedArticleFlattenSupport
{
    private WechatMpPublishedArticleFlattenSupport()
    {
    }

    public static List<WechatMpPublishedArticle> flatten(JSONObject res)
    {
        List<WechatMpPublishedArticle> rows = new ArrayList<>();
        if (res == null)
        {
            return rows;
        }

        JSONArray items = res.getJSONArray("item");
        if (items == null || items.isEmpty())
        {
            return rows;
        }

        for (int i = 0; i < items.size(); i++)
        {
            JSONObject item = items.getJSONObject(i);
            if (item == null)
            {
                continue;
            }

            String articleId = item.getString("article_id");
            OffsetDateTime updateTime = toOffsetDateTime(item.getLongValue("update_time"));
            JSONObject content = item.getJSONObject("content");
            if (content == null)
            {
                continue;
            }

            JSONArray newsItems = content.getJSONArray("news_item");
            if (newsItems == null || newsItems.isEmpty())
            {
                continue;
            }

            for (int j = 0; j < newsItems.size(); j++)
            {
                JSONObject news = newsItems.getJSONObject(j);
                if (news == null)
                {
                    continue;
                }
                if (news.getBooleanValue("is_deleted"))
                {
                    continue;
                }
                String url = news.getString("url");
                if (!StringUtils.hasText(url))
                {
                    continue;
                }

                WechatMpPublishedArticle row = new WechatMpPublishedArticle();
                row.setArticleId(articleId);
                row.setPublishId(articleId);
                row.setTitle(news.getString("title"));
                row.setUrl(url);
                row.setThumbUrl(news.getString("thumb_url"));
                row.setDigest(news.getString("digest"));
                row.setAuthor(news.getString("author"));
                row.setUpdateTime(updateTime);
                row.setIndexInMessage(j);
                rows.add(row);
            }
        }
        return rows;
    }

    /**
     * 将 freepublish/getarticle 的详细数据填充到已有文章中（批量同步时对每篇新文章调用）。
     * getarticle 返回格式: { "news_item": [ { "content": "...", ... } ] }
     *
     * @param article 从 batchget 扁平化后的基本文章对象
     * @param detail  getarticle 的 JSON 响应体
     */
    public static void applyGetArticleDetail(WechatMpPublishedArticle article, JSONObject detail)
    {
        if (detail == null || article == null)
        {
            return;
        }
        JSONArray newsItems = detail.getJSONArray("news_item");
        if (newsItems == null || newsItems.isEmpty())
        {
            return;
        }
        // 取第一条（通常 getarticle 返回的就是单篇图文详情）
        JSONObject news = newsItems.getJSONObject(0);
        if (news == null)
        {
            return;
        }
        article.setContent(news.getString("content"));
        article.setContentSourceUrl(news.getString("content_source_url"));
        article.setThumbMediaId(news.getString("thumb_media_id"));
        if (news.containsKey("need_open_comment"))
        {
            article.setNeedOpenComment(news.getInteger("need_open_comment"));
        }
        if (news.containsKey("only_fans_can_comment"))
        {
            article.setOnlyFansCanComment(news.getInteger("only_fans_can_comment"));
        }
        if (news.containsKey("is_deleted"))
        {
            article.setIsDeleted(news.getBoolean("is_deleted"));
        }
    }

    public static long extractTotalCount(JSONObject res)
    {
        if (res == null)
        {
            return 0L;
        }
        return res.getLongValue("total_count");
    }

    private static OffsetDateTime toOffsetDateTime(long unixSeconds)
    {
        if (unixSeconds <= 0L)
        {
            return null;
        }
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(unixSeconds), ZoneId.systemDefault());
    }
}
