package com.ruoyi.crm.wechatmp.support;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.dto.WechatMpArticleDraft;

/**
 * 将微信 draft/batchget 响应扁平化为草稿文章列表。
 */
public final class WechatMpDraftFlattenSupport
{
    private WechatMpDraftFlattenSupport()
    {
    }

    /**
     * 将 draft/batchget 响应扁平化为 {@link WechatMpArticleDraft} 列表。
     * <p>
     * 微信 draft/batchget 返回格式:
     * <pre>
     * {
     *   "total_count": N,
     *   "item_count": N,
     *   "item": [{
     *     "media_id": "...",
     *     "content": {
     *       "news_item": [{ "article_type": "news", "title": "...", ... }]
     *     },
     *     "update_time": 1234567890
     *   }]
     * }
     * </pre>
     */
    public static List<WechatMpArticleDraft> flatten(JSONObject res)
    {
        List<WechatMpArticleDraft> rows = new ArrayList<>();
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

            String mediaId = item.getString("media_id");
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

                WechatMpArticleDraft row = new WechatMpArticleDraft();
                row.setMediaId(mediaId);
                row.setIndexInMsg(j);
                row.setArticleType(news.getString("article_type"));
                row.setTitle(news.getString("title"));
                row.setAuthor(news.getString("author"));
                row.setDigest(news.getString("digest"));
                row.setContent(news.getString("content"));
                row.setContentSourceUrl(news.getString("content_source_url"));
                row.setThumbMediaId(news.getString("thumb_media_id"));
                if (news.containsKey("need_open_comment"))
                {
                    row.setNeedOpenComment(news.getInteger("need_open_comment"));
                }
                if (news.containsKey("only_fans_can_comment"))
                {
                    row.setOnlyFansCanComment(news.getInteger("only_fans_can_comment"));
                }
                row.setUrl(news.getString("url"));
                row.setUpdateTime(updateTime);
                rows.add(row);
            }
        }
        return rows;
    }

    /**
     * 将 draft/get 的详细数据填充到已有草稿对象中。
     * draft/get 返回格式: { "news_item": [ { "content": "...", ... } ] }
     */
    public static void applyDraftDetail(WechatMpArticleDraft draft, JSONObject detail)
    {
        if (detail == null || draft == null)
        {
            return;
        }
        JSONArray newsItems = detail.getJSONArray("news_item");
        if (newsItems == null || newsItems.isEmpty())
        {
            return;
        }
        JSONObject news = newsItems.getJSONObject(0);
        if (news == null)
        {
            return;
        }
        draft.setArticleType(news.getString("article_type"));
        draft.setTitle(news.getString("title"));
        draft.setAuthor(news.getString("author"));
        draft.setDigest(news.getString("digest"));
        draft.setContent(news.getString("content"));
        draft.setContentSourceUrl(news.getString("content_source_url"));
        draft.setThumbMediaId(news.getString("thumb_media_id"));
        if (news.containsKey("need_open_comment"))
        {
            draft.setNeedOpenComment(news.getInteger("need_open_comment"));
        }
        if (news.containsKey("only_fans_can_comment"))
        {
            draft.setOnlyFansCanComment(news.getInteger("only_fans_can_comment"));
        }
        draft.setUrl(news.getString("url"));
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
