package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.crm.domain.CustomerMetrics;
import com.ruoyi.crm.mapper.CrmActivityMapper;
import com.ruoyi.crm.mapper.CustomerMetricsMapper;
import com.ruoyi.crm.service.ICustomerMetricsService;

import lombok.extern.slf4j.Slf4j;

/**
 * 客户聚合指标 Service 实现
 *
 * @author luohd
 * @date 2026-06-16
 */
@Slf4j
@Service
public class CustomerMetricsServiceImpl implements ICustomerMetricsService
{
    @Autowired
    private CustomerMetricsMapper customerMetricsMapper;

    @Autowired
    private CrmActivityMapper crmActivityMapper;

    @Override
    public CustomerMetrics getMetrics(String contactId)
    {
        return customerMetricsMapper.selectByContactId(contactId);
    }

    @Override
    public void incrementMetric(String contactId, String eventType, String channel, BigDecimal amount)
    {
        if (contactId == null)
        {
            return;
        }
        try
        {
            CustomerMetrics metrics = customerMetricsMapper.selectByContactId(contactId);
            if (metrics == null)
            {
                metrics = new CustomerMetrics();
                metrics.setContactId(contactId);
            }

            updateMetricByEventType(metrics, eventType, channel, amount);

            determineLifecycleStage(metrics);

            customerMetricsMapper.upsert(metrics);
        }
        catch (Exception e)
        {
            log.error("Failed to increment metric for contactId={}, eventType={}", contactId, eventType, e);
        }
    }

    private void updateMetricByEventType(CustomerMetrics m, String eventType, String channel, BigDecimal amount)
    {
        if (eventType == null) return;

        Date now = new Date();

        if ("channel.wecom.added".equals(eventType))
        {
            if (m.getWecomAddedAt() == null) m.setWecomAddedAt(now);
        }
        else if ("channel.wecom.message.received".equals(eventType))
        {
            m.setWecomMessageCount(nullSafeInt(m.getWecomMessageCount()) + 1);
            m.setWecomInboundCount(nullSafeInt(m.getWecomInboundCount()) + 1);
            m.setWecomLastMessageAt(now);
        }
        else if ("channel.wecom.message.sent".equals(eventType))
        {
            m.setWecomMessageCount(nullSafeInt(m.getWecomMessageCount()) + 1);
            m.setWecomOutboundCount(nullSafeInt(m.getWecomOutboundCount()) + 1);
            m.setWecomLastMessageAt(now);
        }
        else if ("channel.wecom.group.joined".equals(eventType))
        {
            m.setWecomGroupCount(nullSafeInt(m.getWecomGroupCount()) + 1);
        }
        else if ("channel.wecom.group.quit".equals(eventType))
        {
            m.setWecomGroupCount(Math.max(0, nullSafeInt(m.getWecomGroupCount()) - 1));
        }
        else if ("channel.wechat_mp.follow".equals(eventType))
        {
            if (m.getWechatMpFollowedAt() == null)
            {
                m.setWechatMpFollowedAt(now);
            }
        }
        else if ("channel.wechat_mp.message.received".equals(eventType))
        {
            m.setTotalEventCount(nullSafeInt(m.getTotalEventCount()) + 1);
            m.setLastEventAt(now);
            return;
        }
        else if ("channel.wechat_mp.message.sent".equals(eventType))
        {
            m.setTotalEventCount(nullSafeInt(m.getTotalEventCount()) + 1);
            m.setLastEventAt(now);
            return;
        }
        else if ("channel.wechat_mp.article.read".equals(eventType))
        {
            m.setWechatMpArticleRead30d(nullSafeInt(m.getWechatMpArticleRead30d()) + 1);
        }
        else if ("lead.created".equals(eventType))
        {
            m.setLeadCount(nullSafeInt(m.getLeadCount()) + 1);
            if (m.getLeadCreatedAt() == null) m.setLeadCreatedAt(now);
        }
        else if ("appointment.created".equals(eventType))
        {
            m.setAppointmentCount(nullSafeInt(m.getAppointmentCount()) + 1);
        }
        else if ("appointment.noshow".equals(eventType))
        {
            m.setAppointmentNoshowCount(nullSafeInt(m.getAppointmentNoshowCount()) + 1);
        }
        else if ("visit.first".equals(eventType) || "visit.return".equals(eventType))
        {
            m.setVisitCount(nullSafeInt(m.getVisitCount()) + 1);
            if (m.getFirstVisitAt() == null) m.setFirstVisitAt(now);
            m.setLastVisitAt(now);
        }
        else if ("opportunity.created".equals(eventType))
        {
            m.setOpportunityCount(nullSafeInt(m.getOpportunityCount()) + 1);
        }
        else if ("opportunity.won".equals(eventType))
        {
            m.setOpportunityWonCount(nullSafeInt(m.getOpportunityWonCount()) + 1);
            if (amount != null)
            {
                BigDecimal currentRevenue = m.getTotalRevenue() != null ? m.getTotalRevenue() : BigDecimal.ZERO;
                BigDecimal newRevenue = currentRevenue.add(amount);
                m.setTotalRevenue(newRevenue);
                m.setLastPaymentAt(now);
                int wonCount = m.getOpportunityWonCount();
                m.setAvgDealAmount(newRevenue.divide(new BigDecimal(wonCount), 2, RoundingMode.HALF_UP));
            }
        }
        else if ("ai.scored".equals(eventType))
        {
            m.setAiScoreUpdatedAt(now);
        }
        else if ("ai.tag_predicted".equals(eventType))
        {
            m.setAiTagCount(nullSafeInt(m.getAiTagCount()) + 1);
        }

        m.setTotalEventCount(nullSafeInt(m.getTotalEventCount()) + 1);
        m.setLastEventAt(now);
    }

    @Override
    public void recomputeAll()
    {
        log.info("CustomerMetrics full recompute started");

        // 1. 单条聚合 SQL 获取所有 Contact 的完整指标
        List<CustomerMetrics> allMetrics = crmActivityMapper.selectMetricsAggregate();
        if (allMetrics == null || allMetrics.isEmpty())
        {
            log.info("No activity events found for recompute, skipped");
            return;
        }

        // 2. Java 层计算 lifecycle_stage 和 churn_risk_score（状态机逻辑）
        for (CustomerMetrics m : allMetrics)
        {
            m.setComputedAt(new Date());
            determineLifecycleStage(m);
        }

        // 3. 分批覆写式 upsert（每批 500 条）
        int batchSize = 500;
        List<CustomerMetrics> batch = new ArrayList<>(batchSize);
        int processedCount = 0;

        for (CustomerMetrics m : allMetrics)
        {
            batch.add(m);
            if (batch.size() >= batchSize)
            {
                customerMetricsMapper.batchUpsertOverwrite(batch);
                processedCount += batch.size();
                batch.clear();
            }
        }

        if (!batch.isEmpty())
        {
            customerMetricsMapper.batchUpsertOverwrite(batch);
            processedCount += batch.size();
        }

        // 4. 分批标记 crm_activity 为已聚合
        List<String> allSubjectIds = new ArrayList<>(allMetrics.size());
        for (CustomerMetrics m : allMetrics)
        {
            allSubjectIds.add(m.getContactId());
        }
        int markBatchSize = 500;
        for (int i = 0; i < allSubjectIds.size(); i += markBatchSize)
        {
            int end = Math.min(i + markBatchSize, allSubjectIds.size());
            crmActivityMapper.markAsAggregated(allSubjectIds.subList(i, end));
        }

        log.info("CustomerMetrics full recompute completed, processed {} contacts", processedCount);
    }

    private void determineLifecycleStage(CustomerMetrics m)
    {
        boolean hasOpportunityWon = m.getOpportunityWonCount() != null && m.getOpportunityWonCount() > 0;
        boolean hasPayment = m.getTotalRevenue() != null && m.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0;
        boolean hasOpportunity = m.getOpportunityCount() != null && m.getOpportunityCount() > 0;
        boolean hasLeadConverted = m.getLeadConverted() != null && m.getLeadConverted();

        if (hasPayment || hasOpportunityWon)
        {
            m.setLifecycleStage("customer");
        }
        else if (hasOpportunity || hasLeadConverted)
        {
            m.setLifecycleStage("opportunity");
        }
        else
        {
            m.setLifecycleStage("lead");
        }

        if ("customer".equals(m.getLifecycleStage()))
        {
            Date lastEvent = m.getLastEventAt();
            if (lastEvent != null)
            {
                long daysSinceLastEvent = (System.currentTimeMillis() - lastEvent.getTime()) / (24 * 60 * 60 * 1000L);
                if (daysSinceLastEvent > 90)
                {
                    m.setLifecycleStage("churned");
                    m.setChurnRiskScore(daysSinceLastEvent > 180 ? 90 : 70);
                }
                else if (daysSinceLastEvent > 60)
                {
                    m.setChurnRiskScore(50);
                }
                else if (daysSinceLastEvent > 30)
                {
                    m.setChurnRiskScore(30);
                }
                else
                {
                    m.setChurnRiskScore(10);
                }
            }
        }

        m.setLifecycleStageAt(new Date());
    }

    private int nullSafeInt(Integer val)
    {
        return val == null ? 0 : val;
    }
}
