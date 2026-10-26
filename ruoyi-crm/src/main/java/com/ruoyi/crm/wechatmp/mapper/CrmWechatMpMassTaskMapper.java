package com.ruoyi.crm.wechatmp.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMassTask;

public interface CrmWechatMpMassTaskMapper
{
    int insert(CrmWechatMpMassTask task);

    int update(CrmWechatMpMassTask task);

    CrmWechatMpMassTask selectById(@Param("id") Long id);

    CrmWechatMpMassTask selectByMsgId(@Param("appId") String appId, @Param("msgId") String msgId);

    CrmWechatMpMassTask selectByClientMsgId(@Param("appId") String appId, @Param("clientMsgId") String clientMsgId);

    List<CrmWechatMpMassTask> selectList(CrmWechatMpMassTask query);

    int countIsToAllInPeriod(@Param("appId") String appId,
            @Param("periodStart") Date periodStart,
            @Param("periodEnd") Date periodEnd);
}
