package com.ruoyi.crm.wechatmp.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.wechatmp.dto.WechatMpMenuDraftRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMenuPublishRequest;

public interface IWechatMpMenuService
{
    List<Map<String, Object>> listTemplates();

    Map<String, Object> getMenuState(String appId);

    void saveDraft(WechatMpMenuDraftRequest req);

    void publish(WechatMpMenuPublishRequest req);
}
