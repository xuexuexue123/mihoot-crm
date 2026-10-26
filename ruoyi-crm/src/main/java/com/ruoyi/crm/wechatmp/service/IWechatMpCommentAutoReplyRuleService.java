package com.ruoyi.crm.wechatmp.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpCommentAutoReplyRule;

public interface IWechatMpCommentAutoReplyRuleService
{
    List<CrmWechatMpCommentAutoReplyRule> selectList(CrmWechatMpCommentAutoReplyRule query);

    CrmWechatMpCommentAutoReplyRule getById(Long id);

    void add(CrmWechatMpCommentAutoReplyRule row);

    void update(CrmWechatMpCommentAutoReplyRule row);

    void delete(Long id);

    /** 处理当前租户 pending 留言；返回 processed / replied / review / skipped */
    Map<String, Object> processPending(int limit);
}
