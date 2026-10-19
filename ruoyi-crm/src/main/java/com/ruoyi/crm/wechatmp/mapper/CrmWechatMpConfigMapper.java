package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;

public interface CrmWechatMpConfigMapper
{
    CrmWechatMpConfig selectByAppId(@Param("appId") String appId);

    CrmWechatMpConfig selectById(@Param("id") Long id);

    List<CrmWechatMpConfig> selectAll();

    int insert(CrmWechatMpConfig row);

    int update(CrmWechatMpConfig row);

    int deleteById(@Param("id") Long id);
}
