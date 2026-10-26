package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;

public interface CrmWechatMpFanMapper
{
    List<CrmWechatMpFan> selectFanList(CrmWechatMpFan query);

    CrmWechatMpFan selectByOpenId(@Param("appId") String appId, @Param("openId") String openId);

    List<CrmWechatMpFan> selectSubscribedByOpenId(@Param("openId") String openId);

    List<CrmWechatMpFan> selectByUnionId(@Param("unionId") String unionId);

    int upsert(CrmWechatMpFan fan);
}
