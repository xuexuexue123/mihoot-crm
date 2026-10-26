package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpAutoReply;

public interface CrmWechatMpAutoReplyMapper
{
    List<CrmWechatMpAutoReply> selectList(CrmWechatMpAutoReply query);

    CrmWechatMpAutoReply selectById(@Param("id") Long id);

    int insert(CrmWechatMpAutoReply row);

    int update(CrmWechatMpAutoReply row);

    int deleteById(@Param("id") Long id);

    List<CrmWechatMpAutoReply> selectEnabledByType(@Param("appId") String appId, @Param("ruleType") String ruleType);
}
