package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMenu;

public interface CrmWechatMpMenuMapper
{
    CrmWechatMpMenu selectDraftByAppId(@Param("appId") String appId);

    CrmWechatMpMenu selectLatestPublished(@Param("appId") String appId);

    CrmWechatMpMenu selectById(@Param("id") Long id);

    int insert(CrmWechatMpMenu row);

    int update(CrmWechatMpMenu row);

    int deleteDraftByAppId(@Param("appId") String appId);

    int markPublished(@Param("id") Long id, @Param("publishedBy") String publishedBy);

    List<CrmWechatMpMenu> selectHistory(@Param("appId") String appId);
}
