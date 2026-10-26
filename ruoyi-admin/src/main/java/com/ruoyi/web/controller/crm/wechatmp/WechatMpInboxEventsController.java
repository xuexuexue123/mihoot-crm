package com.ruoyi.web.controller.crm.wechatmp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.realtime.WechatMpInboxSseHub;

/**
 * WeChat MP Inbox SSE stream (multi-instance via Redis pub/sub).
 */
@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpInboxEventsController extends BaseController
{
    @Autowired
    private WechatMpInboxSseHub sseHub;

    @Autowired
    private CrmWechatMpConfigMapper configMapper;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:conversations')")
    @GetMapping(value = "/inbox/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (!StringUtils.hasText(tenant))
        {
            throw new ServiceException("Missing tenant header", 400);
        }
        List<String> appIds = new ArrayList<>();
        List<CrmWechatMpConfig> configs = configMapper.selectAll();
        if (configs != null)
        {
            for (CrmWechatMpConfig cfg : configs)
            {
                if (cfg != null && StringUtils.hasText(cfg.getAppId()))
                {
                    appIds.add(cfg.getAppId());
                }
            }
        }
        return sseHub.register(tenant.trim(), appIds);
    }
}
