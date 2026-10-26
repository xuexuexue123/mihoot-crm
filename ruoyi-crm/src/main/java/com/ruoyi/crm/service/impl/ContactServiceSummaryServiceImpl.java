package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContactServiceSummary;
import com.ruoyi.crm.domain.CrmContactServiceSummaryRevision;
import com.ruoyi.crm.domain.CrmConversationSummaryDraft;
import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.dto.ContactServiceSummaryPatchDto;
import com.ruoyi.crm.domain.dto.ContactServiceSummaryRefreshDto;
import com.ruoyi.crm.domain.dto.ServiceSummaryDeltaItem;
import com.ruoyi.crm.domain.vo.ContactServiceSummaryVo;
import com.ruoyi.crm.domain.vo.KfSummaryCompatVo;
import com.ruoyi.crm.mapper.CrmContactServiceSummaryMapper;
import com.ruoyi.crm.mapper.CrmContactServiceSummaryRevisionMapper;
import com.ruoyi.crm.mapper.CrmConversationSummaryDraftMapper;
import com.ruoyi.crm.mapper.CrmWecomKfConversationMapper;
import com.ruoyi.crm.service.IContactServiceSummaryService;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.ContactServiceSummaryDebouncer;
import com.ruoyi.crm.service.support.ContactServiceSummaryDeltaLoader;
import com.ruoyi.crm.service.support.ContactServiceSummaryLlmClient;
import com.ruoyi.crm.service.support.ContactServiceSummaryWorker;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;

@Service
public class ContactServiceSummaryServiceImpl implements IContactServiceSummaryService
{
    private static final Logger log = LoggerFactory.getLogger(ContactServiceSummaryServiceImpl.class);

    private static final long REFRESH_TIMEOUT_SECONDS = 30L;
    private static final String CHANNEL_WECOM_KF = "wecom_kf";

    @Autowired
    private CrmContactServiceSummaryMapper summaryMapper;

    @Autowired
    private CrmContactServiceSummaryRevisionMapper revisionMapper;

    @Autowired
    private CrmConversationSummaryDraftMapper draftMapper;

    @Autowired
    private ContactServiceSummaryWorker worker;

    @Autowired
    private ContactServiceSummaryDebouncer debouncer;

    @Autowired
    private ContactServiceSummaryDeltaLoader deltaLoader;

    @Autowired
    private ContactServiceSummaryLlmClient llmClient;

    @Autowired(required = false)
    private ICrmCustomerIdentityService identityService;

    @Autowired(required = false)
    private CrmWecomKfConversationMapper kfConversationMapper;

    private final ExecutorService refreshExecutor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "contact-service-summary-refresh");
        t.setDaemon(true);
        return t;
    });

    @PreDestroy
    public void shutdown()
    {
        refreshExecutor.shutdownNow();
    }

    @Override
    public ContactServiceSummaryVo get(String tenantId, String contactId)
    {
        tenantId = requireTenant(tenantId);
        CrmContactServiceSummary row = summaryMapper.selectByContactId(tenantId, contactId);
        return row == null ? null : toVo(row);
    }

    @Override
    public ContactServiceSummaryVo refresh(String tenantId, String contactId, ContactServiceSummaryRefreshDto dto)
    {
        tenantId = requireTenant(tenantId);
        boolean forceFull = dto != null && Boolean.TRUE.equals(dto.getForceFull());
        String trigger = "refresh";
        if (dto != null && "kf_panel".equals(dto.getReason()))
        {
            trigger = "refresh";
        }
        final String tid = tenantId;
        final String cid = contactId;
        final String trg = trigger;
        final boolean force = forceFull;
        Future<CrmContactServiceSummary> future = refreshExecutor.submit(new Callable<CrmContactServiceSummary>()
        {
            @Override
            public CrmContactServiceSummary call()
            {
                String previous = null;
                try
                {
                    previous = com.ruoyi.framework.datasource.DynamicDataSourceContextHolder.getDataSourceKey();
                    com.ruoyi.framework.datasource.DynamicDataSourceContextHolder.setDataSourceKey(tid);
                    return worker.run(tid, cid, trg, force);
                }
                finally
                {
                    if (StringUtils.isNotEmpty(previous))
                    {
                        com.ruoyi.framework.datasource.DynamicDataSourceContextHolder.setDataSourceKey(previous);
                    }
                    else
                    {
                        com.ruoyi.framework.datasource.DynamicDataSourceContextHolder.clearDataSourceKey();
                    }
                }
            }
        });
        try
        {
            CrmContactServiceSummary row = future.get(REFRESH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return toVo(row);
        }
        catch (TimeoutException e)
        {
            log.info("Summary refresh timeout contactId={}", contactId);
            CrmContactServiceSummary row = summaryMapper.selectByContactId(tenantId, contactId);
            if (row == null)
            {
                row = new CrmContactServiceSummary();
                row.setTenantId(tenantId);
                row.setContactId(contactId);
                row.setStatus(ContactServiceSummaryWorker.STATUS_GENERATING);
                row.setVersion(1L);
                row.setGeneratedBy("ai");
                row.setUpdatedAt(new Date());
            }
            else
            {
                row.setStatus(ContactServiceSummaryWorker.STATUS_GENERATING);
            }
            return toVo(row);
        }
        catch (Exception e)
        {
            throw new IllegalStateException("refresh failed: " + e.getMessage(), e);
        }
    }

    @Override
    public ContactServiceSummaryVo patch(String tenantId, String contactId, ContactServiceSummaryPatchDto dto)
    {
        tenantId = requireTenant(tenantId);
        CrmContactServiceSummary row = summaryMapper.selectByContactId(tenantId, contactId);
        Date now = new Date();
        if (row == null)
        {
            row = new CrmContactServiceSummary();
            row.setTenantId(tenantId);
            row.setContactId(contactId);
            row.setVersion(1L);
            row.setStatus(ContactServiceSummaryWorker.STATUS_READY);
            row.setGeneratedBy("human");
            row.setCreatedAt(now);
            row.setUpdatedAt(now);
            applyPatchFields(row, dto);
            mergeLockedFields(row, dto);
            summaryMapper.insert(row);
        }
        else
        {
            applyPatchFields(row, dto);
            mergeLockedFields(row, dto);
            if ("ai".equals(row.getGeneratedBy()))
            {
                row.setGeneratedBy("ai_human");
            }
            else
            {
                row.setGeneratedBy("human");
            }
            row.setVersion((row.getVersion() == null ? 0L : row.getVersion()) + 1L);
            row.setStatus(ContactServiceSummaryWorker.STATUS_READY);
            row.setUpdatedAt(now);
            summaryMapper.update(row);
        }
        insertHumanRevision(row);
        return toVo(row);
    }

    @Override
    public void enqueueBootstrap(String tenantId, String contactId, String trigger, boolean force)
    {
        if (StringUtils.isEmpty(contactId))
        {
            return;
        }
        final String tid = StringUtils.isNotEmpty(tenantId) ? tenantId : TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isEmpty(tid))
        {
            log.warn("enqueueBootstrap skipped: no tenant for contactId={}", contactId);
            return;
        }
        Runnable job = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    CrmContactServiceSummary existing = summaryMapper.selectByContactId(tid, contactId);
                    if (!force && existing != null && ContactServiceSummaryWorker.STATUS_READY.equals(existing.getStatus())
                            && StringUtils.isNotEmpty(existing.getCurrentNeed()))
                    {
                        return;
                    }
                    worker.runAsync(tid, contactId, trigger != null ? trigger : "bootstrap", true);
                }
                catch (Exception e)
                {
                    log.warn("enqueueBootstrap failed contactId={}: {}", contactId, e.getMessage());
                }
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    job.run();
                }
            });
        }
        else
        {
            job.run();
        }
    }

    @Override
    public void enqueueIncremental(String tenantId, String contactId)
    {
        String tid = StringUtils.isNotEmpty(tenantId) ? tenantId : TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isEmpty(tid) || StringUtils.isEmpty(contactId))
        {
            return;
        }
        // mark stale if row exists
        CrmContactServiceSummary existing = summaryMapper.selectByContactId(tid, contactId);
        if (existing != null && ContactServiceSummaryWorker.STATUS_READY.equals(existing.getStatus()))
        {
            summaryMapper.updateStatus(tid, contactId, ContactServiceSummaryWorker.STATUS_STALE, null);
        }
        debouncer.enqueue(tid, contactId);
    }

    @Override
    public void onContactLinked(String tenantId, String contactId, String channel, String conversationKey)
    {
        String tid = StringUtils.isNotEmpty(tenantId) ? tenantId : TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isEmpty(tid) || StringUtils.isEmpty(contactId))
        {
            return;
        }
        if (StringUtils.isNotEmpty(channel) && StringUtils.isNotEmpty(conversationKey))
        {
            try
            {
                CrmConversationSummaryDraft draft = draftMapper.selectOne(tid, channel, conversationKey);
                if (draft != null && StringUtils.isNotEmpty(draft.getDraftJson()))
                {
                    // seed empty summary row from draft sections if none yet
                    CrmContactServiceSummary existing = summaryMapper.selectByContactId(tid, contactId);
                    if (existing == null)
                    {
                        ContactServiceSummaryVo.SummarySections sections = JSON.parseObject(
                                draft.getDraftJson(), ContactServiceSummaryVo.SummarySections.class);
                        CrmContactServiceSummary seed = new CrmContactServiceSummary();
                        seed.setTenantId(tid);
                        seed.setContactId(contactId);
                        seed.setVersion(1L);
                        seed.setStatus(ContactServiceSummaryWorker.STATUS_STALE);
                        seed.setGeneratedBy("ai");
                        Date now = new Date();
                        seed.setCreatedAt(now);
                        seed.setUpdatedAt(now);
                        if (sections != null)
                        {
                            seed.setCurrentNeed(sections.getCurrentNeed());
                            seed.setConfirmedFacts(sections.getConfirmedFacts());
                            seed.setCommitmentsTodos(sections.getCommitmentsTodos());
                            seed.setRisksSentiment(sections.getRisksSentiment());
                        }
                        summaryMapper.insert(seed);
                    }
                    draftMapper.delete(tid, channel, conversationKey);
                }
            }
            catch (Exception e)
            {
                log.warn("merge draft on link failed: {}", e.getMessage());
            }
        }
        enqueueBootstrap(tid, contactId, "link", true);
    }

    @Override
    public KfSummaryCompatVo generateKfSummary(String tenantId, String externalUserid, String openKfid)
    {
        tenantId = requireTenant(tenantId);
        String contactId = resolveKfContactId(externalUserid, openKfid);
        KfSummaryCompatVo vo = new KfSummaryCompatVo();
        if (StringUtils.isNotEmpty(contactId))
        {
            ContactServiceSummaryRefreshDto dto = new ContactServiceSummaryRefreshDto();
            dto.setReason("kf_panel");
            dto.setForceFull(Boolean.FALSE);
            ContactServiceSummaryVo summary = refresh(tenantId, contactId, dto);
            vo.setContactId(contactId);
            vo.setPersisted(Boolean.TRUE);
            if (summary != null)
            {
                vo.setStructured(summary.getSummary());
                vo.setVersion(summary.getVersion());
                vo.setSummary(formatReadable(summary.getSummary()));
            }
            return vo;
        }

        // no contact: draft path
        List<ServiceSummaryDeltaItem> deltas = deltaLoader.loadKfByExternalUserid(null, externalUserid, openKfid,
                ContactServiceSummaryDeltaLoader.DEFAULT_LIMIT);
        ContactServiceSummaryVo.SummarySections sections;
        try
        {
            ContactServiceSummaryLlmClient.LlmResult llm = llmClient.rewrite(tenantId, null,
                    new ArrayList<String>(), deltas, true);
            sections = llm.getSections();
        }
        catch (Exception e)
        {
            sections = new ContactServiceSummaryVo.SummarySections();
            sections.setCurrentNeed("生成失败: " + e.getMessage());
        }
        CrmConversationSummaryDraft draft = new CrmConversationSummaryDraft();
        draft.setTenantId(tenantId);
        draft.setChannel(CHANNEL_WECOM_KF);
        draft.setConversationKey(externalUserid);
        draft.setDraftJson(JSON.toJSONString(sections));
        draft.setUpdatedAt(new Date());
        if (!deltas.isEmpty())
        {
            draft.setLastEventId(deltas.get(deltas.size() - 1).getEventId());
        }
        draftMapper.upsert(draft);

        vo.setContactId(null);
        vo.setPersisted(Boolean.FALSE);
        vo.setStructured(sections);
        vo.setSummary(formatReadable(sections) + "\n\n（未关联联系人，仅暂存草稿；关联后将写入正式服务摘要）");
        return vo;
    }

    private String resolveKfContactId(String externalUserid, String openKfid)
    {
        if (identityService != null && StringUtils.isNotEmpty(externalUserid))
        {
            String viaKf = identityService.findContactByIdentity("wechat_kf_openid", externalUserid.trim());
            if (StringUtils.isNotEmpty(viaKf))
            {
                return viaKf;
            }
            String viaWecom = identityService.findContactByIdentity("wecom_external_userid", externalUserid.trim());
            if (StringUtils.isNotEmpty(viaWecom))
            {
                return viaWecom;
            }
        }
        if (kfConversationMapper != null && StringUtils.isNotEmpty(externalUserid)
                && StringUtils.isNotEmpty(openKfid))
        {
            try
            {
                CrmWecomKfConversation conv = kfConversationMapper.selectByOpenKfidExternalUserid(
                        openKfid, externalUserid);
                if (conv != null && StringUtils.isNotEmpty(conv.getContactId()))
                {
                    return conv.getContactId();
                }
            }
            catch (Exception e)
            {
                log.debug("KF conversation lookup failed: {}", e.getMessage());
            }
        }
        return null;
    }

    private void applyPatchFields(CrmContactServiceSummary row, ContactServiceSummaryPatchDto dto)
    {
        if (dto == null)
        {
            return;
        }
        if (dto.getCurrentNeed() != null)
        {
            row.setCurrentNeed(dto.getCurrentNeed());
        }
        if (dto.getConfirmedFacts() != null)
        {
            row.setConfirmedFacts(dto.getConfirmedFacts());
        }
        if (dto.getCommitmentsTodos() != null)
        {
            row.setCommitmentsTodos(dto.getCommitmentsTodos());
        }
        if (dto.getRisksSentiment() != null)
        {
            row.setRisksSentiment(dto.getRisksSentiment());
        }
    }

    private void mergeLockedFields(CrmContactServiceSummary row, ContactServiceSummaryPatchDto dto)
    {
        if (dto == null || dto.getLockFields() == null || dto.getLockFields().isEmpty())
        {
            return;
        }
        Set<String> set = new LinkedHashSet<>(ContactServiceSummaryDeltaLoader.parseStringList(row.getLockedFields()));
        set.addAll(dto.getLockFields());
        row.setLockedFields(JSON.toJSONString(set));
    }

    private void insertHumanRevision(CrmContactServiceSummary row)
    {
        CrmContactServiceSummaryRevision rev = new CrmContactServiceSummaryRevision();
        rev.setTenantId(row.getTenantId());
        rev.setContactId(row.getContactId());
        rev.setVersion(row.getVersion());
        rev.setTrigger("human_patch");
        rev.setSourceChannels(row.getSourceChannels());
        rev.setModel(row.getModel());
        rev.setGeneratedBy(row.getGeneratedBy());
        try
        {
            rev.setCreatedBy(SecurityUtils.getUsername());
        }
        catch (Exception e)
        {
            rev.setCreatedBy("human");
        }
        rev.setCreatedAt(new Date());
        JSONObject snap = new JSONObject();
        snap.put("currentNeed", row.getCurrentNeed());
        snap.put("confirmedFacts", row.getConfirmedFacts());
        snap.put("commitmentsTodos", row.getCommitmentsTodos());
        snap.put("risksSentiment", row.getRisksSentiment());
        rev.setSnapshot(snap.toJSONString());
        revisionMapper.insert(rev);
    }

    private static ContactServiceSummaryVo toVo(CrmContactServiceSummary row)
    {
        ContactServiceSummaryVo vo = new ContactServiceSummaryVo();
        vo.setContactId(row.getContactId());
        vo.setStatus(row.getStatus());
        vo.setGeneratedBy(row.getGeneratedBy());
        vo.setVersion(row.getVersion());
        vo.setUpdatedAt(row.getUpdatedAt());
        vo.setSourceChannels(ContactServiceSummaryDeltaLoader.parseStringList(row.getSourceChannels()));
        vo.setLockedFields(ContactServiceSummaryDeltaLoader.parseStringList(row.getLockedFields()));
        vo.setModel(row.getModel());
        vo.setErrorMessage(row.getErrorMessage());
        vo.setSummary(ContactServiceSummaryWorker.toSections(row));
        return vo;
    }

    public static String formatReadable(ContactServiceSummaryVo.SummarySections s)
    {
        if (s == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("【当前诉求】").append(nullToEmpty(s.getCurrentNeed())).append("\n");
        sb.append("【已确认事实】").append(nullToEmpty(s.getConfirmedFacts())).append("\n");
        sb.append("【承诺/待办】").append(nullToEmpty(s.getCommitmentsTodos())).append("\n");
        sb.append("【风险与情绪】").append(nullToEmpty(s.getRisksSentiment()));
        if (s.getLastTouch() != null && StringUtils.isNotEmpty(s.getLastTouch().getSummary()))
        {
            sb.append("\n【最近触点】").append(s.getLastTouch().getSummary());
        }
        return sb.toString();
    }

    private static String nullToEmpty(String s)
    {
        return s == null ? "" : s;
    }

    private static String requireTenant(String tenantId)
    {
        if (StringUtils.isNotEmpty(tenantId))
        {
            return tenantId.trim();
        }
        String fromDs = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isNotEmpty(fromDs))
        {
            return fromDs;
        }
        throw new IllegalArgumentException("tenant 不能为空");
    }
}
