package com.ruoyi.web.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.mapper.CrmMessengerConversationMapper;
import com.ruoyi.crm.mapper.CrmMessengerMessageMapper;

@RestController
@RequestMapping("/crm/messenger")
public class CrmMessengerStatsController extends BaseController
{
    @Autowired
    private CrmMessengerConversationMapper conversationMapper;

    @Autowired
    private CrmMessengerMessageMapper messageMapper;

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations')")
    @PostMapping("/stats")
    public AjaxResult getStats(@RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        if (body == null)
        {
            body = new HashMap<>();
        }
        Long startTime = longField(body, "startTime", "start_time");
        Long endTime = longField(body, "endTime", "end_time");

        Map<String, Object> stats = new HashMap<>();
        try
        {
            stats.put("conversationCount", conversationMapper.countActive(tenant, startTime, endTime));
            stats.put("inboundMessageCount", messageMapper.countByDirection(tenant, 0, startTime, endTime));
            stats.put("outboundMessageCount", messageMapper.countByDirection(tenant, 1, startTime, endTime));
        }
        catch (Exception e)
        {
            // P0 骨架：表未建或查询失败时返回零值占位
            stats.put("conversationCount", 0L);
            stats.put("inboundMessageCount", 0L);
            stats.put("outboundMessageCount", 0L);
        }
        return AjaxResult.success(stats);
    }

    private static Long longField(Map<String, Object> body, String camel, String snake)
    {
        Object v = body.get(camel);
        if (v == null)
        {
            v = body.get(snake);
        }
        if (v instanceof Number)
        {
            return ((Number) v).longValue();
        }
        return null;
    }
}
