package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.crm.domain.CrmWhatsappWebhookEvent;
import com.ruoyi.crm.mapper.CrmWhatsappConversationMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.mapper.CrmWhatsappWebhookEventMapper;
import com.ruoyi.crm.service.ICrmWhatsappStatsService;

@Service
public class CrmWhatsappStatsServiceImpl implements ICrmWhatsappStatsService
{
    /** 模板发送后统计回复的窗口：24 小时（写死，与营销文档一致） */
    private static final long TEMPLATE_REPLY_WINDOW_MS = 24L * 60 * 60 * 1000;

    @Autowired
    private CrmWhatsappWebhookEventMapper eventMapper;

    @Autowired
    private CrmWhatsappConversationMapper conversationMapper;

    @Autowired
    private CrmWhatsappMessageMapper messageMapper;

    @Override
    public Map<String, Object> getStats(String tenantId, Long startTime, Long endTime, String phoneNumberId)
    {
        List<CrmWhatsappWebhookEvent> events = eventMapper.selectPage(
                tenantId, null, null, null, phoneNumberId,
                startTime, endTime);

        // --- Funnel ---
        long adClicks = 0;
        java.util.Set<String> leadIdSet = new java.util.HashSet<>();
        java.util.Set<String> chatStartSet = new java.util.HashSet<>();

        long totalMessages = 0;
        long incomingMessages = 0;
        long outgoingMessages = 0;

        for (CrmWhatsappWebhookEvent e : events)
        {
            totalMessages++;
            if (e.getAdCampaignId() != null && !e.getAdCampaignId().isEmpty())
            {
                adClicks++;
            }
            if ("messages".equals(e.getEventType()) && e.getDirection() != null && e.getDirection() == 0)
            {
                if (e.getWaId() != null)
                {
                    chatStartSet.add(e.getWaId());
                }
            }
            if (e.getCrmLeadId() != null && !e.getCrmLeadId().isEmpty())
            {
                leadIdSet.add(e.getCrmLeadId());
            }
            if (e.getDirection() != null && e.getDirection() == 0)
            {
                incomingMessages++;
            }
            else if (e.getDirection() != null && e.getDirection() == 1)
            {
                outgoingMessages++;
            }
        }
        long chatStarts = chatStartSet.size();

        Map<String, Object> funnel = new HashMap<>();
        funnel.put("adClicks", adClicks > 0 ? adClicks : null);
        funnel.put("chatStarts", chatStarts);
        funnel.put("newLeads", leadIdSet.size());
        funnel.put("qualifiedConversations", null);
        funnel.put("appointments", null);
        funnel.put("arrivals", null);
        funnel.put("deals", null);
        funnel.put("dealAmount", null);

        // 时间窗内活跃/新建会话（有消息或 create/last_message 落入窗）
        long totalConversations = conversationMapper.countActiveInWindow(
                tenantId, startTime, endTime, phoneNumberId);

        // 模板运营口径
        long templateSent = messageMapper.countTemplateOutbound(tenantId, startTime, endTime,
                phoneNumberId, Arrays.asList("sent", "delivered", "read"));
        long templateDelivered = messageMapper.countTemplateOutbound(tenantId, startTime, endTime,
                phoneNumberId, Arrays.asList("delivered", "read"));
        long templateRead = messageMapper.countTemplateOutbound(tenantId, startTime, endTime,
                phoneNumberId, Arrays.asList("read"));
        long templateConv = messageMapper.countTemplateConversations(
                tenantId, startTime, endTime, phoneNumberId);
        long templateConvReplied = messageMapper.countTemplateConversationsWithReply(
                tenantId, startTime, endTime, phoneNumberId, TEMPLATE_REPLY_WINDOW_MS);

        Map<String, Object> operational = buildOperational(
                totalConversations, null,
                templateSent, templateDelivered, templateRead,
                templateConv, templateConvReplied,
                totalMessages, incomingMessages, outgoingMessages);

        // --- Daily Stats ---
        List<Map<String, Object>> dailyStats = buildDailyStats(tenantId, startTime, endTime, phoneNumberId);

        Map<String, Object> result = new HashMap<>();
        result.put("funnel", funnel);
        result.put("operational", operational);
        result.put("dailyStats", dailyStats);
        result.put("daily_stats", dailyStats);

        return result;
    }

    private List<Map<String, Object>> buildDailyStats(String tenantId, Long startTime, Long endTime,
                                                      String phoneNumberId)
    {
        List<Map<String, Object>> rows = messageMapper.selectTemplateDailyStats(
                tenantId, startTime, endTime, phoneNumberId, TEMPLATE_REPLY_WINDOW_MS);
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        if (rows == null || rows.isEmpty())
        {
            return dailyStats;
        }
        for (Map<String, Object> row : rows)
        {
            String date = stringVal(row.get("date"));
            long sent = longVal(row.get("sent"));
            long delivered = longVal(row.get("delivered"));
            long read = longVal(row.get("read"));
            long conv = longVal(row.get("template_conv"));
            long replied = longVal(row.get("template_conv_replied"));

            Map<String, Object> dayOperational = buildOperational(
                    null, null, sent, delivered, read, conv, replied, null, null, null);

            Map<String, Object> day = new HashMap<>();
            day.put("date", date);
            day.put("operational", dayOperational);
            Map<String, Object> dayFunnel = new HashMap<>();
            dayFunnel.put("chatStarts", null);
            day.put("funnel", dayFunnel);
            dailyStats.add(day);
        }
        return dailyStats;
    }

    private static Map<String, Object> buildOperational(
            Long totalConversations, Long activeConversations,
            long templateSent, long templateDelivered, long templateRead,
            long templateConv, long templateConvReplied,
            Long totalMessages, Long incomingMessages, Long outgoingMessages)
    {
        Double templateDeliverRate = templateSent > 0
                ? roundRate((double) templateDelivered / templateSent) : null;
        Double templateReadRate = templateDelivered > 0
                ? roundRate((double) templateRead / templateDelivered) : null;
        Double templateReplyRate = templateConv > 0
                ? roundRate((double) templateConvReplied / templateConv) : null;

        Map<String, Object> operational = new HashMap<>();
        if (totalConversations != null)
        {
            operational.put("totalConversations", totalConversations);
        }
        if (activeConversations != null)
        {
            operational.put("activeConversations", activeConversations);
        }
        operational.put("avgResponseTimeSec", null);
        operational.put("firstResponseTimeSec", null);
        operational.put("aiHandledRate", null);
        operational.put("humanTakeoverRate", null);
        operational.put("templateSendCount", templateSent);
        operational.put("templateDeliverRate", templateDeliverRate);
        operational.put("templateReadRate", templateReadRate);
        operational.put("templateReplyRate", templateReplyRate);
        operational.put("replyRate", null);
        operational.put("conversionRate", null);
        if (totalMessages != null)
        {
            operational.put("totalMessages", totalMessages);
        }
        if (incomingMessages != null)
        {
            operational.put("incomingMessages", incomingMessages);
        }
        if (outgoingMessages != null)
        {
            operational.put("outgoingMessages", outgoingMessages);
        }
        return operational;
    }

    private static long longVal(Object o)
    {
        if (o == null)
        {
            return 0L;
        }
        if (o instanceof Number)
        {
            return ((Number) o).longValue();
        }
        try
        {
            return Long.parseLong(String.valueOf(o));
        }
        catch (NumberFormatException e)
        {
            return 0L;
        }
    }

    private static String stringVal(Object o)
    {
        return o == null ? null : String.valueOf(o);
    }

    private static Double roundRate(double rate)
    {
        return Math.round(rate * 10000.0) / 10000.0;
    }
}
