package com.ruoyi.web.controller.crm;

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

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomKfConfig;
import com.ruoyi.crm.mapper.CrmWecomKfConfigMapper;
import com.ruoyi.crm.wecom.realtime.WecomKfInboxSseHub;

/**
 * WeCom KF Inbox SSE stream (multi-instance via Redis pub/sub).
 */
@RestController
@RequestMapping("/crm/wecom/kf")
public class CrmWecomKfInboxEventsController extends BaseController
{
    @Autowired
    private WecomKfInboxSseHub sseHub;

    @Autowired
    private CrmWecomKfConfigMapper kfConfigMapper;

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations')")
    @GetMapping(value = "/inbox/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (!StringUtils.hasText(tenant))
        {
            throw new ServiceException("Missing tenant header", 400);
        }
        return sseHub.register(tenant.trim(), resolveVisibleOpenKfids());
    }

    private List<String> resolveVisibleOpenKfids()
    {
        List<String> openKfids = new ArrayList<>();
        CrmWecomKfConfig kfCfg = kfConfigMapper.selectFirst();
        if (kfCfg == null || !StringUtils.hasText(kfCfg.getOpenKfids()))
        {
            return openKfids;
        }
        try
        {
            List<String> parsed = JSON.parseArray(kfCfg.getOpenKfids(), String.class);
            if (parsed != null)
            {
                for (String s : parsed)
                {
                    if (StringUtils.hasText(s))
                    {
                        openKfids.add(s.trim());
                    }
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return openKfids;
    }
}
