package com.ruoyi.crm.wechatmp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleDraft;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMaterial;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpPublishTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpArticleDraft;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftAddRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftSyncRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftUpdateRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpArticleDraftMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMaterialMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpPublishTaskMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleDraftService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.crm.wechatmp.support.MarkdownToHtmlConverter;
import com.ruoyi.crm.wechatmp.support.WechatMpDraftFlattenSupport;

/**
 * 公众号草稿文章服务 —— 数据落库版。
 * <ul>
 *   <li>读取优先从 DB (crm_wechat_mp_article_draft) 返回，毫秒级响应</li>
 *   <li>每次读取前做轻量增量同步：只拉取 DB 中最大 update_time 之后的新草稿</li>
 *   <li>支持 localOnly 模式：先保存到本地 DB，随后通过 syncDraft 推送到微信</li>
 *   <li>新增/修改/删除草稿默认直接调用微信 API 并同步更新本地 DB</li>
 * </ul>
 */
@Service
public class WechatMpArticleDraftServiceImpl implements IWechatMpArticleDraftService
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpArticleDraftServiceImpl.class);

    private static final int MAX_COUNT = 20;
    private static final int SYNC_MAX_BATCHES = 25;
    private static final int ERRCODE_INVALID_MEDIA_ID = 40007;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Autowired
    private CrmWechatMpArticleDraftMapper draftMapper;

    @Autowired
    private CrmWechatMpMaterialMapper materialMapper;

    @Autowired
    private CrmWechatMpPublishTaskMapper publishTaskMapper;

    @Override
    public TableDataInfo listDrafts(String appId, int offset, int count)
    {
        if (!StringUtils.hasText(appId))
        {
            throw new ServiceException("appId 不能为空", HttpStatus.BAD_REQUEST);
        }

        int safeOffset = Math.max(offset, 0);
        int safeCount = Math.min(Math.max(count, 1), MAX_COUNT);

        List<CrmWechatMpArticleDraft> rows = draftMapper.selectPageByAppId(appId, safeOffset, safeCount);
        long total = draftMapper.countByAppId(appId);

        List<WechatMpArticleDraft> dtos = toDtoList(rows);

        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("success");
        data.setTotal(total);
        data.setRows(dtos);
        return data;
    }

    @Override
    public AjaxResult getDraftDetail(String appId, String mediaId, boolean localOnly)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(mediaId))
        {
            return AjaxResult.error("appId 和 mediaId 不能为空");
        }

        if (localOnly)
        {
            return getDraftDetailFromLocal(appId, mediaId);
        }
        return getDraftDetailFromWechat(appId, mediaId);
    }

    private AjaxResult getDraftDetailFromLocal(String appId, String mediaId)
    {
        CrmWechatMpArticleDraft row = draftMapper.selectByMediaId(appId, mediaId);
        if (row == null)
        {
            return AjaxResult.error("本地草稿不存在");
        }
        WechatMpArticleDraft dto = new WechatMpArticleDraft();
        dto.setMediaId(row.getMediaId());
        dto.setIndexInMsg(row.getIndexInMsg());
        dto.setArticleType(row.getArticleType());
        dto.setTitle(row.getTitle());
        dto.setAuthor(row.getAuthor());
        dto.setDigest(row.getDigest());
        dto.setContent(row.getContent());
        dto.setContentMd(row.getContentMd());
        dto.setContentSourceUrl(row.getContentSourceUrl());
        dto.setThumbMediaId(row.getThumbMediaId());
        dto.setNeedOpenComment(row.getNeedOpenComment());
        dto.setOnlyFansCanComment(row.getOnlyFansCanComment());
        dto.setUrl(row.getUrl());
        dto.setSynced(row.getSynced());
        dto.setPublished(row.getPublished());
        dto.setUpdateTime(row.getUpdateTime());
        return AjaxResult.success(dto);
    }

    private AjaxResult getDraftDetailFromWechat(String appId, String mediaId)
    {
        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;

        JSONObject detail = tencentApiClient.getDraft(token, mediaId);
        if (tencentApiClient.isTokenInvalid(detail) && !tokenRefreshed)
        {
            token = tokenService.refreshAccessToken(appId);
            tokenRefreshed = true;
            detail = tencentApiClient.getDraft(token, mediaId);
        }
        if (!isWeChatSuccess(detail))
        {
            return AjaxResult.error("获取草稿详情失败: " + detail.getString("errmsg"));
        }

        JSONArray newsItems = detail.getJSONArray("news_item");
        if (newsItems != null && !newsItems.isEmpty())
        {
            JSONObject news = newsItems.getJSONObject(0);
            WechatMpArticleDraft dto = new WechatMpArticleDraft();
            dto.setMediaId(mediaId);
            dto.setArticleType(news.getString("article_type"));
            dto.setTitle(news.getString("title"));
            dto.setAuthor(news.getString("author"));
            dto.setDigest(news.getString("digest"));
            dto.setContent(news.getString("content"));
            dto.setContentSourceUrl(news.getString("content_source_url"));
            dto.setThumbMediaId(news.getString("thumb_media_id"));
            dto.setUrl(news.getString("url"));
            if (news.containsKey("need_open_comment"))
            {
                dto.setNeedOpenComment(news.getInteger("need_open_comment"));
            }
            if (news.containsKey("only_fans_can_comment"))
            {
                dto.setOnlyFansCanComment(news.getInteger("only_fans_can_comment"));
            }
            return AjaxResult.success(dto);
        }
        return AjaxResult.success(detail);
    }

    @Override
    public AjaxResult createDraft(String appId, WechatMpDraftAddRequest request, boolean localOnly)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }
        if (request == null || request.getArticles() == null || request.getArticles().isEmpty())
        {
            return AjaxResult.error("articles 不能为空");
        }

        if (localOnly)
        {
            return createDraftLocalOnly(appId, request);
        }
        return createDraftRemote(appId, request);
    }

    private AjaxResult createDraftLocalOnly(String appId, WechatMpDraftAddRequest request)
    {
        String mediaId = UUID.randomUUID().toString().replace("-", "");

        WechatMpDraftAddRequest.Article article = request.getArticles().get(0);
        String htmlContent = resolveContent(article.getContentMd(), article.getContent());

        CrmWechatMpArticleDraft entity = new CrmWechatMpArticleDraft();
        entity.setAppId(appId);
        entity.setMediaId(mediaId);
        entity.setIndexInMsg(0);
        entity.setArticleType(article.getArticleType() != null ? article.getArticleType() : "news");
        entity.setTitle(article.getTitle());
        entity.setAuthor(article.getAuthor());
        entity.setDigest(article.getDigest());
        entity.setContent(htmlContent);
        entity.setContentMd(article.getContentMd());
        entity.setContentSourceUrl(article.getContentSourceUrl());
        entity.setThumbMediaId(article.getThumbMediaId());
        entity.setNeedOpenComment(article.getNeedOpenComment() != null ? article.getNeedOpenComment() : 0);
        entity.setOnlyFansCanComment(article.getOnlyFansCanComment() != null ? article.getOnlyFansCanComment() : 0);
        entity.setSynced(false);
        entity.setPublished(false);

        List<CrmWechatMpArticleDraft> list = new ArrayList<>();
        list.add(entity);
        draftMapper.batchUpsert(list);

        return AjaxResult.success("草稿已保存到本地", mediaId);
    }

    private AjaxResult createDraftRemote(String appId, WechatMpDraftAddRequest request)
    {
        // 缺省封面：若请求未传 thumbMediaId，自动使用该公众号的默认永久素材
        String defaultThumbMediaId = null;
        for (WechatMpDraftAddRequest.Article article : request.getArticles())
        {
            if ("news".equals(article.getArticleType() != null ? article.getArticleType() : "news")
                    && (article.getThumbMediaId() == null || article.getThumbMediaId().isEmpty()))
            {
                if (defaultThumbMediaId == null)
                {
                    CrmWechatMpMaterial defaultMaterial = materialMapper.selectDefaultByAppId(appId);
                    if (defaultMaterial == null || defaultMaterial.getMediaId() == null
                            || defaultMaterial.getMediaId().isEmpty())
                    {
                        return AjaxResult.error("未配置默认封面 thumbMediaId，无法创建图文草稿");
                    }
                    defaultThumbMediaId = defaultMaterial.getMediaId();
                }
                article.setThumbMediaId(defaultThumbMediaId);
            }
        }

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;

        JSONObject body = new JSONObject();
        JSONArray articles = new JSONArray();
        for (WechatMpDraftAddRequest.Article article : request.getArticles())
        {
            String htmlContent = resolveContent(article.getContentMd(), article.getContent());
            JSONObject a = new JSONObject();
            a.put("article_type", article.getArticleType() != null ? article.getArticleType() : "news");
            a.put("title", article.getTitle());
            a.put("content", htmlContent);
            a.put("need_open_comment", article.getNeedOpenComment() != null ? article.getNeedOpenComment() : 0);
            a.put("only_fans_can_comment", article.getOnlyFansCanComment() != null ? article.getOnlyFansCanComment() : 0);
            putIfNotNull(a, "author", article.getAuthor());
            putIfNotNull(a, "digest", article.getDigest());
            putIfNotNull(a, "content_source_url", article.getContentSourceUrl());
            putIfNotNull(a, "thumb_media_id", article.getThumbMediaId());
            articles.add(a);
        }
        body.put("articles", articles);

        JSONObject res = tencentApiClient.addDraft(token, body);
        if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
        {
            token = tokenService.refreshAccessToken(appId);
            tokenRefreshed = true;
            res = tencentApiClient.addDraft(token, body);
        }
        if (!isWeChatSuccess(res))
        {
            return AjaxResult.error("新增草稿失败: " + res.getString("errmsg"));
        }

        String mediaId = res.getString("media_id");

        try
        {
            JSONObject detail = tencentApiClient.getDraft(token, mediaId);
            if (isWeChatSuccess(detail))
            {
                WechatMpArticleDraft dto = new WechatMpArticleDraft();
                dto.setMediaId(mediaId);
                WechatMpDraftFlattenSupport.applyDraftDetail(dto, detail);
                if (request.getArticles().size() == 1
                        && request.getArticles().get(0).getContentMd() != null)
                {
                    dto.setContentMd(request.getArticles().get(0).getContentMd());
                }

                CrmWechatMpArticleDraft entity = toDomain(appId, dto);
                entity.setSynced(false); // 新建草稿标记为未同步，后续由 /drafts/sync 推送
                if (entity.getPublished() == null)
                {
                    entity.setPublished(false);
                }
                List<CrmWechatMpArticleDraft> list = new ArrayList<>();
                list.add(entity);
                draftMapper.batchUpsert(list);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to fetch draft detail after create for mediaId={}: {}", mediaId, e.getMessage());
        }

        return AjaxResult.success("新增草稿成功", mediaId);
    }

    @Override
    public AjaxResult updateDraft(String appId, String mediaId, WechatMpDraftUpdateRequest request, boolean localOnly)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(mediaId))
        {
            return AjaxResult.error("appId 和 mediaId 不能为空");
        }
        if (request == null || request.getArticles() == null)
        {
            return AjaxResult.error("articles 不能为空");
        }

        if (localOnly)
        {
            return updateDraftLocalOnly(appId, mediaId, request);
        }
        return updateDraftRemote(appId, mediaId, request);
    }

    private AjaxResult updateDraftLocalOnly(String appId, String mediaId, WechatMpDraftUpdateRequest request)
    {
        WechatMpDraftUpdateRequest.Article article = request.getArticles();
        log.info("updateDraftLocalOnly mediaId={}, contentMd={}, contentLength={}",
                mediaId,
                article.getContentMd() != null ? article.getContentMd().substring(0, Math.min(50, article.getContentMd().length())) : "null",
                article.getContent() != null ? article.getContent().length() : 0);
        String htmlContent = resolveContent(article.getContentMd(), article.getContent());

        CrmWechatMpArticleDraft entity = new CrmWechatMpArticleDraft();
        entity.setAppId(appId);
        entity.setMediaId(mediaId);
        entity.setIndexInMsg(request.getIndex());
        entity.setArticleType(article.getArticleType() != null ? article.getArticleType() : "news");
        entity.setTitle(article.getTitle());
        entity.setAuthor(article.getAuthor());
        entity.setDigest(article.getDigest());
        entity.setContent(htmlContent);
        entity.setContentMd(article.getContentMd());
        entity.setContentSourceUrl(article.getContentSourceUrl());
        entity.setThumbMediaId(article.getThumbMediaId());
        entity.setNeedOpenComment(article.getNeedOpenComment() != null ? article.getNeedOpenComment() : 0);
        entity.setOnlyFansCanComment(article.getOnlyFansCanComment() != null ? article.getOnlyFansCanComment() : 0);
        entity.setSynced(false);

        List<CrmWechatMpArticleDraft> list = new ArrayList<>();
        list.add(entity);
        draftMapper.batchUpsert(list);

        return AjaxResult.success("草稿已保存到本地");
    }

    private AjaxResult updateDraftRemote(String appId, String mediaId, WechatMpDraftUpdateRequest request)
    {
        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;

        JSONObject body = new JSONObject();
        body.put("media_id", mediaId);
        body.put("index", request.getIndex());

        WechatMpDraftUpdateRequest.Article article = request.getArticles();
        String htmlContent = resolveContent(article.getContentMd(), article.getContent());
        JSONObject a = new JSONObject();
        a.put("article_type", article.getArticleType() != null ? article.getArticleType() : "news");
        a.put("title", article.getTitle());
        a.put("content", htmlContent);
        a.put("need_open_comment", article.getNeedOpenComment() != null ? article.getNeedOpenComment() : 0);
        a.put("only_fans_can_comment", article.getOnlyFansCanComment() != null ? article.getOnlyFansCanComment() : 0);
        putIfNotNull(a, "author", article.getAuthor());
        putIfNotNull(a, "digest", article.getDigest());
        putIfNotNull(a, "content_source_url", article.getContentSourceUrl());
        putIfNotNull(a, "thumb_media_id", article.getThumbMediaId());
        body.put("articles", a);

        JSONObject res = tencentApiClient.updateDraft(token, body);
        if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
        {
            token = tokenService.refreshAccessToken(appId);
            tokenRefreshed = true;
            res = tencentApiClient.updateDraft(token, body);
        }
        if (!isWeChatSuccess(res))
        {
            return AjaxResult.error("修改草稿失败: " + res.getString("errmsg"));
        }

        try
        {
            JSONObject detail = tencentApiClient.getDraft(token, mediaId);
            if (isWeChatSuccess(detail))
            {
                WechatMpArticleDraft dto = new WechatMpArticleDraft();
                dto.setMediaId(mediaId);
                WechatMpDraftFlattenSupport.applyDraftDetail(dto, detail);
                if (request.getArticles().getContentMd() != null)
                {
                    dto.setContentMd(request.getArticles().getContentMd());
                }

                CrmWechatMpArticleDraft entity = toDomain(appId, dto);
                entity.setSynced(true);
                if (entity.getPublished() == null)
                {
                    entity.setPublished(false);
                }
                List<CrmWechatMpArticleDraft> list = new ArrayList<>();
                list.add(entity);
                draftMapper.batchUpsert(list);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to fetch draft detail after update for mediaId={}: {}", mediaId, e.getMessage());
        }

        return AjaxResult.success("修改草稿成功");
    }

    @Override
    public AjaxResult deleteDraft(String appId, String mediaId, boolean localOnly)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(mediaId))
        {
            return AjaxResult.error("appId 和 mediaId 不能为空");
        }

        if (localOnly)
        {
            draftMapper.deleteByMediaId(appId, mediaId);
            return AjaxResult.success("本地草稿已删除");
        }

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;

        JSONObject res = tencentApiClient.deleteDraft(token, mediaId);
        if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
        {
            token = tokenService.refreshAccessToken(appId);
            tokenRefreshed = true;
            res = tencentApiClient.deleteDraft(token, mediaId);
        }
        if (!isWeChatSuccess(res))
        {
            return AjaxResult.error("删除草稿失败: " + res.getString("errmsg"));
        }

        draftMapper.deleteByMediaId(appId, mediaId);

        return AjaxResult.success("删除草稿成功");
    }

    @Override
    public AjaxResult syncDraft(String appId, String mediaId, WechatMpDraftSyncRequest body)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(mediaId))
        {
            return AjaxResult.error("appId 和 mediaId 不能为空");
        }

        // 从 DB 读取草稿数据
        List<CrmWechatMpArticleDraft> rows = draftMapper.selectPageByAppId(appId, 0, MAX_COUNT);
        CrmWechatMpArticleDraft draft = null;
        for (CrmWechatMpArticleDraft row : rows)
        {
            if (mediaId.equals(row.getMediaId()))
            {
                draft = row;
                break;
            }
        }
        if (draft == null)
        {
            return AjaxResult.error("本地草稿不存在");
        }

        // 合并字段：Body 中有非空值的字段覆盖 DB，否则沿用 DB
        String mergedContent = mergeContent(body, draft);
        if (mergedContent == null || mergedContent.isEmpty())
        {
            return AjaxResult.error("草稿正文不能为空");
        }
        String mergedContentMd = getNonEmpty(body != null ? body.getContentMd() : null, draft.getContentMd());
        String mergedTitle = getNonEmpty(body != null ? body.getTitle() : null, draft.getTitle());
        String mergedAuthor = getNonEmpty(body != null ? body.getAuthor() : null, draft.getAuthor());
        String mergedDigest = getNonEmpty(body != null ? body.getDigest() : null, draft.getDigest());
        String mergedContentSourceUrl = getNonEmpty(body != null ? body.getContentSourceUrl() : null, draft.getContentSourceUrl());
        String mergedThumbMediaId = getNonEmpty(body != null ? body.getThumbMediaId() : null, draft.getThumbMediaId());
        Integer mergedNeedOpenComment = body != null && body.getNeedOpenComment() != null
                ? body.getNeedOpenComment() : draft.getNeedOpenComment();
        Integer mergedOnlyFansCanComment = body != null && body.getOnlyFansCanComment() != null
                ? body.getOnlyFansCanComment() : draft.getOnlyFansCanComment();
        int mergedIndex = body != null && body.getIndex() != null ? body.getIndex() : draft.getIndexInMsg();

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;

        // 先尝试 draft/update
        JSONObject updateBody = new JSONObject();
        updateBody.put("media_id", mediaId);
        updateBody.put("index", mergedIndex);
        JSONObject articleObj = new JSONObject();
        articleObj.put("article_type", draft.getArticleType() != null ? draft.getArticleType() : "news");
        articleObj.put("title", mergedTitle);
        articleObj.put("content", mergedContent);
        articleObj.put("need_open_comment", mergedNeedOpenComment != null ? mergedNeedOpenComment : 0);
        articleObj.put("only_fans_can_comment", mergedOnlyFansCanComment != null ? mergedOnlyFansCanComment : 0);
        putIfNotNull(articleObj, "author", mergedAuthor);
        putIfNotNull(articleObj, "digest", mergedDigest);
        putIfNotNull(articleObj, "content_source_url", mergedContentSourceUrl);
        putIfNotNull(articleObj, "thumb_media_id", mergedThumbMediaId);
        updateBody.put("articles", articleObj);

        JSONObject res = tencentApiClient.updateDraft(token, updateBody);
        if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
        {
            token = tokenService.refreshAccessToken(appId);
            tokenRefreshed = true;
            res = tencentApiClient.updateDraft(token, updateBody);
        }

        String newMediaId = mediaId;

        // 如果 update 失败且错误码是 invalid media_id (40007)，说明微信侧没有这个草稿，改用 draft/add
        if (!isWeChatSuccess(res) && res.getIntValue("errcode") == ERRCODE_INVALID_MEDIA_ID)
        {
            JSONObject addBody = new JSONObject();
            JSONArray articlesArr = new JSONArray();
            JSONObject addArticle = new JSONObject();
            addArticle.put("article_type", draft.getArticleType() != null ? draft.getArticleType() : "news");
            addArticle.put("title", mergedTitle);
            addArticle.put("content", mergedContent);
            addArticle.put("need_open_comment", mergedNeedOpenComment != null ? mergedNeedOpenComment : 0);
            addArticle.put("only_fans_can_comment", mergedOnlyFansCanComment != null ? mergedOnlyFansCanComment : 0);
            putIfNotNull(addArticle, "author", mergedAuthor);
            putIfNotNull(addArticle, "digest", mergedDigest);
            putIfNotNull(addArticle, "content_source_url", mergedContentSourceUrl);
            putIfNotNull(addArticle, "thumb_media_id", mergedThumbMediaId);
            articlesArr.add(addArticle);
            addBody.put("articles", articlesArr);

            res = tencentApiClient.addDraft(token, addBody);
            if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
            {
                token = tokenService.refreshAccessToken(appId);
                tokenRefreshed = true;
                res = tencentApiClient.addDraft(token, addBody);
            }
            newMediaId = res.getString("media_id");
        }

        if (!isWeChatSuccess(res))
        {
            return AjaxResult.error("同步草稿失败: " + res.getString("errmsg"));
        }

        // 落库：写回合并后的字段，更新 synced 和 media_id
        draftMapper.updateSyncedByMediaId(appId, mediaId, newMediaId, true);

        // 通过 batchUpsert 写回合并后的 content/contentMd/元数据
        CrmWechatMpArticleDraft entity = new CrmWechatMpArticleDraft();
        entity.setAppId(appId);
        entity.setMediaId(newMediaId);
        entity.setIndexInMsg(mergedIndex);
        entity.setArticleType(draft.getArticleType());
        entity.setTitle(mergedTitle);
        entity.setAuthor(mergedAuthor);
        entity.setDigest(mergedDigest);
        entity.setContent(mergedContent);
        entity.setContentMd(mergedContentMd);
        entity.setContentSourceUrl(mergedContentSourceUrl);
        entity.setThumbMediaId(mergedThumbMediaId);
        entity.setNeedOpenComment(mergedNeedOpenComment);
        entity.setOnlyFansCanComment(mergedOnlyFansCanComment);
        entity.setUrl(draft.getUrl());
        entity.setSynced(true);
        entity.setPublished(draft.getPublished() != null ? draft.getPublished() : Boolean.FALSE);
        entity.setUpdateTime(java.time.OffsetDateTime.now());
        List<CrmWechatMpArticleDraft> list = new ArrayList<>();
        list.add(entity);
        draftMapper.batchUpsert(list);

        return AjaxResult.success("同步草稿成功", newMediaId);
    }

    /**
     * 合并正文：Body.content > DB.content > flexmark(contentMd from Body or DB)。
     * 正文优先级规则与文档严格一致：Body 有非空 content 时绝不触发 flexmark 转换。
     */
    private String mergeContent(WechatMpDraftSyncRequest body, CrmWechatMpArticleDraft draft)
    {
        if (body != null && body.getContent() != null && !body.getContent().isEmpty())
        {
            return body.getContent();
        }
        if (draft.getContent() != null && !draft.getContent().isEmpty())
        {
            return draft.getContent();
        }
        // 最后尝试 contentMd
        String fallbackMd = getNonEmpty(body != null ? body.getContentMd() : null, draft.getContentMd());
        if (fallbackMd != null && !fallbackMd.isEmpty())
        {
            return MarkdownToHtmlConverter.toHtml(fallbackMd);
        }
        return null;
    }

    /**
     * 取两个值中第一个非空（不是 null 且不是空字符串）的值。优先取 preferred。
     */
    private static String getNonEmpty(String preferred, String fallback)
    {
        if (preferred != null && !preferred.isEmpty())
        {
            return preferred;
        }
        return fallback;
    }

    /**
     * 只在 value 非 null 且非空字符串时才写入 JSONObject，避免微信 API 将 null 当作 invalid media_id 等错误。
     */
    private static void putIfNotNull(JSONObject json, String key, String value)
    {
        if (value != null && !value.isEmpty())
        {
            json.put(key, value);
        }
    }

    // ======================== 增量同步 ========================

    private void syncIncremental(String appId)
    {
        java.time.OffsetDateTime maxUpdateTime = draftMapper.selectMaxUpdateTime(appId);

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;
        int offset = 0;
        int batchCount = 0;

        while (batchCount < SYNC_MAX_BATCHES)
        {
            JSONObject res = tencentApiClient.batchGetDrafts(token, offset, MAX_COUNT, 0);
            if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
            {
                token = tokenService.refreshAccessToken(appId);
                tokenRefreshed = true;
                res = tencentApiClient.batchGetDrafts(token, offset, MAX_COUNT, 0);
            }
            if (!isWeChatSuccess(res))
            {
                log.warn("batchget drafts returned error for appId={}, errcode={}, errmsg={}",
                        appId, res.getIntValue("errcode"), res.getString("errmsg"));
                break;
            }

            JSONArray items = res.getJSONArray("item");
            if (items == null || items.isEmpty())
            {
                break;
            }

            List<WechatMpArticleDraft> newDrafts = WechatMpDraftFlattenSupport.flatten(res);

            List<WechatMpArticleDraft> toSync = new ArrayList<>();
            boolean reachedExisting = false;
            for (WechatMpArticleDraft draft : newDrafts)
            {
                if (maxUpdateTime != null
                        && draft.getUpdateTime() != null
                        && !draft.getUpdateTime().isAfter(maxUpdateTime))
                {
                    reachedExisting = true;
                    break;
                }
                toSync.add(draft);
            }

            if (!toSync.isEmpty())
            {
                for (WechatMpArticleDraft draft : toSync)
                {
                    fetchDraftDetail(token, tokenRefreshed, appId, draft);
                }

                List<CrmWechatMpArticleDraft> entities = toDomainList(appId, toSync);
                // 从微信同步的数据标记为已同步；published 保持 toDomain 默认 false（不覆盖已有行，见 upsert COALESCE）
                for (CrmWechatMpArticleDraft entity : entities)
                {
                    entity.setSynced(true);
                    if (entity.getPublished() == null)
                    {
                        entity.setPublished(false);
                    }
                }
                draftMapper.batchUpsert(entities);
            }

            long totalCount = WechatMpDraftFlattenSupport.extractTotalCount(res);
            offset += items.size();
            batchCount++;

            if (reachedExisting || offset >= totalCount)
            {
                break;
            }
        }
    }

    private void fetchDraftDetail(String token, boolean tokenRefreshed,
            String appId, WechatMpArticleDraft draft)
    {
        try
        {
            String tk = token;
            JSONObject detail = tencentApiClient.getDraft(tk, draft.getMediaId());
            if (tencentApiClient.isTokenInvalid(detail) && !tokenRefreshed)
            {
                tk = tokenService.refreshAccessToken(appId);
                detail = tencentApiClient.getDraft(tk, draft.getMediaId());
            }
            if (isWeChatSuccess(detail))
            {
                WechatMpDraftFlattenSupport.applyDraftDetail(draft, detail);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to fetch draft detail for mediaId={}: {}", draft.getMediaId(), e.getMessage());
        }
    }

    @Override
    public AjaxResult publishDraft(String appId, String mediaId)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(mediaId))
        {
            return AjaxResult.error("appId 和 mediaId 不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;

        JSONObject res = tencentApiClient.freepublishSubmit(token, mediaId);
        if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
        {
            token = tokenService.refreshAccessToken(appId);
            tokenRefreshed = true;
            res = tencentApiClient.freepublishSubmit(token, mediaId);
        }
        if (!isWeChatSuccess(res))
        {
            return AjaxResult.error("发布草稿失败: " + res.getString("errmsg"));
        }

        // 写入发布任务记录，关联草稿和后续回调/统计
        try
        {
            CrmWechatMpPublishTask task = new CrmWechatMpPublishTask();
            task.setAppId(appId);
            task.setMediaId(mediaId);
            task.setPublishId(res.getString("publish_id"));
            task.setMsgDataId(res.getString("msg_data_id"));
            task.setIndexInMsg(0);
            task.setPublishStatus(1); // 1=发布中
            publishTaskMapper.insert(task);
        }
        catch (Exception e)
        {
            log.warn("Failed to insert publish_task for mediaId={}: {}", mediaId, e.getMessage());
        }

        // 同时标记草稿为已发布
        try
        {
            draftMapper.updatePublished(appId, mediaId, true);
        }
        catch (Exception e2)
        {
            log.warn("Failed to update published flag for mediaId={}: {}", mediaId, e2.getMessage());
        }

        return AjaxResult.success("草稿已提交发布");
    }

    @Override
    public AjaxResult updatePublished(String appId, String mediaId, boolean published)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(mediaId))
        {
            return AjaxResult.error("appId 和 mediaId 不能为空");
        }

        CrmWechatMpArticleDraft draft = draftMapper.selectByMediaId(appId, mediaId);
        if (draft == null)
        {
            return AjaxResult.error("草稿不存在");
        }

        draftMapper.updatePublished(appId, mediaId, published);
        return AjaxResult.success(published ? "已标记为已发布" : "已取消发布标记");
    }

    @Override
    public AjaxResult queryPublishStatus(String appId, String publishId)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(publishId))
        {
            return AjaxResult.error("appId 和 publishId 不能为空");
        }

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;

        JSONObject res = tencentApiClient.freepublishGet(token, publishId);
        if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
        {
            token = tokenService.refreshAccessToken(appId);
            tokenRefreshed = true;
            res = tencentApiClient.freepublishGet(token, publishId);
        }
        if (!isWeChatSuccess(res))
        {
            return AjaxResult.error("查询发布状态失败: " + res.getString("errmsg"));
        }

        applyPublishResult(publishId, res);
        return AjaxResult.success("发布状态已更新");
    }

    @Override
    public AjaxResult refreshAllPendingStatus(String appId)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }

        List<CrmWechatMpPublishTask> pendingTasks = publishTaskMapper.selectAllPending();
        if (pendingTasks.isEmpty())
        {
            return AjaxResult.success("没有发布中的任务", 0);
        }

        String token = tokenService.getAccessToken(appId);
        boolean tokenRefreshed = false;
        int updated = 0;
        int failed = 0;

        for (CrmWechatMpPublishTask task : pendingTasks)
        {
            try
            {
                JSONObject res = tencentApiClient.freepublishGet(token, task.getPublishId());
                if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
                {
                    token = tokenService.refreshAccessToken(appId);
                    tokenRefreshed = true;
                    res = tencentApiClient.freepublishGet(token, task.getPublishId());
                }
                if (!isWeChatSuccess(res))
                {
                    log.warn("Query publish status failed for publishId={}: {}", task.getPublishId(), res.getString("errmsg"));
                    failed++;
                    continue;
                }
                applyPublishResult(task.getPublishId(), res);
                updated++;
            }
            catch (Exception e)
            {
                log.error("Error querying publish status for publishId={}", task.getPublishId(), e);
                failed++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("pending", pendingTasks.size());
        result.put("updated", updated);
        result.put("failed", failed);
        return AjaxResult.success("已刷新发布状态", result);
    }

    /**
     * 从微信 freepublish/get 响应中提取发布结果并更新 publish_task 表。
     */
    private void applyPublishResult(String publishId, JSONObject res)
    {
        CrmWechatMpPublishTask task = new CrmWechatMpPublishTask();
        task.setPublishId(publishId);
        task.setPublishStatus(res.getIntValue("publish_status"));

        String articleId = res.getString("article_id");
        if (StringUtils.hasText(articleId))
        {
            task.setArticleId(articleId);
        }

        JSONObject articleDetail = res.getJSONObject("article_detail");
        if (articleDetail != null)
        {
            JSONArray itemArray = articleDetail.getJSONArray("item");
            if (itemArray != null && !itemArray.isEmpty())
            {
                JSONObject firstItem = itemArray.getJSONObject(0);
                if (firstItem != null)
                {
                    String articleUrl = firstItem.getString("article_url");
                    if (StringUtils.hasText(articleUrl))
                    {
                        task.setArticleUrl(articleUrl);
                    }
                }
            }
        }

        JSONArray failIdxsArr = res.getJSONArray("fail_idx");
        if (failIdxsArr != null && !failIdxsArr.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < failIdxsArr.size(); i++)
            {
                if (sb.length() > 0) sb.append(",");
                sb.append(failIdxsArr.getIntValue(i));
            }
            task.setFailIdxs(sb.toString());
        }

        publishTaskMapper.updateByPublishId(task);

        log.info("Publish status updated: publishId={}, status={}, articleId={}, articleUrl={}",
                publishId, task.getPublishStatus(), task.getArticleId(), task.getArticleUrl());
    }

    @Override
    public AjaxResult uploadImage(String appId, MultipartFile file)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }
        if (file == null || file.isEmpty())
        {
            return AjaxResult.error("上传文件不能为空");
        }

        // 校验文件大小（微信限制 1MB）
        if (file.getSize() > 1024 * 1024)
        {
            return AjaxResult.error("图片大小不能超过 1MB");
        }

        // 将 MultipartFile 转存为临时文件
        java.io.File tempFile = null;
        try
        {
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains("."))
            {
                suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            tempFile = java.io.File.createTempFile("wechat_mp_upload_", suffix);
            file.transferTo(tempFile);

            String token = tokenService.getAccessToken(appId);
            JSONObject res = tencentApiClient.uploadImage(token, tempFile);

            if (tencentApiClient.isTokenInvalid(res))
            {
                token = tokenService.refreshAccessToken(appId);
                res = tencentApiClient.uploadImage(token, tempFile);
            }

            if (res == null || res.getIntValue("errcode") != 0)
            {
                String errmsg = res != null ? res.getString("errmsg") : "未知错误";
                return AjaxResult.error("上传图片失败: " + errmsg);
            }

            return AjaxResult.success("上传成功", res.getString("url"));
        }
        catch (Exception e)
        {
            log.error("uploadImage failed for appId={}", appId, e);
            return AjaxResult.error("上传图片失败: " + e.getMessage());
        }
        finally
        {
            if (tempFile != null && tempFile.exists())
            {
                tempFile.delete();
            }
        }
    }

    @Override
    public AjaxResult fetchDraftsFromWechat(String appId)
    {
        if (!StringUtils.hasText(appId))
        {
            return AjaxResult.error("appId 不能为空");
        }
        try
        {
            syncIncremental(appId);
            return AjaxResult.success("增量同步完成");
        }
        catch (Exception e)
        {
            log.error("fetchDraftsFromWechat failed for appId={}", appId, e);
            return AjaxResult.error("增量同步失败: " + e.getMessage());
        }
    }

    // ======================== 辅助方法 ========================

    /**
     * 正文优先级：非空 HTML content 优先（前端微信友好样式）；
     * 仅有 contentMd 时才用 flexmark 转 HTML。与 docs/wechat-mp-draft-article-api 约定一致。
     */
    private String resolveContent(String contentMd, String htmlContent)
    {
        if (htmlContent != null && !htmlContent.trim().isEmpty())
        {
            return htmlContent;
        }
        if (contentMd != null && !contentMd.isEmpty())
        {
            return MarkdownToHtmlConverter.toHtml(contentMd);
        }
        return htmlContent;
    }

    private boolean isWeChatSuccess(JSONObject res)
    {
        return res != null && res.getIntValue("errcode") == 0;
    }

    private List<CrmWechatMpArticleDraft> toDomainList(String appId, List<WechatMpArticleDraft> dtos)
    {
        List<CrmWechatMpArticleDraft> entities = new ArrayList<>(dtos.size());
        for (WechatMpArticleDraft dto : dtos)
        {
            entities.add(toDomain(appId, dto));
        }
        return entities;
    }

    private CrmWechatMpArticleDraft toDomain(String appId, WechatMpArticleDraft dto)
    {
        CrmWechatMpArticleDraft entity = new CrmWechatMpArticleDraft();
        entity.setAppId(appId);
        entity.setMediaId(dto.getMediaId());
        entity.setIndexInMsg(dto.getIndexInMsg());
        entity.setArticleType(dto.getArticleType() != null ? dto.getArticleType() : "news");
        entity.setTitle(dto.getTitle());
        entity.setAuthor(dto.getAuthor());
        entity.setDigest(dto.getDigest());
        entity.setContent(dto.getContent());
        entity.setContentMd(dto.getContentMd());
        entity.setContentSourceUrl(dto.getContentSourceUrl());
        entity.setThumbMediaId(dto.getThumbMediaId());
        entity.setNeedOpenComment(dto.getNeedOpenComment());
        entity.setOnlyFansCanComment(dto.getOnlyFansCanComment());
        entity.setUrl(dto.getUrl());
        entity.setSynced(dto.getSynced());
        // published NOT NULL：微信同步路径常未带该字段，默认未发布
        entity.setPublished(dto.getPublished() != null ? dto.getPublished() : Boolean.FALSE);
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }

    private List<WechatMpArticleDraft> toDtoList(List<CrmWechatMpArticleDraft> entities)
    {
        List<WechatMpArticleDraft> dtos = new ArrayList<>(entities.size());
        for (CrmWechatMpArticleDraft entity : entities)
        {
            WechatMpArticleDraft dto = new WechatMpArticleDraft();
            dto.setMediaId(entity.getMediaId());
            dto.setIndexInMsg(entity.getIndexInMsg());
            dto.setArticleType(entity.getArticleType());
            dto.setTitle(entity.getTitle());
            dto.setAuthor(entity.getAuthor());
            dto.setDigest(entity.getDigest());
            dto.setContent(entity.getContent());
            dto.setContentMd(entity.getContentMd());
            dto.setContentSourceUrl(entity.getContentSourceUrl());
            dto.setThumbMediaId(entity.getThumbMediaId());
            dto.setNeedOpenComment(entity.getNeedOpenComment());
            dto.setOnlyFansCanComment(entity.getOnlyFansCanComment());
            dto.setUrl(entity.getUrl());
            dto.setSynced(entity.getSynced());
            dto.setPublished(entity.getPublished());
            dto.setUpdateTime(entity.getUpdateTime());
            dtos.add(dto);
        }
        return dtos;
    }
}
