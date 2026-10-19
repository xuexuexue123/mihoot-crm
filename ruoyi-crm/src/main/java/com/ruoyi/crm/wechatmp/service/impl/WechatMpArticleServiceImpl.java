package com.ruoyi.crm.wechatmp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticle;
import com.ruoyi.crm.wechatmp.dto.WechatMpPublishedArticle;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpArticleMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpArticleStatMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.crm.wechatmp.support.WechatMpPublishedArticleFlattenSupport;

/**
 * 公众号已发布图文服务 —— 数据落库版。
 * <ul>
 *   <li>读取优先从 DB (crm_wechat_mp_article) 返回，毫秒级响应</li>
 *   <li>每次读取前做一次轻量增量同步：只拉取 DB 中最大 update_time 之后的新文章</li>
 *   <li>对新文章调用 freepublish/getarticle 获取正文 HTML 等详细信息并落库</li>
 * </ul>
 */
@Service
public class WechatMpArticleServiceImpl implements IWechatMpArticleService
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpArticleServiceImpl.class);

    private static final int MAX_COUNT = 20;

    /** 单次同步最多拉取的发布记录批次数，防止无限循环 */
    private static final int SYNC_MAX_BATCHES = 25;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Autowired
    private CrmWechatMpArticleMapper articleMapper;

    @Autowired
    private CrmWechatMpArticleStatMapper articleStatMapper;

    @Override
    public TableDataInfo listPublishedArticles(String appId, int offset, int count)
    {
        if (!StringUtils.hasText(appId))
        {
            throw new ServiceException("appId 不能为空", HttpStatus.BAD_REQUEST);
        }

        int safeOffset = Math.max(offset, 0);
        int safeCount = Math.min(Math.max(count, 1), MAX_COUNT);

        // 1. 增量同步（首次全量，后续只拉新数据）
        try
        {
            syncIncremental(appId);
        }
        catch (Exception e)
        {
            log.warn("syncIncremental failed for appId={}, fallback to DB only. {}", appId, e.getMessage());
        }

        // 2. 从 DB 分页读取
        List<CrmWechatMpArticle> rows = articleMapper.selectPageByAppId(appId, safeOffset, safeCount);
        long total = articleMapper.countByAppId(appId);

        // 3. 批量查询统计聚合（独立查询，stats 表不存在时不影响文章返回）
        Map<String, Map<String, Object>> statsMap = loadAggregatedStats(appId);

        // 4. 转换为 DTO（不返回 content 字段，前端列表不需要正文）
        List<WechatMpPublishedArticle> dtos = toDtoList(rows, statsMap);

        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("success");
        data.setTotal(total);
        data.setRows(dtos);
        return data;
    }

    // ======================== 增量同步 ========================

    private void syncIncremental(String appId)
    {
        java.time.OffsetDateTime maxUpdateTime = articleMapper.selectMaxUpdateTime(appId);

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;
        int offset = 0;
        int batchCount = 0;

        while (batchCount < SYNC_MAX_BATCHES)
        {
            JSONObject res = tencentApiClient.batchGetPublishedArticles(token, offset, MAX_COUNT);
            if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
            {
                token = tokenService.refreshAccessToken(appId);
                tokenRefreshed = true;
                res = tencentApiClient.batchGetPublishedArticles(token, offset, MAX_COUNT);
            }
            if (!isWeChatSuccess(res))
            {
                log.warn("batchget returned error for appId={}, errcode={}, errmsg={}",
                        appId, res.getIntValue("errcode"), res.getString("errmsg"));
                break;
            }

            JSONArray items = res.getJSONArray("item");
            if (items == null || items.isEmpty())
            {
                break;
            }

            // 扁平化 batchget 得到基本数据（不含 HTML 正文）
            List<WechatMpPublishedArticle> newArticles = WechatMpPublishedArticleFlattenSupport.flatten(res);

            // 只保留 update_time > maxUpdateTime 的文章
            List<WechatMpPublishedArticle> toSync = new ArrayList<>();
            boolean reachedExisting = false;
            for (WechatMpPublishedArticle article : newArticles)
            {
                if (maxUpdateTime != null
                        && article.getUpdateTime() != null
                        && !article.getUpdateTime().isAfter(maxUpdateTime))
                {
                    reachedExisting = true;
                    break;
                }
                toSync.add(article);
            }

            if (!toSync.isEmpty())
            {
                // 对每篇新文章调用 getarticle 获取详细信息
                for (WechatMpPublishedArticle article : toSync)
                {
                    fetchArticleDetail(token, tokenRefreshed, appId, article);
                }

                // 批量写入 DB
                List<CrmWechatMpArticle> entities = toDomainList(appId, toSync);
                articleMapper.batchUpsert(entities);
            }

            long totalCount = res.getLongValue("total_count");
            offset += items.size();
            batchCount++;

            if (reachedExisting || offset >= totalCount)
            {
                break;
            }
        }
    }

    /**
     * 调用 freepublish/getarticle 获取单篇图文详细信息并填充到 article 中。
     */
    private void fetchArticleDetail(String token, boolean tokenRefreshed,
            String appId, WechatMpPublishedArticle article)
    {
        try
        {
            String tk = token;
            JSONObject detail = tencentApiClient.getPublishedArticle(tk, article.getArticleId());
            if (tencentApiClient.isTokenInvalid(detail) && !tokenRefreshed)
            {
                tk = tokenService.refreshAccessToken(appId);
                detail = tencentApiClient.getPublishedArticle(tk, article.getArticleId());
            }
            if (isWeChatSuccess(detail))
            {
                WechatMpPublishedArticleFlattenSupport.applyGetArticleDetail(article, detail);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to fetch article detail for articleId={}: {}", article.getArticleId(), e.getMessage());
        }
    }

    // ======================== 辅助方法 ========================

    private boolean isWeChatSuccess(JSONObject res)
    {
        return res != null && res.getIntValue("errcode") == 0;
    }

    private List<CrmWechatMpArticle> toDomainList(String appId, List<WechatMpPublishedArticle> dtos)
    {
        List<CrmWechatMpArticle> entities = new ArrayList<>(dtos.size());
        for (WechatMpPublishedArticle dto : dtos)
        {
            CrmWechatMpArticle entity = new CrmWechatMpArticle();
            entity.setAppId(appId);
            entity.setArticleId(dto.getArticleId());
            entity.setTitle(dto.getTitle());
            entity.setUrl(dto.getUrl());
            entity.setThumbUrl(dto.getThumbUrl());
            entity.setDigest(dto.getDigest());
            entity.setAuthor(dto.getAuthor());
            entity.setIndexInMsg(dto.getIndexInMessage());
            entity.setUpdateTime(dto.getUpdateTime());
            entity.setContent(dto.getContent());
            entity.setContentSourceUrl(dto.getContentSourceUrl());
            entity.setThumbMediaId(dto.getThumbMediaId());
            entity.setNeedOpenComment(dto.getNeedOpenComment());
            entity.setOnlyFansCanComment(dto.getOnlyFansCanComment());
            entity.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : Boolean.FALSE);
            entity.setDetailFetched(dto.getContent() != null);
            entities.add(entity);
        }
        return entities;
    }

    private List<WechatMpPublishedArticle> toDtoList(List<CrmWechatMpArticle> entities,
            Map<String, Map<String, Object>> statsMap)
    {
        List<WechatMpPublishedArticle> dtos = new ArrayList<>(entities.size());
        for (CrmWechatMpArticle entity : entities)
        {
            WechatMpPublishedArticle dto = new WechatMpPublishedArticle();
            dto.setArticleId(entity.getArticleId());
            dto.setPublishId(entity.getArticleId());
            dto.setTitle(entity.getTitle());
            dto.setUrl(entity.getUrl());
            dto.setThumbUrl(entity.getThumbUrl());
            dto.setDigest(entity.getDigest());
            dto.setAuthor(entity.getAuthor());
            dto.setUpdateTime(entity.getUpdateTime());
            dto.setIndexInMessage(entity.getIndexInMsg());
            dto.setContent(entity.getContent());
            dto.setContentSourceUrl(entity.getContentSourceUrl());
            dto.setThumbMediaId(entity.getThumbMediaId());
            dto.setNeedOpenComment(entity.getNeedOpenComment());
            dto.setOnlyFansCanComment(entity.getOnlyFansCanComment());
            dto.setIsDeleted(entity.getIsDeleted());

            // 从 statsMap 合并统计数据
            String key = entity.getArticleId() + ":" + entity.getIndexInMsg();
            Map<String, Object> stat = statsMap.get(key);
            if (stat != null)
            {
                dto.setHasStats(true);
                dto.setTotalRead(toInt(stat.get("total_read")));
                dto.setTotalShare(toInt(stat.get("total_share")));
                dto.setTotalLike(toInt(stat.get("total_like")));
                dto.setTotalComment(toInt(stat.get("total_comment")));
                dto.setTotalCollect(toInt(stat.get("total_collect")));
            }
            else
            {
                dto.setHasStats(false);
                dto.setTotalRead(0);
                dto.setTotalShare(0);
                dto.setTotalLike(0);
                dto.setTotalComment(0);
                dto.setTotalCollect(0);
            }
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * 批量加载该公众号下所有文章的统计聚合。
     * stats 表不存在时返回空 Map，不影响文章列表返回。
     */
    private Map<String, Map<String, Object>> loadAggregatedStats(String appId)
    {
        try
        {
            List<Map<String, Object>> rows = articleStatMapper.selectAggregatedByAppId(appId);
            Map<String, Map<String, Object>> map = new HashMap<>();
            for (Map<String, Object> row : rows)
            {
                Object key = row.get("stat_key");
                if (key != null)
                {
                    map.put(key.toString(), row);
                }
            }
            return map;
        }
        catch (Exception e)
        {
            log.debug("Failed to load article stats for appId={}, table may not exist: {}", appId, e.getMessage());
            return new HashMap<>();
        }
    }

    private static int toInt(Object val)
    {
        if (val instanceof Number)
        {
            return ((Number) val).intValue();
        }
        return 0;
    }
}
