package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;

public interface CrmWechatMpMessageMapper
{
    int insert(CrmWechatMpMessage msg);

    List<CrmWechatMpMessage> selectByConversation(@Param("appId") String appId, @Param("openId") String openId);

    List<Map<String, Object>> selectConversations(@Param("appId") String appId);

    CrmWechatMpMessage selectLastInbound(@Param("appId") String appId, @Param("openId") String openId);
}
