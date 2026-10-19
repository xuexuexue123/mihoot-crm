package com.ruoyi.crm.wechatmp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleComment;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.dto.WechatMpArticleCommentQuery;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpArticleCommentMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpArticleCommentService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.crm.wechatmp.support.WechatMpCommentApiErrors;

@Service
public class WechatMpArticleCommentServiceImpl implements IWechatMpArticleCommentService
{
    @Autowired
    private CrmWechatMpArticleCommentMapper commentMapper;

    @Autowired
    private CrmWechatMpConfigMapper configMapper;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Override
    public List<CrmWechatMpArticleComment> selectList(WechatMpArticleCommentQuery query, int pageNum, int pageSize)
    {
        resolveAppFilter(query);
        int pn = pageNum > 0 ? pageNum : 1;
        int ps = pageSize > 0 ? pageSize : 50;
        PageHelper.startPage(pn, ps);
        return commentMapper.selectList(query);
    }

    @Override
    public CrmWechatMpArticleComment selectById(Long id)
    {
        if (id == null)
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpArticleComment row = commentMapper.selectById(id);
        if (row == null)
        {
            throw new ServiceException("留言不存在", HttpStatus.NOT_FOUND);
        }
        assertAppVisible(row.getAppId());
        return row;
    }

    @Override
    public Map<String, Object> toApiMap(CrmWechatMpArticleComment row)
    {
        Map<String, Object> m = new HashMap<>();
        if (row == null)
        {
            return m;
        }
        m.put("id", row.getId());
        m.put("appId", row.getAppId());
        m.put("accountName", row.getAccountName());
        m.put("msgDataId", row.getMsgDataId());
        m.put("indexInMsg", row.getIndexInMsg());
        m.put("articleId", row.getArticleId());
        m.put("articleTitle", row.getArticleTitle());
        m.put("articleUrl", row.getArticleUrl());
        m.put("userCommentId", row.getUserCommentId());
        m.put("openid", row.getOpenid());
        m.put("content", row.getContent());
        m.put("commentType", row.getCommentType());
        m.put("commentTime", row.getCommentTime());
        m.put("replyContent", row.getReplyContent());
        m.put("replyTime", row.getReplyTime());
        m.put("status", row.getStatus());
        m.put("isRead", row.getIsRead());
        m.put("contactId", row.getContactId());
        m.put("contactName", row.getContactName());
        m.put("nickname", row.getNickname());
        m.put("avatarUrl", row.getAvatarUrl());
        m.put("leadId", row.getLeadId());
        m.put("reviewRequired", row.getReviewRequired());
        m.put("autoReplyRuleId", row.getAutoReplyRuleId());
        return m;
    }

    @Override
    public void reply(Long id, String content)
    {
        if (!StringUtils.hasText(content))
        {
            throw new ServiceException("回复内容不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpArticleComment row = selectById(id);
        if ("replied".equals(row.getStatus()) && StringUtils.hasText(row.getReplyContent()))
        {
            throw new ServiceException("该留言已回复，无法再次回复", HttpStatus.CONFLICT);
        }
        JSONObject res = callWithTokenRetry(row.getAppId(),
                token -> tencentApiClient.commentReplyAdd(token, row.getMsgDataId(),
                        row.getIndexInMsg() != null ? row.getIndexInMsg() : 0,
                        row.getUserCommentId(), content.trim()));
        if (!WechatMpCommentApiErrors.isSuccess(res))
        {
            throw WechatMpCommentApiErrors.toException(res);
        }
        commentMapper.updateReply(id, content.trim(), new Date(), "replied");
    }

    @Override
    public void openComment(String appId, String msgDataId, Integer index)
    {
        assertAppVisible(appId);
        requireMsgDataId(msgDataId);
        int idx = index != null ? index : 0;
        JSONObject res = callWithTokenRetry(appId.trim(),
                token -> tencentApiClient.commentOpen(token, msgDataId.trim(), idx));
        if (!WechatMpCommentApiErrors.isSuccess(res))
        {
            throw WechatMpCommentApiErrors.toException(res);
        }
    }

    @Override
    public void closeComment(String appId, String msgDataId, Integer index)
    {
        assertAppVisible(appId);
        requireMsgDataId(msgDataId);
        int idx = index != null ? index : 0;
        JSONObject res = callWithTokenRetry(appId.trim(),
                token -> tencentApiClient.commentClose(token, msgDataId.trim(), idx));
        if (!WechatMpCommentApiErrors.isSuccess(res))
        {
            throw WechatMpCommentApiErrors.toException(res);
        }
    }

    @Override
    public void markelect(Long id)
    {
        CrmWechatMpArticleComment row = selectById(id);
        JSONObject res = callWithTokenRetry(row.getAppId(),
                token -> tencentApiClient.commentMarkelect(token, row.getMsgDataId(),
                        row.getIndexInMsg() != null ? row.getIndexInMsg() : 0, row.getUserCommentId()));
        if (!WechatMpCommentApiErrors.isSuccess(res))
        {
            throw WechatMpCommentApiErrors.toException(res);
        }
        commentMapper.updateCommentType(id, 1);
    }

    @Override
    public void unmarkelect(Long id)
    {
        CrmWechatMpArticleComment row = selectById(id);
        JSONObject res = callWithTokenRetry(row.getAppId(),
                token -> tencentApiClient.commentUnmarkelect(token, row.getMsgDataId(),
                        row.getIndexInMsg() != null ? row.getIndexInMsg() : 0, row.getUserCommentId()));
        if (!WechatMpCommentApiErrors.isSuccess(res))
        {
            throw WechatMpCommentApiErrors.toException(res);
        }
        commentMapper.updateCommentType(id, 0);
    }

    @Override
    public void deleteComment(Long id)
    {
        CrmWechatMpArticleComment row = selectById(id);
        JSONObject res = callWithTokenRetry(row.getAppId(),
                token -> tencentApiClient.commentDelete(token, row.getMsgDataId(),
                        row.getIndexInMsg() != null ? row.getIndexInMsg() : 0, row.getUserCommentId()));
        if (!WechatMpCommentApiErrors.isSuccess(res))
        {
            throw WechatMpCommentApiErrors.toException(res);
        }
        commentMapper.deleteById(id);
    }

    @Override
    public void deleteReply(Long id)
    {
        CrmWechatMpArticleComment row = selectById(id);
        JSONObject res = callWithTokenRetry(row.getAppId(),
                token -> tencentApiClient.commentReplyDelete(token, row.getMsgDataId(),
                        row.getIndexInMsg() != null ? row.getIndexInMsg() : 0, row.getUserCommentId()));
        if (!WechatMpCommentApiErrors.isSuccess(res))
        {
            throw WechatMpCommentApiErrors.toException(res);
        }
        commentMapper.clearReply(id);
    }

    @Override
    public void markRead(Long id)
    {
        selectById(id);
        commentMapper.markRead(id);
    }

    @Override
    public Map<String, Object> patchCrmLinks(Long id, boolean touchContact, String contactId,
            boolean touchLead, String leadId)
    {
        if (!touchContact && !touchLead)
        {
            throw new ServiceException("至少提供 contactId 或 leadId", HttpStatus.BAD_REQUEST);
        }
        selectById(id);
        String normalizedContact = normalizeLinkValue(contactId);
        String normalizedLead = normalizeLinkValue(leadId);
        commentMapper.updateCrmLinks(id, touchContact, normalizedContact, touchLead, normalizedLead);
        return toApiMap(selectById(id));
    }

    /** 空串视为清空（null） */
    private static String normalizeLinkValue(String value)
    {
        if (!StringUtils.hasText(value))
        {
            return null;
        }
        return value.trim();
    }

    private void resolveAppFilter(WechatMpArticleCommentQuery query)
    {
        if (query == null)
        {
            return;
        }
        if (StringUtils.hasText(query.getAppId()))
        {
            assertAppVisible(query.getAppId());
            query.setAppId(query.getAppId().trim());
            query.setAppIds(null);
            return;
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
        if (appIds.isEmpty())
        {
            appIds.add("__none__");
        }
        query.setAppIds(appIds);
    }

    private void assertAppVisible(String appId)
    {
        if (!StringUtils.hasText(appId))
        {
            throw new ServiceException("appId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpConfig cfg = configMapper.selectByAppId(appId.trim());
        if (cfg == null)
        {
            throw new ServiceException("公众号配置不存在或不属于当前租户", HttpStatus.FORBIDDEN);
        }
    }

    private static void requireMsgDataId(String msgDataId)
    {
        if (!StringUtils.hasText(msgDataId))
        {
            throw new ServiceException("msgDataId 不能为空", HttpStatus.BAD_REQUEST);
        }
    }

    @FunctionalInterface
    private interface TokenCall
    {
        JSONObject apply(String token);
    }

    private JSONObject callWithTokenRetry(String appId, TokenCall call)
    {
        String token = tokenService.getAccessToken(appId);
        JSONObject res = call.apply(token);
        if (tencentApiClient.isTokenInvalid(res))
        {
            token = tokenService.refreshAccessToken(appId);
            res = call.apply(token);
        }
        return res;
    }
}
