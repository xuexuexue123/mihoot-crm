package com.ruoyi.crm.wechatmp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpQrcode;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

/**
 * 回调副作用：消息入库、扫码归因、自动回复触发
 */
@Component
public class WechatMpCallbackSideEffects
{
    @Autowired
    private IWechatMpQrcodeService qrcodeService;

    public void enrichQrcodeAttribution(String appId, WxMpXmlMessage wxMessage, ActivityEventDTO dto)
    {
        if (dto == null || wxMessage == null)
        {
            return;
        }
        String scene = resolveScene(wxMessage);
        if (scene == null)
        {
            return;
        }
        CrmWechatMpQrcode qr = qrcodeService.findByScene(appId, scene);
        if (qr == null)
        {
            return;
        }
        JSONObject ctx = dto.getContext() != null
                ? JSONObject.parseObject(dto.getContext()) : new JSONObject();
        ctx.put("scene_str", scene);
        ctx.put("qr_code_id", qr.getId());
        if (qr.getCrmEventId() != null)
        {
            ctx.put("crm_event_id", qr.getCrmEventId());
        }
        dto.setContext(ctx.toString());
    }

    private String resolveScene(WxMpXmlMessage msg)
    {
        if (!"event".equals(msg.getMsgType()))
        {
            return null;
        }
        String eventKey = msg.getEventKey();
        if (eventKey == null)
        {
            return null;
        }
        if ("subscribe".equals(msg.getEvent()) && eventKey.startsWith("qrscene_"))
        {
            return eventKey.substring("qrscene_".length());
        }
        if ("SCAN".equals(msg.getEvent()))
        {
            return eventKey;
        }
        return null;
    }
}
