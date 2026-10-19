package com.ruoyi.crm.wechatmp.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleComment;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpCommentAutoReplyRule;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpArticleCommentMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpCommentAutoReplyRuleMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpCommentAutoReplyRuleService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.crm.wechatmp.support.WechatMpCommentApiErrors;

@Service
public class WechatMpCommentAutoReplyRuleServiceImpl implements IWechatMpCommentAutoReplyRuleService
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpCommentAutoReplyRuleServiceImpl.class);

    @Autowired
    private CrmWechatMpCommentAutoReplyRuleMapper ruleMapper;

    @Autowired
    private CrmWechatMpArticleCommentMapper commentMapper;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @Override
    public List<CrmWechatMpCommentAutoReplyRule> selectList(CrmWechatMpCommentAutoReplyRule query)
    {
        return ruleMapper.selectList(query != null ? query : new CrmWechatMpCommentAutoReplyRule());
    }

    @Override
    public CrmWechatMpCommentAutoReplyRule getById(Long id)
    {
        CrmWechatMpCommentAutoReplyRule row = ruleMapper.selectById(id);
        if (row == null)
        {
            throw new ServiceException("规则不存在", HttpStatus.NOT_FOUND);
        }
        return row;
    }

    @Override
    public void add(CrmWechatMpCommentAutoReplyRule row)
    {
        validate(row);
        if (!StringUtils.hasText(row.getMatchMode()))
        {
            row.setMatchMode("contains");
        }
        ruleMapper.insert(row);
    }

    @Override
    public void update(CrmWechatMpCommentAutoReplyRule row)
    {
        if (row.getId() == null)
        {
            throw new ServiceException("id 不能为空", HttpStatus.BAD_REQUEST);
        }
        validate(row);
        getById(row.getId());
        ruleMapper.update(row);
    }

    @Override
    public void delete(Long id)
    {
        getById(id);
        ruleMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> processPending(int limit)
    {
        int lim = limit > 0 ? limit : 50;
        List<CrmWechatMpCommentAutoReplyRule> rules = ruleMapper.selectEnabledOrdered();
        List<CrmWechatMpArticleComment> pending = commentMapper.selectPendingForAutoReply(lim);
        int processed = 0;
        int replied = 0;
        int review = 0;
        int skipped = 0;

        if (rules == null || rules.isEmpty() || pending == null || pending.isEmpty())
        {
            Map<String, Object> empty = new HashMap<>();
            empty.put("processed", 0);
            empty.put("replied", 0);
            empty.put("review", 0);
            empty.put("skipped", 0);
            return empty;
        }

        for (CrmWechatMpArticleComment comment : pending)
        {
            processed++;
            CrmWechatMpCommentAutoReplyRule matched = match(rules, comment);
            if (matched == null)
            {
                skipped++;
                continue;
            }
            if (Boolean.TRUE.equals(matched.getRequireReview()))
            {
                commentMapper.markReviewRequired(comment.getId(), matched.getId());
                review++;
                continue;
            }
            try
            {
                String token = tokenService.getAccessToken(comment.getAppId());
                JSONObject res = tencentApiClient.commentReplyAdd(token, comment.getMsgDataId(),
                        comment.getIndexInMsg() != null ? comment.getIndexInMsg() : 0,
                        comment.getUserCommentId(), matched.getReplyContent());
                if (tencentApiClient.isTokenInvalid(res))
                {
                    token = tokenService.refreshAccessToken(comment.getAppId());
                    res = tencentApiClient.commentReplyAdd(token, comment.getMsgDataId(),
                            comment.getIndexInMsg() != null ? comment.getIndexInMsg() : 0,
                            comment.getUserCommentId(), matched.getReplyContent());
                }
                int code = WechatMpCommentApiErrors.errcode(res);
                if (WechatMpCommentApiErrors.isSuccess(res) || code == 88005)
                {
                    commentMapper.updateReply(comment.getId(), matched.getReplyContent(), new Date(), "replied");
                    replied++;
                }
                else
                {
                    log.warn("auto reply fail commentId={}: {}", comment.getId(), WechatMpCommentApiErrors.message(res));
                    skipped++;
                }
            }
            catch (Exception e)
            {
                log.warn("auto reply error commentId={}: {}", comment.getId(), e.getMessage());
                skipped++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("processed", processed);
        result.put("replied", replied);
        result.put("review", review);
        result.put("skipped", skipped);
        return result;
    }

    private static CrmWechatMpCommentAutoReplyRule match(List<CrmWechatMpCommentAutoReplyRule> rules,
            CrmWechatMpArticleComment comment)
    {
        String content = comment.getContent() != null ? comment.getContent() : "";
        for (CrmWechatMpCommentAutoReplyRule rule : rules)
        {
            if (StringUtils.hasText(rule.getAppId()) && !rule.getAppId().equals(comment.getAppId()))
            {
                continue;
            }
            if (!StringUtils.hasText(rule.getKeyword()))
            {
                continue;
            }
            String mode = rule.getMatchMode() != null ? rule.getMatchMode() : "contains";
            if ("equals".equalsIgnoreCase(mode))
            {
                if (content.equals(rule.getKeyword()))
                {
                    return rule;
                }
            }
            else if (content.contains(rule.getKeyword()))
            {
                return rule;
            }
        }
        return null;
    }

    private static void validate(CrmWechatMpCommentAutoReplyRule row)
    {
        if (row == null || !StringUtils.hasText(row.getKeyword()))
        {
            throw new ServiceException("keyword 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(row.getReplyContent()))
        {
            throw new ServiceException("replyContent 不能为空", HttpStatus.BAD_REQUEST);
        }
    }
}
