package com.ruoyi.crm.interceptor;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.support.InboxChannelPathRules;

/**
 * Returns 403 inbox_channel_disabled when agent-facing Inbox APIs hit a disabled channel.
 */
@Component
public class InboxChannelAccessInterceptor implements HandlerInterceptor
{
    @Autowired
    private InboxChannelGateService gateService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception
    {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()))
        {
            return true;
        }
        Optional<InboxChannel> channel = InboxChannelPathRules.resolveGatedChannel(request.getRequestURI());
        if (!channel.isPresent())
        {
            return true;
        }
        String tenant = request.getHeader("tenant");
        if (StringUtils.isBlank(tenant))
        {
            return true;
        }
        if (gateService.isEnabled(tenant, channel.get()))
        {
            return true;
        }
        writeChannelDisabled(response, channel.get());
        return false;
    }

    private static void writeChannelDisabled(HttpServletResponse response, InboxChannel channel)
            throws IOException
    {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        AjaxResult body = new AjaxResult(HttpStatus.FORBIDDEN, "inbox_channel_disabled",
                Collections.singletonMap("channel", channel.getCode()));
        response.getWriter().print(JSON.toJSONString(body));
    }
}
