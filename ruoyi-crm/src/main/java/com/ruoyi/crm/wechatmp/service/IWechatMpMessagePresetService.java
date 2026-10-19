package com.ruoyi.crm.wechatmp.service;

import java.util.List;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderResult;

public interface IWechatMpMessagePresetService
{
    List<CrmWechatMpMessagePreset> selectList(CrmWechatMpMessagePreset query);

    CrmWechatMpMessagePreset getById(Long id);

    void add(CrmWechatMpMessagePreset row);

    CrmWechatMpMessagePreset update(Long id, CrmWechatMpMessagePreset row);

    void delete(Long id);

    WechatMpMessagePresetRenderResult render(Long id, WechatMpMessagePresetRenderRequest request);
}
