package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.crm.domain.CrmContactServiceSummary;
import com.ruoyi.crm.domain.dto.ContactServiceSummaryPatchDto;
import com.ruoyi.crm.domain.dto.ServiceSummaryDeltaItem;
import com.ruoyi.crm.domain.vo.ContactServiceSummaryVo;
import com.ruoyi.crm.domain.vo.KfSummaryCompatVo;
import com.ruoyi.crm.mapper.CrmContactServiceSummaryMapper;
import com.ruoyi.crm.mapper.CrmContactServiceSummaryRevisionMapper;
import com.ruoyi.crm.mapper.CrmConversationSummaryDraftMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.ContactServiceSummaryDebouncer;
import com.ruoyi.crm.service.support.ContactServiceSummaryDeltaLoader;
import com.ruoyi.crm.service.support.ContactServiceSummaryLlmClient;
import com.ruoyi.crm.service.support.ContactServiceSummaryWorker;

@ExtendWith(MockitoExtension.class)
class ContactServiceSummaryServiceImplTest
{
    @Mock
    private CrmContactServiceSummaryMapper summaryMapper;
    @Mock
    private CrmContactServiceSummaryRevisionMapper revisionMapper;
    @Mock
    private CrmConversationSummaryDraftMapper draftMapper;
    @Mock
    private ContactServiceSummaryWorker worker;
    @Mock
    private ContactServiceSummaryDebouncer debouncer;
    @Mock
    private ContactServiceSummaryDeltaLoader deltaLoader;
    @Mock
    private ContactServiceSummaryLlmClient llmClient;
    @Mock
    private ICrmCustomerIdentityService identityService;

    @InjectMocks
    private ContactServiceSummaryServiceImpl service;

    @Test
    void applyLocks_preservesLockedCommitmentsTodos()
    {
        ContactServiceSummaryVo.SummarySections previous = new ContactServiceSummaryVo.SummarySections();
        previous.setCommitmentsTodos("已口头承诺周六上午面诊");
        previous.setCurrentNeed("旧诉求");

        ContactServiceSummaryVo.SummarySections next = new ContactServiceSummaryVo.SummarySections();
        next.setCommitmentsTodos("AI 想改掉的内容");
        next.setCurrentNeed("新诉求");

        ContactServiceSummaryLlmClient.applyLocks(previous, next, Arrays.asList("commitmentsTodos"));

        assertEquals("已口头承诺周六上午面诊", next.getCommitmentsTodos());
        assertEquals("新诉求", next.getCurrentNeed());
    }

    @Test
    void patch_mergesLockFieldsAndWritesRevision()
    {
        CrmContactServiceSummary existing = new CrmContactServiceSummary();
        existing.setTenantId("t1");
        existing.setContactId("c1");
        existing.setVersion(2L);
        existing.setStatus("ready");
        existing.setGeneratedBy("ai");
        existing.setCurrentNeed("need");
        existing.setLockedFields("[\"currentNeed\"]");
        existing.setUpdatedAt(new Date());

        when(summaryMapper.selectByContactId("t1", "c1")).thenReturn(existing);

        ContactServiceSummaryPatchDto dto = new ContactServiceSummaryPatchDto();
        dto.setCommitmentsTodos("人工锁定待办");
        dto.setLockFields(Collections.singletonList("commitmentsTodos"));

        ContactServiceSummaryVo vo = service.patch("t1", "c1", dto);

        assertEquals("人工锁定待办", vo.getSummary().getCommitmentsTodos());
        assertTrue(vo.getLockedFields().contains("commitmentsTodos"));
        assertTrue(vo.getLockedFields().contains("currentNeed"));
        assertEquals("ai_human", vo.getGeneratedBy());
        verify(summaryMapper).update(any(CrmContactServiceSummary.class));
        verify(revisionMapper).insert(any());
    }

    @Test
    void workerFailure_keepsPreviousSectionsViaReload()
    {
        CrmContactServiceSummary existing = readyRow("t1", "c1");
        existing.setCommitmentsTodos("勿覆盖");
        when(summaryMapper.selectByContactId("t1", "c1")).thenReturn(existing);
        when(deltaLoader.loadBootstrap(eq("c1"), any(Integer.class))).thenReturn(Collections.emptyList());
        when(llmClient.rewrite(anyString(), any(), anyList(), anyList(), anyBoolean()))
                .thenThrow(new IllegalStateException("model timeout"));

        ContactServiceSummaryWorker realWorker = new ContactServiceSummaryWorker();
        ReflectionTestUtils.setField(realWorker, "summaryMapper", summaryMapper);
        ReflectionTestUtils.setField(realWorker, "revisionMapper", revisionMapper);
        ReflectionTestUtils.setField(realWorker, "deltaLoader", deltaLoader);
        ReflectionTestUtils.setField(realWorker, "llmClient", llmClient);

        CrmContactServiceSummary result = realWorker.run("t1", "c1", "refresh", true);

        assertEquals("failed", result.getStatus());
        assertEquals("勿覆盖", result.getCommitmentsTodos());
        verify(summaryMapper).updateStatus(eq("t1"), eq("c1"), eq("failed"), anyString());
        verify(summaryMapper, never()).update(any(CrmContactServiceSummary.class));
        verify(revisionMapper, never()).insert(any());
    }

    @Test
    void enqueueBootstrap_skipsWhenReadySummaryExists()
    {
        CrmContactServiceSummary existing = readyRow("t1", "c1");
        when(summaryMapper.selectByContactId("t1", "c1")).thenReturn(existing);

        service.enqueueBootstrap("t1", "c1", "convert", false);

        verify(worker, never()).runAsync(anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void enqueueBootstrap_runsWhenMissing()
    {
        when(summaryMapper.selectByContactId("t1", "c1")).thenReturn(null);

        service.enqueueBootstrap("t1", "c1", "convert", false);

        verify(worker).runAsync("t1", "c1", "convert", true);
    }

    @Test
    void kfSummary_withContact_usesRefreshPath()
    {
        when(identityService.findContactByIdentity("wechat_kf_openid", "eu1")).thenReturn("c1");

        ContactServiceSummaryServiceImpl spy = org.mockito.Mockito.spy(service);
        ContactServiceSummaryVo summaryVo = new ContactServiceSummaryVo();
        summaryVo.setContactId("c1");
        summaryVo.setVersion(3L);
        ContactServiceSummaryVo.SummarySections sections = new ContactServiceSummaryVo.SummarySections();
        sections.setCurrentNeed("种植牙");
        summaryVo.setSummary(sections);
        org.mockito.Mockito.doReturn(summaryVo).when(spy).refresh(eq("t1"), eq("c1"), any());

        KfSummaryCompatVo vo = spy.generateKfSummary("t1", "eu1", null);

        assertTrue(Boolean.TRUE.equals(vo.getPersisted()));
        assertEquals("c1", vo.getContactId());
        assertEquals(Long.valueOf(3L), vo.getVersion());
        assertNotNull(vo.getSummary());
        assertTrue(vo.getSummary().contains("种植牙"));
    }

    @Test
    void kfSummary_withoutContact_writesDraft()
    {
        when(identityService.findContactByIdentity(anyString(), anyString())).thenReturn(null);
        when(deltaLoader.loadKfByExternalUserid(any(), eq("eu2"), any(), any(Integer.class)))
                .thenReturn(Collections.<ServiceSummaryDeltaItem>emptyList());
        ContactServiceSummaryLlmClient.LlmResult llm = new ContactServiceSummaryLlmClient.LlmResult();
        ContactServiceSummaryVo.SummarySections sections = new ContactServiceSummaryVo.SummarySections();
        sections.setCurrentNeed("草稿诉求");
        llm.setSections(sections);
        llm.setModel("test");
        when(llmClient.rewrite(anyString(), any(), anyList(), anyList(), eq(true))).thenReturn(llm);

        KfSummaryCompatVo vo = service.generateKfSummary("t1", "eu2", "kf1");

        assertFalse(Boolean.TRUE.equals(vo.getPersisted()));
        assertNull(vo.getContactId());
        assertEquals("草稿诉求", vo.getStructured().getCurrentNeed());
        verify(draftMapper).upsert(any());
    }

    private static CrmContactServiceSummary readyRow(String tenantId, String contactId)
    {
        CrmContactServiceSummary row = new CrmContactServiceSummary();
        row.setTenantId(tenantId);
        row.setContactId(contactId);
        row.setStatus("ready");
        row.setVersion(2L);
        row.setGeneratedBy("ai");
        row.setCurrentNeed("need");
        row.setUpdatedAt(new Date());
        return row;
    }
}
