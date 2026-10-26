package com.ruoyi.crm.service.support;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContactServiceSummary;
import com.ruoyi.crm.domain.CrmContactServiceSummaryRevision;
import com.ruoyi.crm.domain.dto.ServiceSummaryDeltaItem;
import com.ruoyi.crm.domain.vo.ContactServiceSummaryVo;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmContactServiceSummaryMapper;
import com.ruoyi.crm.mapper.CrmContactServiceSummaryRevisionMapper;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * Contact 服务摘要 Worker：bootstrap / refresh / incremental。
 */
@Component
public class ContactServiceSummaryWorker
{
    private static final Logger log = LoggerFactory.getLogger(ContactServiceSummaryWorker.class);

    public static final String STATUS_READY = "ready";
    public static final String STATUS_STALE = "stale";
    public static final String STATUS_GENERATING = "generating";
    public static final String STATUS_FAILED = "failed";

    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    @Autowired
    private CrmContactServiceSummaryMapper summaryMapper;

    @Autowired
    private CrmContactServiceSummaryRevisionMapper revisionMapper;

    @Autowired
    private ContactServiceSummaryDeltaLoader deltaLoader;

    @Autowired
    private ContactServiceSummaryLlmClient llmClient;

    @Autowired(required = false)
    private ActivityEventProducer activityEventProducer;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2, r -> {
        Thread t = new Thread(r, "contact-service-summary-worker");
        t.setDaemon(true);
        return t;
    });

    @PreDestroy
    public void shutdown()
    {
        executor.shutdownNow();
    }

    public void runAsync(String tenantId, String contactId, String trigger, boolean forceFull)
    {
        final String dsKey = tenantId;
        executor.execute(() -> {
            String previous = null;
            try
            {
                previous = DynamicDataSourceContextHolder.getDataSourceKey();
                if (StringUtils.isNotEmpty(dsKey))
                {
                    DynamicDataSourceContextHolder.setDataSourceKey(dsKey);
                }
                run(tenantId, contactId, trigger, forceFull);
            }
            catch (Exception e)
            {
                log.error("Summary worker failed contactId={} trigger={}", contactId, trigger, e);
            }
            finally
            {
                if (StringUtils.isNotEmpty(previous))
                {
                    DynamicDataSourceContextHolder.setDataSourceKey(previous);
                }
                else
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
    }

    public CrmContactServiceSummary run(String tenantId, String contactId, String trigger, boolean forceFull)
    {
        String lockKey = tenantId + ":" + contactId;
        Object lock = locks.computeIfAbsent(lockKey, k -> new Object());
        synchronized (lock)
        {
            return doRun(tenantId, contactId, trigger, forceFull);
        }
    }

    private CrmContactServiceSummary doRun(String tenantId, String contactId, String trigger, boolean forceFull)
    {
        CrmContactServiceSummary existing = summaryMapper.selectByContactId(tenantId, contactId);
        boolean firstInsert = existing == null;
        if (existing == null)
        {
            existing = newEmpty(tenantId, contactId);
            existing.setStatus(STATUS_GENERATING);
            summaryMapper.insert(existing);
        }
        else
        {
            summaryMapper.updateStatus(tenantId, contactId, STATUS_GENERATING, null);
            existing.setStatus(STATUS_GENERATING);
        }

        ContactServiceSummaryVo.SummarySections previous = toSections(existing);
        List<String> locked = ContactServiceSummaryDeltaLoader.parseStringList(existing.getLockedFields());

        boolean bootstrap = forceFull
                || "bootstrap".equals(trigger)
                || "convert".equals(trigger)
                || "link".equals(trigger)
                || (StringUtils.isEmpty(existing.getCurrentNeed())
                        && StringUtils.isEmpty(existing.getConfirmedFacts()));

        List<ServiceSummaryDeltaItem> deltas;
        if (bootstrap)
        {
            deltas = deltaLoader.loadBootstrap(contactId, ContactServiceSummaryDeltaLoader.DEFAULT_LIMIT);
        }
        else
        {
            deltas = deltaLoader.loadIncremental(contactId, existing.getLastEventId(),
                    ContactServiceSummaryDeltaLoader.DEFAULT_LIMIT);
        }

        try
        {
            ContactServiceSummaryLlmClient.LlmResult llm = llmClient.rewrite(tenantId, previous, locked, deltas,
                    bootstrap);
            applySections(existing, llm.getSections());
            existing.setModel(llm.getModel());
            existing.setStatus(STATUS_READY);
            existing.setErrorMessage(null);

            long nextVersion;
            if (firstInsert)
            {
                nextVersion = 1L;
            }
            else
            {
                nextVersion = (existing.getVersion() == null ? 0L : existing.getVersion()) + 1L;
            }
            existing.setVersion(nextVersion);

            Set<String> channels = deltaLoader.collectChannels(deltas);
            existing.setSourceChannels(JSON.toJSONString(channels));
            if (deltas != null && !deltas.isEmpty())
            {
                ServiceSummaryDeltaItem last = deltas.get(deltas.size() - 1);
                existing.setLastEventId(last.getEventId());
                existing.setLastEventAt(last.getAt());
                if (existing.getLastTouchAt() == null && last.getAt() != null)
                {
                    existing.setLastTouchAt(last.getAt());
                }
                if (StringUtils.isEmpty(existing.getLastTouchChannel()))
                {
                    existing.setLastTouchChannel(last.getChannel());
                }
                if (StringUtils.isEmpty(existing.getLastTouchSummary())
                        && StringUtils.isNotEmpty(last.getText()))
                {
                    existing.setLastTouchSummary(truncate(last.getText(), 200));
                }
            }
            if ("human".equals(existing.getGeneratedBy()) || "ai_human".equals(existing.getGeneratedBy()))
            {
                existing.setGeneratedBy("ai_human");
            }
            else
            {
                existing.setGeneratedBy("ai");
            }
            existing.setUpdatedAt(new Date());
            summaryMapper.update(existing);
            insertRevision(existing, trigger);
            if (activityEventProducer != null)
            {
                activityEventProducer.produceAiSummaryGenerated(contactId, existing, trigger, channels);
            }
            return existing;
        }
        catch (Exception e)
        {
            log.warn("Summary generation failed contactId={}: {}", contactId, e.getMessage());
            String msg = e.getMessage();
            if (msg != null && msg.length() > 500)
            {
                msg = msg.substring(0, 500);
            }
            summaryMapper.updateStatus(tenantId, contactId, STATUS_FAILED, msg);
            // reload to keep previous five sections
            CrmContactServiceSummary reloaded = summaryMapper.selectByContactId(tenantId, contactId);
            if (reloaded != null)
            {
                reloaded.setStatus(STATUS_FAILED);
                reloaded.setErrorMessage(msg);
                return reloaded;
            }
            existing.setStatus(STATUS_FAILED);
            existing.setErrorMessage(msg);
            return existing;
        }
    }

    private void insertRevision(CrmContactServiceSummary row, String trigger)
    {
        CrmContactServiceSummaryRevision rev = new CrmContactServiceSummaryRevision();
        rev.setTenantId(row.getTenantId());
        rev.setContactId(row.getContactId());
        rev.setVersion(row.getVersion());
        rev.setTrigger(trigger != null ? trigger : "refresh");
        rev.setSourceChannels(row.getSourceChannels());
        rev.setModel(row.getModel());
        rev.setGeneratedBy(row.getGeneratedBy());
        rev.setCreatedBy("system");
        rev.setCreatedAt(new Date());
        JSONObject snap = new JSONObject();
        snap.put("currentNeed", row.getCurrentNeed());
        snap.put("confirmedFacts", row.getConfirmedFacts());
        snap.put("commitmentsTodos", row.getCommitmentsTodos());
        snap.put("risksSentiment", row.getRisksSentiment());
        JSONObject lt = new JSONObject();
        lt.put("at", row.getLastTouchAt());
        lt.put("channel", row.getLastTouchChannel());
        lt.put("summary", row.getLastTouchSummary());
        snap.put("lastTouch", lt);
        rev.setSnapshot(snap.toJSONString());
        revisionMapper.insert(rev);
    }

    private static CrmContactServiceSummary newEmpty(String tenantId, String contactId)
    {
        CrmContactServiceSummary row = new CrmContactServiceSummary();
        row.setTenantId(tenantId);
        row.setContactId(contactId);
        row.setVersion(1L);
        row.setStatus(STATUS_GENERATING);
        row.setGeneratedBy("ai");
        Date now = new Date();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);
        return row;
    }

    public static ContactServiceSummaryVo.SummarySections toSections(CrmContactServiceSummary row)
    {
        ContactServiceSummaryVo.SummarySections s = new ContactServiceSummaryVo.SummarySections();
        if (row == null)
        {
            return s;
        }
        s.setCurrentNeed(row.getCurrentNeed());
        s.setConfirmedFacts(row.getConfirmedFacts());
        s.setCommitmentsTodos(row.getCommitmentsTodos());
        s.setRisksSentiment(row.getRisksSentiment());
        ContactServiceSummaryVo.LastTouch lt = new ContactServiceSummaryVo.LastTouch();
        lt.setAt(row.getLastTouchAt());
        lt.setChannel(row.getLastTouchChannel());
        lt.setSummary(row.getLastTouchSummary());
        s.setLastTouch(lt);
        return s;
    }

    private static void applySections(CrmContactServiceSummary row, ContactServiceSummaryVo.SummarySections s)
    {
        if (s == null)
        {
            return;
        }
        row.setCurrentNeed(s.getCurrentNeed());
        row.setConfirmedFacts(s.getConfirmedFacts());
        row.setCommitmentsTodos(s.getCommitmentsTodos());
        row.setRisksSentiment(s.getRisksSentiment());
        if (s.getLastTouch() != null)
        {
            if (s.getLastTouch().getAt() != null)
            {
                row.setLastTouchAt(s.getLastTouch().getAt());
            }
            row.setLastTouchChannel(s.getLastTouch().getChannel());
            row.setLastTouchSummary(s.getLastTouch().getSummary());
        }
    }

    private static String truncate(String s, int max)
    {
        if (s == null || s.length() <= max)
        {
            return s;
        }
        return s.substring(0, max);
    }
}
