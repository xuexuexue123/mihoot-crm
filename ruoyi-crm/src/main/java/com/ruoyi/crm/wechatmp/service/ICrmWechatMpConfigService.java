package com.ruoyi.crm.wechatmp.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.dto.WechatMpConfigSaveRequest;

public interface ICrmWechatMpConfigService
{
    List<Map<String, Object>> listConfigs();

    Map<String, Object> getConfigMasked(Long id);

    void saveOrUpdate(WechatMpConfigSaveRequest body, String tenantId);

    void delete(Long id, String tenantId);

    boolean isConnected(String appId);

    List<String> listConnectedAppIds();
}
