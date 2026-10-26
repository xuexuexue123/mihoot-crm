package com.ruoyi.crm.wechatmp.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.dto.WechatMpPublishedArticle;

class WechatMpPublishedArticleFlattenSupportTest
{
    @Test
    void flatten_expandsMultiArticleMessage()
    {
        JSONObject res = JSON.parseObject(
                "{"
                + "\"total_count\":1,"
                + "\"item_count\":1,"
                + "\"item\":[{"
                + "  \"article_id\":\"ARTICLE_ID\","
                + "  \"update_time\":1719806400,"
                + "  \"content\":{"
                + "    \"news_item\":["
                + "      {"
                + "        \"title\":\"头条标题\","
                + "        \"author\":\"作者A\","
                + "        \"digest\":\"摘要A\","
                + "        \"url\":\"https://mp.weixin.qq.com/s/head\","
                + "        \"thumb_url\":\"https://mmbiz.qpic.cn/head\","
                + "        \"is_deleted\":false"
                + "      },"
                + "      {"
                + "        \"title\":\"次条标题\","
                + "        \"author\":\"作者B\","
                + "        \"digest\":\"摘要B\","
                + "        \"url\":\"https://mp.weixin.qq.com/s/sub\","
                + "        \"thumb_url\":\"https://mmbiz.qpic.cn/sub\","
                + "        \"is_deleted\":false"
                + "      }"
                + "    ]"
                + "  }"
                + "}]"
                + "}");

        List<WechatMpPublishedArticle> rows = WechatMpPublishedArticleFlattenSupport.flatten(res);

        assertEquals(2, rows.size());
        assertEquals("ARTICLE_ID", rows.get(0).getArticleId());
        assertEquals("ARTICLE_ID", rows.get(0).getPublishId());
        assertEquals(0, rows.get(0).getIndexInMessage());
        assertEquals("头条标题", rows.get(0).getTitle());
        assertEquals("https://mp.weixin.qq.com/s/head", rows.get(0).getUrl());
        assertEquals(1, rows.get(1).getIndexInMessage());
        assertEquals("次条标题", rows.get(1).getTitle());
        assertEquals(1L, WechatMpPublishedArticleFlattenSupport.extractTotalCount(res));
    }

    @Test
    void flatten_skipsDeletedAndEmptyUrl()
    {
        JSONObject res = JSON.parseObject(
                "{"
                + "\"total_count\":1,"
                + "\"item\":[{"
                + "  \"article_id\":\"ARTICLE_ID\","
                + "  \"update_time\":1719806400,"
                + "  \"content\":{"
                + "    \"news_item\":["
                + "      {"
                + "        \"title\":\"已删除\","
                + "        \"url\":\"https://mp.weixin.qq.com/s/deleted\","
                + "        \"is_deleted\":true"
                + "      },"
                + "      {"
                + "        \"title\":\"无链接\","
                + "        \"url\":\"\","
                + "        \"is_deleted\":false"
                + "      },"
                + "      {"
                + "        \"title\":\"有效\","
                + "        \"url\":\"https://mp.weixin.qq.com/s/ok\","
                + "        \"is_deleted\":false"
                + "      }"
                + "    ]"
                + "  }"
                + "}]"
                + "}");

        List<WechatMpPublishedArticle> rows = WechatMpPublishedArticleFlattenSupport.flatten(res);

        assertEquals(1, rows.size());
        assertEquals("有效", rows.get(0).getTitle());
        assertEquals(2, rows.get(0).getIndexInMessage());
    }

    @Test
    void flatten_emptyItemReturnsEmptyRows()
    {
        JSONObject res = JSON.parseObject("{\"total_count\":0,\"item\":[]}");

        List<WechatMpPublishedArticle> rows = WechatMpPublishedArticleFlattenSupport.flatten(res);

        assertEquals(0, rows.size());
        assertEquals(0L, WechatMpPublishedArticleFlattenSupport.extractTotalCount(res));
    }

    @Test
    void flatten_nullResponseReturnsEmpty()
    {
        assertEquals(0, WechatMpPublishedArticleFlattenSupport.flatten(null).size());
        assertEquals(0L, WechatMpPublishedArticleFlattenSupport.extractTotalCount(null));
    }
}
