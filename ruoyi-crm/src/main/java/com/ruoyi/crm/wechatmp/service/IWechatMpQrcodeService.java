package com.ruoyi.crm.wechatmp.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpQrcode;
import com.ruoyi.crm.wechatmp.dto.WechatMpQrcodeCreateRequest;

public interface IWechatMpQrcodeService
{
    List<CrmWechatMpQrcode> selectList(CrmWechatMpQrcode query);

    CrmWechatMpQrcode getById(Long id);

    CrmWechatMpQrcode create(WechatMpQrcodeCreateRequest req);

    void update(Long id, CrmWechatMpQrcode patch);

    void delete(Long id);

    Map<String, Object> getStats(Long id);

    CrmWechatMpQrcode findByScene(String appId, String sceneStr);
}
