package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpCommentAutoReplyRule;

public interface CrmWechatMpCommentAutoReplyRuleMapper
{
    List<CrmWechatMpCommentAutoReplyRule> selectList(CrmWechatMpCommentAutoReplyRule query);

    List<CrmWechatMpCommentAutoReplyRule> selectEnabledOrdered();

    CrmWechatMpCommentAutoReplyRule selectById(@Param("id") Long id);

    int insert(CrmWechatMpCommentAutoReplyRule row);

    int update(CrmWechatMpCommentAutoReplyRule row);

    int deleteById(@Param("id") Long id);
}
