package com.ruoyi.web.controller.crm;

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
import com.ruoyi.crm.service.ICrmWhatsappStatsService;

@RestController
@RequestMapping("/crm/whatsapp")
public class CrmWhatsappStatsController extends BaseController
{
    @Autowired
    private ICrmWhatsappStatsService statsService;

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:stats') or @ss.hasPermi('crm:whatsapp:marketing')")
    @PostMapping("/stats")
    public AjaxResult getStats(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        Long startTime = longField(body, "startTime", "start_time");
        Long endTime = longField(body, "endTime", "end_time");
        String phoneNumberId = stringField(body, "phoneNumberId", "phone_number_id");

        Map<String, Object> stats = statsService.getStats(tenant, startTime, endTime, phoneNumberId);
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

    private static String stringField(Map<String, Object> body, String camel, String snake)
    {
        Object v = body.get(camel);
        if (v == null)
        {
            v = body.get(snake);
        }
        return v != null ? String.valueOf(v) : null;
    }
}
