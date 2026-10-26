package com.ruoyi.crm.wechatmp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticle;
import com.ruoyi.crm.wechatmp.dto.WechatMpPublishedArticle;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpArticleMapper;
import com.ruoyi.crm.wechatmp.service.impl.WechatMpArticleServiceImpl;

@ExtendWith(MockitoExtension.class)
class WechatMpArticleServiceImplTest
{
    private static final String APP_ID = "wxTestApp";
    private static final String TOKEN = "test-token";

    @Mock
    private WechatMpTokenService tokenService;

    @Mock
    private WechatMpTencentApiClient tencentApiClient;

    @Mock
    private CrmWechatMpArticleMapper articleMapper;

    @InjectMocks
    private WechatMpArticleServiceImpl articleService;

    @Test
    void listPublishedArticles_rejectsMissingAppId()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> articleService.listPublishedArticles("", 0, 20));
        assertEquals(400, ex.getCode());
    }

    @Test
    void listPublishedArticles_returnsFromDb()
    {
        // mock: sync returns empty (DB already has data with a recent max update time)
        when(articleMapper.selectMaxUpdateTime(APP_ID))
                .thenReturn(OffsetDateTime.now());
        when(tokenService.getAccessToken(APP_ID)).thenReturn(TOKEN);
        when(tencentApiClient.batchGetPublishedArticles(eq(TOKEN), eq(0), eq(20)))
                .thenReturn(successResponse());

        // mock: DB read
        List<CrmWechatMpArticle> dbRows = dbRows();
        when(articleMapper.selectPageByAppId(APP_ID, 0, 20)).thenReturn(dbRows);
        when(articleMapper.countByAppId(APP_ID)).thenReturn(56L);

        TableDataInfo data = articleService.listPublishedArticles(APP_ID, 0, 20);

        assertEquals(200, data.getCode());
        assertEquals("success", data.getMsg());
        assertEquals(56L, data.getTotal());
        @SuppressWarnings("unchecked")
        List<WechatMpPublishedArticle> rows = (List<WechatMpPublishedArticle>) data.getRows();
        assertEquals(2, rows.size());
        assertEquals("https://mp.weixin.qq.com/s/head", rows.get(0).getUrl());
        assertEquals(0, rows.get(0).getIndexInMessage());
        assertEquals(1, rows.get(1).getIndexInMessage());
    }

    @Test
    void listPublishedArticles_emptyDbReturnsSuccess()
    {
        when(articleMapper.selectMaxUpdateTime(APP_ID)).thenReturn(null);
        when(tokenService.getAccessToken(APP_ID)).thenReturn(TOKEN);
        when(tencentApiClient.batchGetPublishedArticles(eq(TOKEN), eq(0), eq(20)))
                .thenReturn(JSON.parseObject("{\"errcode\":0,\"total_count\":0,\"item\":[]}"));

        when(articleMapper.selectPageByAppId(APP_ID, 0, 20)).thenReturn(new ArrayList<>());
        when(articleMapper.countByAppId(APP_ID)).thenReturn(0L);

        TableDataInfo data = articleService.listPublishedArticles(APP_ID, 0, 20);

        assertEquals(200, data.getCode());
        assertEquals(0L, data.getTotal());
        assertTrue(((List<?>) data.getRows()).isEmpty());
    }

    @Test
    void listPublishedArticles_syncFailsButDbWorks()
    {
        // sync throws, but DB read still succeeds (fallback)
        when(articleMapper.selectMaxUpdateTime(APP_ID))
                .thenThrow(new RuntimeException("DB connection error for max update time"));
        // DB read works
        when(articleMapper.selectPageByAppId(APP_ID, 0, 20)).thenReturn(new ArrayList<>());
        when(articleMapper.countByAppId(APP_ID)).thenReturn(0L);

        TableDataInfo data = articleService.listPublishedArticles(APP_ID, 0, 20);

        assertEquals(200, data.getCode());
        assertEquals(0L, data.getTotal());
    }

    @Test
    void listPublishedArticles_clampsCountToMax20()
    {
        when(articleMapper.selectMaxUpdateTime(APP_ID))
                .thenReturn(OffsetDateTime.now());
        when(tokenService.getAccessToken(APP_ID)).thenReturn(TOKEN);
        when(tencentApiClient.batchGetPublishedArticles(eq(TOKEN), eq(0), eq(20)))
                .thenReturn(successResponse());
        when(articleMapper.selectPageByAppId(APP_ID, 0, 20)).thenReturn(new ArrayList<>());
        when(articleMapper.countByAppId(APP_ID)).thenReturn(0L);

        articleService.listPublishedArticles(APP_ID, 0, 100);

        verify(articleMapper).selectPageByAppId(APP_ID, 0, 20);
    }

    @Test
    void listPublishedArticles_tokenInvalidRetriesDuringSync()
    {
        JSONObject invalid = JSON.parseObject("{\"errcode\":40001,\"errmsg\":\"invalid credential\"}");
        when(articleMapper.selectMaxUpdateTime(APP_ID)).thenReturn(null);
        when(tokenService.getAccessToken(APP_ID)).thenReturn(TOKEN);
        when(tokenService.refreshAccessToken(APP_ID)).thenReturn("fresh-token");
        when(tencentApiClient.isTokenInvalid(invalid)).thenReturn(true);
        when(tencentApiClient.batchGetPublishedArticles(eq(TOKEN), eq(0), eq(20))).thenReturn(invalid);
        when(tencentApiClient.batchGetPublishedArticles(eq("fresh-token"), eq(0), eq(20)))
                .thenReturn(successResponse());
        when(articleMapper.selectPageByAppId(APP_ID, 0, 20)).thenReturn(new ArrayList<>());
        when(articleMapper.countByAppId(APP_ID)).thenReturn(0L);
        when(articleMapper.batchUpsert(any())).thenReturn(1);

        TableDataInfo data = articleService.listPublishedArticles(APP_ID, 0, 20);

        assertEquals(200, data.getCode());
        verify(tokenService).refreshAccessToken(APP_ID);
    }

    private static List<CrmWechatMpArticle> dbRows()
    {
        List<CrmWechatMpArticle> rows = new ArrayList<>();
        CrmWechatMpArticle a1 = new CrmWechatMpArticle();
        a1.setAppId(APP_ID);
        a1.setArticleId("ARTICLE_ID");
        a1.setTitle("头条标题");
        a1.setUrl("https://mp.weixin.qq.com/s/head");
        a1.setUpdateTime(OffsetDateTime.parse("2024-07-01T00:00:00+08:00"));
        a1.setIndexInMsg(0);
        a1.setIsDeleted(false);
        rows.add(a1);

        CrmWechatMpArticle a2 = new CrmWechatMpArticle();
        a2.setAppId(APP_ID);
        a2.setArticleId("ARTICLE_ID");
        a2.setTitle("次条标题");
        a2.setUrl("https://mp.weixin.qq.com/s/sub");
        a2.setUpdateTime(OffsetDateTime.parse("2024-07-01T00:00:00+08:00"));
        a2.setIndexInMsg(1);
        a2.setIsDeleted(false);
        rows.add(a2);

        return rows;
    }

    private static JSONObject successResponse()
    {
        return JSON.parseObject(
                "{"
                + "\"errcode\":0,"
                + "\"total_count\":56,"
                + "\"item_count\":1,"
                + "\"item\":[{"
                + "  \"article_id\":\"ARTICLE_ID\","
                + "  \"update_time\":1719806400,"
                + "  \"content\":{"
                + "    \"news_item\":["
                + "      {"
                + "        \"title\":\"头条标题\","
                + "        \"url\":\"https://mp.weixin.qq.com/s/head\","
                + "        \"is_deleted\":false"
                + "      },"
                + "      {"
                + "        \"title\":\"次条标题\","
                + "        \"url\":\"https://mp.weixin.qq.com/s/sub\","
                + "        \"is_deleted\":false"
                + "      }"
                + "    ]"
                + "  }"
                + "}]"
                + "}");
    }
}
