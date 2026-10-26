package com.ruoyi.crm.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmMarketingContent;
import com.ruoyi.crm.domain.CrmMarketingContentPublish;
import com.ruoyi.crm.domain.dto.MarketingContentPublishRefreshRequest;
import com.ruoyi.crm.domain.dto.MarketingContentPublishRequest;
import com.ruoyi.crm.domain.dto.MarketingContentQuery;
import com.ruoyi.crm.domain.vo.MarketingContentDetailVo;
import com.ruoyi.crm.domain.vo.MarketingContentListVo;
import com.ruoyi.crm.domain.vo.MarketingContentPublishResultVo;
import com.ruoyi.crm.domain.vo.MarketingContentPublishVo;
import com.ruoyi.crm.knowledge.CrmKnowledgeIngestService;
import com.ruoyi.crm.mapper.CrmMarketingContentMapper;
import com.ruoyi.crm.mapper.CrmMarketingContentPublishMapper;
import com.ruoyi.crm.service.ICrmMarketingContentService;
import com.ruoyi.crm.service.support.MarketingContentVoSupport;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMaterial;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpPublishTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftAddRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftSyncRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMaterialMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpPublishTaskMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleDraftService;
import com.ruoyi.crm.wechatmp.support.MarkdownToHtmlConverter;

@Service
public class CrmMarketingContentServiceImpl implements ICrmMarketingContentService
{
    private static final Logger log = LoggerFactory.getLogger(CrmMarketingContentServiceImpl.class);

    private static final Set<String> ALLOWED_STATUS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            CrmMarketingContent.STATUS_DRAFT,
            CrmMarketingContent.STATUS_READY,
            CrmMarketingContent.STATUS_ARCHIVED)));

    /** 微信 freepublish publish_status：0=成功 */
    private static final int WX_PUBLISH_SUCCESS = 0;

    @Autowired
    private CrmMarketingContentMapper contentMapper;

    @Autowired
    private CrmMarketingContentPublishMapper publishMapper;

    @Autowired
    private IWechatMpArticleDraftService draftService;

    @Autowired
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Autowired
    private CrmWechatMpMaterialMapper materialMapper;

    @Autowired
    private CrmWechatMpPublishTaskMapper publishTaskMapper;

    @Autowired
    private CrmKnowledgeIngestService knowledgeIngestService;

    @Override
    public List<MarketingContentListVo> selectList(MarketingContentQuery query)
    {
        return contentMapper.selectList(query != null ? query : new MarketingContentQuery());
    }

    @Override
    public MarketingContentDetailVo getDetail(String id)
    {
        CrmMarketingContent content = requireContent(id);
        return MarketingContentVoSupport.toDetailVo(content, publishMapper.selectByContentId(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketingContentDetailVo create(CrmMarketingContent request)
    {
        if (request == null)
        {
            throw new ServiceException("请求不能为空", HttpStatus.BAD_REQUEST);
        }
        requireNonBlankTitle(request.getTitle());
        String status = StringUtils.hasText(request.getStatus()) ? request.getStatus().trim() : CrmMarketingContent.STATUS_DRAFT;
        requireAllowedStatus(status);

        Date now = MarketingContentVoSupport.now();
        String username = safeUsername();

        CrmMarketingContent entity = new CrmMarketingContent();
        entity.setId(UUID.randomUUID().toString());
        entity.setTitle(request.getTitle().trim());
        entity.setAuthor(blankToNull(request.getAuthor()));
        entity.setDigest(blankToNull(request.getDigest()));
        entity.setCoverUrl(blankToNull(request.getCoverUrl()));
        entity.setContentMd(request.getContentMd() != null ? request.getContentMd() : "");
        entity.setContentHtml(request.getContentHtml());
        entity.setStatus(status);
        boolean websiteVisible = Boolean.TRUE.equals(request.getWebsiteVisible());
        entity.setWebsiteVisible(websiteVisible);
        entity.setWebsitePublishedAt(websiteVisible ? now : null);
        entity.setCreateBy(username);
        entity.setCreateTime(now);
        entity.setUpdateBy(username);
        entity.setUpdateTime(now);
        contentMapper.insert(entity);
        knowledgeIngestService.onMarketingContentChanged(entity.getId());
        return getDetail(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketingContentDetailVo update(CrmMarketingContent request)
    {
        if (request == null || !StringUtils.hasText(request.getId()))
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmMarketingContent existing = requireContent(request.getId());

        if (request.getTitle() != null)
        {
            requireNonBlankTitle(request.getTitle());
            request.setTitle(request.getTitle().trim());
        }
        if (request.getStatus() != null)
        {
            requireAllowedStatus(request.getStatus().trim());
            request.setStatus(request.getStatus().trim());
        }
        if (request.getWebsiteVisible() != null)
        {
            applyWebsiteVisibility(request, existing, request.getWebsiteVisible());
        }

        request.setUpdateBy(safeUsername());
        request.setUpdateTime(MarketingContentVoSupport.now());
        contentMapper.update(request);
        knowledgeIngestService.onMarketingContentChanged(request.getId());
        return getDetail(request.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketingContentDetailVo publishToWebsite(String id)
    {
        CrmMarketingContent existing = requireContent(id);
        CrmMarketingContent patch = new CrmMarketingContent();
        patch.setId(id);
        applyWebsiteVisibility(patch, existing, true);
        patch.setUpdateBy(safeUsername());
        patch.setUpdateTime(MarketingContentVoSupport.now());
        contentMapper.update(patch);
        return getDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketingContentDetailVo unpublishFromWebsite(String id)
    {
        CrmMarketingContent existing = requireContent(id);
        CrmMarketingContent patch = new CrmMarketingContent();
        patch.setId(id);
        applyWebsiteVisibility(patch, existing, false);
        patch.setUpdateBy(safeUsername());
        patch.setUpdateTime(MarketingContentVoSupport.now());
        contentMapper.update(patch);
        return getDetail(id);
    }

    /**
     * 设置官网可见：true 时若原先不可见则写 published_at；false 时清空 published_at。
     */
    private static void applyWebsiteVisibility(CrmMarketingContent target, CrmMarketingContent existing,
            boolean visible)
    {
        target.setWebsiteVisible(visible);
        if (visible)
        {
            boolean wasVisible = Boolean.TRUE.equals(existing.getWebsiteVisible());
            if (wasVisible && existing.getWebsitePublishedAt() != null)
            {
                target.setWebsitePublishedAt(existing.getWebsitePublishedAt());
            }
            else
            {
                target.setWebsitePublishedAt(MarketingContentVoSupport.now());
            }
        }
        else
        {
            target.setWebsitePublishedAt(null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id)
    {
        requireContent(id);
        contentMapper.deleteById(id);
        knowledgeIngestService.onMarketingContentDeleted(id);
    }

    @Override
    public List<MarketingContentPublishVo> listPublishes(String id)
    {
        requireContent(id);
        return MarketingContentVoSupport.toPublishVoList(publishMapper.selectByContentId(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketingContentPublishResultVo publish(String id, MarketingContentPublishRequest request)
    {
        if (request == null || !StringUtils.hasText(request.getAppId()))
        {
            throw new ServiceException("no_app_id", HttpStatus.BAD_REQUEST);
        }
        String appId = request.getAppId().trim();
        validateAppId(appId);

        CrmMarketingContent content = requireContent(id);

        boolean hasHtml = StringUtils.hasText(request.getContent());
        boolean hasMd = StringUtils.hasText(request.getContentMd());
        if (!hasHtml && !hasMd)
        {
            throw new ServiceException("no_content", HttpStatus.BAD_REQUEST);
        }

        // 正文双存：非空 HTML 优先；禁止用 flexmark 覆盖 HTML
        String htmlForWx;
        String mdToStore = null;
        if (hasHtml)
        {
            htmlForWx = request.getContent();
            if (request.getContentMd() != null)
            {
                mdToStore = request.getContentMd();
            }
        }
        else
        {
            mdToStore = request.getContentMd();
            htmlForWx = MarkdownToHtmlConverter.toHtml(request.getContentMd());
        }

        if (!StringUtils.hasText(htmlForWx))
        {
            throw new ServiceException("no_content", HttpStatus.BAD_REQUEST);
        }

        // 写回内容表（可选元数据覆盖）
        CrmMarketingContent patch = new CrmMarketingContent();
        patch.setId(id);
        patch.setContentHtml(htmlForWx);
        if (mdToStore != null)
        {
            patch.setContentMd(mdToStore);
        }
        if (StringUtils.hasText(request.getTitle()))
        {
            patch.setTitle(request.getTitle().trim());
            content.setTitle(patch.getTitle());
        }
        if (request.getAuthor() != null)
        {
            patch.setAuthor(request.getAuthor());
            content.setAuthor(request.getAuthor());
        }
        if (request.getDigest() != null)
        {
            patch.setDigest(request.getDigest());
            content.setDigest(request.getDigest());
        }
        patch.setUpdateBy(safeUsername());
        patch.setUpdateTime(MarketingContentVoSupport.now());
        contentMapper.update(patch);

        // 刷新内存中的正文
        content.setContentHtml(htmlForWx);
        if (mdToStore != null)
        {
            content.setContentMd(mdToStore);
        }

        String title = StringUtils.hasText(request.getTitle()) ? request.getTitle().trim() : content.getTitle();
        String author = request.getAuthor() != null ? request.getAuthor() : content.getAuthor();
        String digest = request.getDigest() != null ? request.getDigest() : content.getDigest();
        String contentMd = mdToStore != null ? mdToStore
                : (content.getContentMd() != null ? content.getContentMd() : "");
        String thumbMediaId = resolveThumbMediaId(appId, request.getThumbMediaId());

        CrmMarketingContentPublish publishRow = publishMapper.selectByContentChannelApp(
                id, CrmMarketingContentPublish.CHANNEL_WECHAT_MP, appId);

        String mediaId = null;
        try
        {
            if (publishRow == null || !StringUtils.hasText(publishRow.getMediaId()))
            {
                WechatMpDraftAddRequest addReq = buildAddRequest(title, author, digest, htmlForWx, contentMd,
                        thumbMediaId, request.getContentSourceUrl(),
                        request.getNeedOpenComment(), request.getOnlyFansCanComment());
                AjaxResult createResult = draftService.createDraft(appId, addReq, true);
                if (!isAjaxSuccess(createResult))
                {
                    throw new ServiceException(ajaxMsg(createResult, "draft_create_failed"), HttpStatus.ERROR);
                }
                mediaId = extractDataAsString(createResult);
                if (!StringUtils.hasText(mediaId))
                {
                    throw new ServiceException("draft_create_failed", HttpStatus.ERROR);
                }
                mediaId = syncExistingDraft(appId, mediaId, title, author, digest, htmlForWx, contentMd,
                        thumbMediaId, request.getContentSourceUrl(),
                        request.getNeedOpenComment(), request.getOnlyFansCanComment());
            }
            else
            {
                mediaId = syncExistingDraft(appId, publishRow.getMediaId(), title, author, digest,
                        htmlForWx, contentMd, thumbMediaId, request.getContentSourceUrl(),
                        request.getNeedOpenComment(), request.getOnlyFansCanComment());
            }
        }
        catch (ServiceException ex)
        {
            markPublishFailed(id, appId, publishRow, mediaId, thumbMediaId, ex.getMessage());
            throw ex;
        }

        // freepublish
        AjaxResult publishResult = draftService.publishDraft(appId, mediaId);
        if (!isAjaxSuccess(publishResult))
        {
            String err = ajaxMsg(publishResult, "publish_failed");
            markPublishFailed(id, appId, publishRow, mediaId, thumbMediaId, err);
            throw new ServiceException(err, HttpStatus.ERROR);
        }

        String publishId = resolveLatestPublishId(appId, mediaId);
        Date now = MarketingContentVoSupport.now();
        if (publishRow == null)
        {
            publishRow = new CrmMarketingContentPublish();
            publishRow.setId(UUID.randomUUID().toString());
            publishRow.setContentId(id);
            publishRow.setChannel(CrmMarketingContentPublish.CHANNEL_WECHAT_MP);
            publishRow.setAppId(appId);
            publishRow.setCreateTime(now);
            publishRow.setMediaId(mediaId);
            publishRow.setPublishId(publishId);
            publishRow.setThumbMediaId(thumbMediaId);
            publishRow.setStatus(CrmMarketingContentPublish.STATUS_PUBLISHING);
            publishRow.setErrorMsg(null);
            publishRow.setUpdateTime(now);
            publishMapper.insert(publishRow);
        }
        else
        {
            publishRow.setMediaId(mediaId);
            publishRow.setPublishId(publishId);
            publishRow.setThumbMediaId(thumbMediaId);
            publishRow.setStatus(CrmMarketingContentPublish.STATUS_PUBLISHING);
            publishRow.setErrorMsg(null);
            publishRow.setUpdateTime(now);
            publishMapper.update(publishRow);
        }

        // 尽力解析 articleUrl
        tryApplyPublishResult(publishRow);

        return new MarketingContentPublishResultVo(id, MarketingContentVoSupport.toPublishVo(publishRow));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketingContentPublishVo refreshPublish(String id, MarketingContentPublishRefreshRequest request)
    {
        if (request == null || !StringUtils.hasText(request.getAppId()))
        {
            throw new ServiceException("no_app_id", HttpStatus.BAD_REQUEST);
        }
        String appId = request.getAppId().trim();
        requireContent(id);

        CrmMarketingContentPublish publishRow = publishMapper.selectByContentChannelApp(
                id, CrmMarketingContentPublish.CHANNEL_WECHAT_MP, appId);
        if (publishRow == null)
        {
            throw new ServiceException("publish_not_found", HttpStatus.NOT_FOUND);
        }

        draftService.refreshAllPendingStatus(appId);
        tryApplyPublishResult(publishRow);

        CrmMarketingContentPublish refreshed = publishMapper.selectByContentChannelApp(
                id, CrmMarketingContentPublish.CHANNEL_WECHAT_MP, appId);
        return MarketingContentVoSupport.toPublishVo(refreshed != null ? refreshed : publishRow);
    }

    // -------------------------------------------------------------------------

    private WechatMpDraftAddRequest buildAddRequest(String title, String author, String digest,
            String html, String contentMd, String thumbMediaId, String contentSourceUrl,
            Integer needOpenComment, Integer onlyFansCanComment)
    {
        WechatMpDraftAddRequest addReq = new WechatMpDraftAddRequest();
        WechatMpDraftAddRequest.Article article = new WechatMpDraftAddRequest.Article();
        article.setArticleType("news");
        article.setTitle(title);
        article.setAuthor(author);
        article.setDigest(digest);
        article.setContent(html);
        article.setContentMd(contentMd);
        article.setThumbMediaId(thumbMediaId);
        article.setContentSourceUrl(contentSourceUrl);
        article.setNeedOpenComment(needOpenComment != null ? needOpenComment : 0);
        article.setOnlyFansCanComment(onlyFansCanComment != null ? onlyFansCanComment : 0);
        addReq.setArticles(Collections.singletonList(article));
        return addReq;
    }

    private String syncExistingDraft(String appId, String mediaId, String title, String author, String digest,
            String html, String contentMd, String thumbMediaId, String contentSourceUrl,
            Integer needOpenComment, Integer onlyFansCanComment)
    {
        WechatMpDraftSyncRequest syncBody = new WechatMpDraftSyncRequest();
        syncBody.setContent(html);
        syncBody.setContentMd(contentMd);
        syncBody.setTitle(title);
        syncBody.setAuthor(author);
        syncBody.setDigest(digest);
        syncBody.setThumbMediaId(thumbMediaId);
        syncBody.setContentSourceUrl(contentSourceUrl);
        if (needOpenComment != null)
        {
            syncBody.setNeedOpenComment(needOpenComment);
        }
        if (onlyFansCanComment != null)
        {
            syncBody.setOnlyFansCanComment(onlyFansCanComment);
        }

        AjaxResult syncResult = draftService.syncDraft(appId, mediaId, syncBody);
        if (!isAjaxSuccess(syncResult))
        {
            throw new ServiceException(ajaxMsg(syncResult, "sync_failed"), HttpStatus.ERROR);
        }
        String newMediaId = extractDataAsString(syncResult);
        return StringUtils.hasText(newMediaId) ? newMediaId : mediaId;
    }

    private void tryApplyPublishResult(CrmMarketingContentPublish publishRow)
    {
        if (publishRow == null)
        {
            return;
        }
        try
        {
            if (StringUtils.hasText(publishRow.getPublishId()))
            {
                draftService.queryPublishStatus(publishRow.getAppId(), publishRow.getPublishId());
            }

            CrmWechatMpPublishTask task = null;
            if (StringUtils.hasText(publishRow.getPublishId()))
            {
                task = publishTaskMapper.selectByPublishId(publishRow.getPublishId());
            }
            if (task == null && StringUtils.hasText(publishRow.getMediaId()))
            {
                List<CrmWechatMpPublishTask> tasks = publishTaskMapper.selectByMediaId(
                        publishRow.getAppId(), publishRow.getMediaId());
                if (tasks != null && !tasks.isEmpty())
                {
                    task = tasks.get(0);
                }
            }
            if (task == null)
            {
                return;
            }

            boolean changed = false;
            if (StringUtils.hasText(task.getPublishId())
                    && !task.getPublishId().equals(publishRow.getPublishId()))
            {
                publishRow.setPublishId(task.getPublishId());
                changed = true;
            }
            if (StringUtils.hasText(task.getArticleId()))
            {
                publishRow.setArticleId(task.getArticleId());
                changed = true;
            }
            if (StringUtils.hasText(task.getArticleUrl()))
            {
                publishRow.setArticleUrl(task.getArticleUrl());
                changed = true;
            }

            Integer wxStatus = task.getPublishStatus();
            if (wxStatus != null && wxStatus == WX_PUBLISH_SUCCESS && StringUtils.hasText(task.getArticleUrl()))
            {
                publishRow.setStatus(CrmMarketingContentPublish.STATUS_PUBLISHED);
                if (publishRow.getPublishedAt() == null)
                {
                    publishRow.setPublishedAt(MarketingContentVoSupport.now());
                }
                publishRow.setErrorMsg(null);
                changed = true;
            }
            else if (wxStatus != null && wxStatus > 1)
            {
                publishRow.setStatus(CrmMarketingContentPublish.STATUS_FAILED);
                publishRow.setErrorMsg("微信发布失败 publish_status=" + wxStatus);
                changed = true;
            }

            if (changed)
            {
                publishRow.setUpdateTime(MarketingContentVoSupport.now());
                publishMapper.update(publishRow);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to apply publish result for contentId={}, appId={}: {}",
                    publishRow.getContentId(), publishRow.getAppId(), e.getMessage());
        }
    }

    private void markPublishFailed(String contentId, String appId, CrmMarketingContentPublish existing,
            String mediaId, String thumbMediaId, String errorMsg)
    {
        try
        {
            Date now = MarketingContentVoSupport.now();
            String msg = truncate(errorMsg, 1024);
            if (existing == null)
            {
                CrmMarketingContentPublish row = new CrmMarketingContentPublish();
                row.setId(UUID.randomUUID().toString());
                row.setContentId(contentId);
                row.setChannel(CrmMarketingContentPublish.CHANNEL_WECHAT_MP);
                row.setAppId(appId);
                row.setMediaId(mediaId);
                row.setThumbMediaId(thumbMediaId);
                row.setStatus(CrmMarketingContentPublish.STATUS_FAILED);
                row.setErrorMsg(msg);
                row.setCreateTime(now);
                row.setUpdateTime(now);
                publishMapper.insert(row);
            }
            else
            {
                if (StringUtils.hasText(mediaId))
                {
                    existing.setMediaId(mediaId);
                }
                if (StringUtils.hasText(thumbMediaId))
                {
                    existing.setThumbMediaId(thumbMediaId);
                }
                existing.setStatus(CrmMarketingContentPublish.STATUS_FAILED);
                existing.setErrorMsg(msg);
                existing.setUpdateTime(now);
                publishMapper.update(existing);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to mark publish failed for contentId={}: {}", contentId, e.getMessage());
        }
    }

    private String resolveLatestPublishId(String appId, String mediaId)
    {
        List<CrmWechatMpPublishTask> tasks = publishTaskMapper.selectByMediaId(appId, mediaId);
        if (tasks == null || tasks.isEmpty())
        {
            return null;
        }
        return tasks.get(0).getPublishId();
    }

    private CrmMarketingContent requireContent(String id)
    {
        if (!StringUtils.hasText(id))
        {
            throw new ServiceException("content_not_found", HttpStatus.NOT_FOUND);
        }
        CrmMarketingContent content = contentMapper.selectById(id.trim());
        if (content == null)
        {
            throw new ServiceException("content_not_found", HttpStatus.NOT_FOUND);
        }
        return content;
    }

    private void validateAppId(String appId)
    {
        if (wechatMpConfigMapper.selectByAppId(appId) == null)
        {
            throw new ServiceException("no_app_id", HttpStatus.BAD_REQUEST);
        }
    }

    private String resolveThumbMediaId(String appId, String requested)
    {
        if (StringUtils.hasText(requested))
        {
            return requested.trim();
        }
        CrmWechatMpMaterial defaultMaterial = materialMapper.selectDefaultByAppId(appId);
        if (defaultMaterial != null && StringUtils.hasText(defaultMaterial.getMediaId()))
        {
            return defaultMaterial.getMediaId();
        }
        return null;
    }

    private static void requireNonBlankTitle(String title)
    {
        if (!StringUtils.hasText(title))
        {
            throw new ServiceException("title 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (title.trim().length() > 128)
        {
            throw new ServiceException("title 最长 128", HttpStatus.BAD_REQUEST);
        }
    }

    private static void requireAllowedStatus(String status)
    {
        if (!ALLOWED_STATUS.contains(status))
        {
            throw new ServiceException("status 仅允许 draft|ready|archived", HttpStatus.BAD_REQUEST);
        }
    }

    private static String blankToNull(String value)
    {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private static String safeUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static boolean isAjaxSuccess(AjaxResult result)
    {
        if (result == null)
        {
            return false;
        }
        Object code = result.get(AjaxResult.CODE_TAG);
        return code instanceof Number && ((Number) code).intValue() == HttpStatus.SUCCESS;
    }

    private static String extractDataAsString(AjaxResult result)
    {
        if (result == null)
        {
            return null;
        }
        Object data = result.get(AjaxResult.DATA_TAG);
        return data != null ? String.valueOf(data) : null;
    }

    private static String ajaxMsg(AjaxResult result, String fallback)
    {
        if (result == null)
        {
            return fallback;
        }
        Object msg = result.get(AjaxResult.MSG_TAG);
        if (msg != null && StringUtils.hasText(String.valueOf(msg)))
        {
            return String.valueOf(msg);
        }
        return fallback;
    }

    private static String truncate(String value, int max)
    {
        if (value == null)
        {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
