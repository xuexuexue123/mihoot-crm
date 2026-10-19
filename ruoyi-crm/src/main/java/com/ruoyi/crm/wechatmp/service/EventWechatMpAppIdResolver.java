package com.ruoyi.crm.wechatmp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpQrcodeMapper;

/**
 * 按活动解析默认微信公众号 AppID（统一入口，禁止各处硬编码「第一个已连接号」）。
 */
@Service
public class EventWechatMpAppIdResolver
{
    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private ICrmWechatMpConfigService wechatMpConfigService;

    @Autowired
    private CrmWechatMpQrcodeMapper wechatMpQrcodeMapper;

    public String resolve(String eventId)
    {
        if (StringUtils.isBlank(eventId))
        {
            return null;
        }
        return resolve(crmEventMapper.selectCrmEventByEventId(eventId));
    }

    /**
     * 使用内存中的活动对象解析（写入校验时活动可能尚未持久化）。
     */
    public String resolve(CrmEvent event)
    {
        if (event == null || StringUtils.isBlank(event.getEventId()))
        {
            return null;
        }

        if (StringUtils.isNotBlank(event.getWechatMpAppId())
                && wechatMpConfigService.isConnected(event.getWechatMpAppId()))
        {
            return event.getWechatMpAppId();
        }

        return resolveWithoutEventField(event.getEventId());
    }

    private String resolveWithoutEventField(String eventId)
    {
        List<String> connected = wechatMpConfigService.listConnectedAppIds();
        if (connected.size() == 1)
        {
            return connected.get(0);
        }

        List<String> fromQrcodes = wechatMpQrcodeMapper.selectDistinctAppIdsByEventId(eventId);
        Set<String> distinct = new HashSet<>();
        if (fromQrcodes != null)
        {
            for (String appId : fromQrcodes)
            {
                if (StringUtils.isNotBlank(appId))
                {
                    distinct.add(appId);
                }
            }
        }
        if (distinct.size() == 1)
        {
            return distinct.iterator().next();
        }

        return null;
    }
}
