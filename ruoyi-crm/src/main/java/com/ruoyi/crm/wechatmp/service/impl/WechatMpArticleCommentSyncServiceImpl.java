package com.ruoyi.crm.wechatmp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticle;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleComment;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpPublishTask;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpArticleCommentMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpArticleMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpPublishTaskMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleCommentSyncService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.crm.wechatmp.support.WechatMpCommentApiErrors;

@Service
public class WechatMpArticleCommentSyncServiceImpl implements IWechatMpArticleCommentSyncService
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpArticleCommentSyncServiceImpl.class);

    private static final int PAGE_COUNT = 49;
    private static final int COMMENT_TYPE_ALL = 0;

    @Value("${crm.wechatMp.commentSync.lookbackDays:90}")
    private int lookbackDays;

    @Value("${crm.wechatMp.commentSync.maxArticlesPerApp:30}")
    private int maxArticlesPerApp;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Autowired
    private CrmWechatMpConfigMapper configMapper;

    @Autowired
    private CrmWechatMpPublishTaskMapper publishTaskMapper;

    @Autowired
    private CrmWechatMpArticleMapper articleMapper;

    @Autowired
    private CrmWechatMpArticleCommentMapper commentMapper;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Override
    public Map<String, Object> sync(String appId, String msgDataId, Integer index)
    {
        List<String> appIds = resolveAppIds(appId);
        int syncedArticles = 0;
        int upserted = 0;
        int skipped = 0;
        List<Map<String, Object>> errors = new ArrayList<>();

        for (String aid : appIds)
        {
            List<CrmWechatMpPublishTask> tasks = resolveTasks(aid, msgDataId, index);
            if (tasks.isEmpty())
            {
                skipped++;
                continue;
            }

            String token;
            try
            {
                token = tokenService.getAccessToken(aid);
            }
            catch (Exception e)
            {
                errors.add(errorRow(aid, null, null, "获取 access_token 失败: " + e.getMessage()));
                continue;
            }

            boolean noCommentPerm = false;
            for (CrmWechatMpPublishTask task : tasks)
            {
                if (noCommentPerm)
                {
                    skipped++;
                    continue;
                }
                if (!StringUtils.hasText(task.getMsgDataId()))
                {
                    skipped++;
                    continue;
                }
                try
                {
                    int n = syncOneArticle(aid, token, task);
                    syncedArticles++;
                    upserted += n;
                }
                catch (ServiceException se)
                {
                    JSONObject fake = new JSONObject();
                    // message already humanized
                    errors.add(errorRow(aid, task.getMsgDataId(), task.getIndexInMsg(), se.getMessage()));
                    if (se.getMessage() != null && se.getMessage().contains("无留言权限"))
                    {
                        noCommentPerm = true;
                    }
                }
                catch (Exception e)
                {
                    log.warn("comment sync appId={} msgDataId={}: {}", aid, task.getMsgDataId(), e.getMessage());
                    errors.add(errorRow(aid, task.getMsgDataId(), task.getIndexInMsg(), e.getMessage()));
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("syncedArticles", syncedArticles);
        result.put("upserted", upserted);
        result.put("skipped", skipped);
        result.put("errors", errors);
        return result;
    }

    private List<String> resolveAppIds(String appId)
    {
        if (StringUtils.hasText(appId))
        {
            CrmWechatMpConfig cfg = configMapper.selectByAppId(appId.trim());
            if (cfg == null)
            {
                throw new ServiceException("公众号配置不存在或不属于当前租户", HttpStatus.FORBIDDEN);
            }
            List<String> one = new ArrayList<>();
            one.add(appId.trim());
            return one;
        }
        List<CrmWechatMpConfig> configs = configMapper.selectAll();
        List<String> appIds = new ArrayList<>();
        if (configs != null)
        {
            for (CrmWechatMpConfig cfg : configs)
            {
                if (cfg != null && StringUtils.hasText(cfg.getAppId()))
                {
                    appIds.add(cfg.getAppId());
                }
            }
        }
        return appIds;
    }

    private List<CrmWechatMpPublishTask> resolveTasks(String appId, String msgDataId, Integer index)
    {
        if (StringUtils.hasText(msgDataId))
        {
            return publishTaskMapper.selectByAppIdAndMsgDataId(appId, msgDataId.trim(), index);
        }
        return publishTaskMapper.selectRecentSuccessWithMsgDataId(appId, lookbackDays, maxArticlesPerApp);
    }

    private int syncOneArticle(String appId, String tokenIn, CrmWechatMpPublishTask task)
    {
        String msgDataId = task.getMsgDataId();
        int index = task.getIndexInMsg();
        String articleId = task.getArticleId();
        String articleUrl = task.getArticleUrl();
        String articleTitle = null;
        if (StringUtils.hasText(articleId))
        {
            CrmWechatMpArticle article = articleMapper.selectByArticleId(appId, articleId, index);
            if (article != null)
            {
                articleTitle = article.getTitle();
                if (!StringUtils.hasText(articleUrl))
                {
                    articleUrl = article.getUrl();
                }
            }
        }

        String token = tokenIn;
        boolean tokenRefreshed = false;
        int begin = 0;
        int upserted = 0;
        while (true)
        {
            JSONObject res = tencentApiClient.commentList(token, msgDataId, index, begin, PAGE_COUNT, COMMENT_TYPE_ALL);
            if (tencentApiClient.isTokenInvalid(res) && !tokenRefreshed)
            {
                token = tokenService.refreshAccessToken(appId);
                tokenRefreshed = true;
                res = tencentApiClient.commentList(token, msgDataId, index, begin, PAGE_COUNT, COMMENT_TYPE_ALL);
            }
            if (!WechatMpCommentApiErrors.isSuccess(res))
            {
                int code = WechatMpCommentApiErrors.errcode(res);
                if (code == 88000 || code == 45009)
                {
                    throw WechatMpCommentApiErrors.toException(res);
                }
                // 无留言时可返回非 0；若 comment 为空则视为成功结束
                JSONArray emptyCheck = res.getJSONArray("comment");
                if (emptyCheck == null || emptyCheck.isEmpty())
                {
                    if (code == 0 || begin == 0)
                    {
                        // first page empty with error → surface if not benign
                        if (code != 0 && code != 88001)
                        {
                            throw WechatMpCommentApiErrors.toException(res);
                        }
                    }
                    break;
                }
                throw WechatMpCommentApiErrors.toException(res);
            }

            JSONArray comments = res.getJSONArray("comment");
            if (comments == null || comments.isEmpty())
            {
                break;
            }

            List<CrmWechatMpArticleComment> batch = new ArrayList<>();
            for (int i = 0; i < comments.size(); i++)
            {
                JSONObject item = comments.getJSONObject(i);
                if (item == null)
                {
                    continue;
                }
                CrmWechatMpArticleComment row = mapComment(appId, msgDataId, index, articleId, articleTitle,
                        articleUrl, item);
                if (row != null)
                {
                    batch.add(row);
                }
            }
            if (!batch.isEmpty())
            {
                commentMapper.batchUpsert(batch);
                upserted += batch.size();
            }

            if (comments.size() < PAGE_COUNT)
            {
                break;
            }
            begin += PAGE_COUNT;
        }
        return upserted;
    }

    private CrmWechatMpArticleComment mapComment(String appId, String msgDataId, int index, String articleId,
            String articleTitle, String articleUrl, JSONObject item)
    {
        Long userCommentId = item.getLong("user_comment_id");
        if (userCommentId == null)
        {
            return null;
        }
        String content = item.getString("content");
        if (content == null)
        {
            content = "";
        }
        CrmWechatMpArticleComment row = new CrmWechatMpArticleComment();
        row.setAppId(appId);
        row.setMsgDataId(msgDataId);
        row.setIndexInMsg(index);
        row.setArticleId(articleId);
        row.setArticleTitle(articleTitle);
        row.setArticleUrl(articleUrl);
        row.setUserCommentId(userCommentId);
        row.setOpenid(item.getString("openid"));
        row.setContent(content);
        row.setCommentType(item.getIntValue("comment_type", 0));

        long createTimeSec = item.getLongValue("create_time", 0L);
        if (createTimeSec > 0)
        {
            row.setCommentTime(new Date(createTimeSec * 1000L));
        }
        else
        {
            row.setCommentTime(new Date());
        }

        JSONObject reply = item.getJSONObject("reply");
        if (reply != null)
        {
            // WeChat may nest as reply.content or reply.reply_list[0]
            String replyContent = reply.getString("content");
            long replyTimeSec = reply.getLongValue("create_time", 0L);
            JSONArray replyList = reply.getJSONArray("reply_list");
            if (!StringUtils.hasText(replyContent) && replyList != null && !replyList.isEmpty())
            {
                JSONObject first = replyList.getJSONObject(0);
                if (first != null)
                {
                    replyContent = first.getString("content");
                    replyTimeSec = first.getLongValue("create_time", replyTimeSec);
                }
            }
            if (StringUtils.hasText(replyContent))
            {
                row.setReplyContent(replyContent);
                row.setReplyTime(replyTimeSec > 0 ? new Date(replyTimeSec * 1000L) : new Date());
                row.setStatus("replied");
            }
            else
            {
                row.setStatus("pending");
            }
        }
        else
        {
            row.setStatus("pending");
        }

        if (StringUtils.hasText(row.getOpenid()))
        {
            try
            {
                String contactId = identityService.findContactByIdentity("wechat_mp_openid", row.getOpenid());
                if (StringUtils.hasText(contactId))
                {
                    row.setContactId(contactId);
                }
            }
            catch (Exception e)
            {
                log.debug("resolve contact for openid failed: {}", e.getMessage());
            }
        }
        return row;
    }

    private static Map<String, Object> errorRow(String appId, String msgDataId, Integer index, String message)
    {
        Map<String, Object> m = new HashMap<>();
        m.put("appId", appId);
        m.put("msgDataId", msgDataId);
        m.put("index", index);
        m.put("message", message);
        return m;
    }
}
