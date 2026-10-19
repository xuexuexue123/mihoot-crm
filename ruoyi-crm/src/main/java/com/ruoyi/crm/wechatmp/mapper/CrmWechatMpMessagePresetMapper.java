package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;

public interface CrmWechatMpMessagePresetMapper
{
    List<CrmWechatMpMessagePreset> selectList(CrmWechatMpMessagePreset query);

    CrmWechatMpMessagePreset selectById(@Param("id") Long id);

    int insert(CrmWechatMpMessagePreset row);

    int update(CrmWechatMpMessagePreset row);

    int deleteById(@Param("id") Long id);
}
