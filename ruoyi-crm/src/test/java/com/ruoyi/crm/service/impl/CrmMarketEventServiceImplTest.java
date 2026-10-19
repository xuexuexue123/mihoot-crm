package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmMarketEvent;
import com.ruoyi.crm.domain.dto.MarketEventBatchRequest;
import com.ruoyi.crm.domain.dto.MarketEventBatchResult;
import com.ruoyi.crm.mapper.CrmMarketEventMapper;

@ExtendWith(MockitoExtension.class)
class CrmMarketEventServiceImplTest
{
    @Mock
    private CrmMarketEventMapper crmMarketEventMapper;

    @InjectMocks
    private CrmMarketEventServiceImpl service;

    @BeforeEach
    void setUpLoginUser()
    {
        SysUser user = new SysUser();
        user.setUserName("admin");
        LoginUser loginUser = new LoginUser(1L, 1L, user, Collections.emptySet());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearLoginUser()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void insert_generatesId()
    {
        when(crmMarketEventMapper.insertCrmMarketEvent(any())).thenReturn(1);
        when(crmMarketEventMapper.selectCrmMarketEventById(anyString())).thenAnswer(inv -> {
            CrmMarketEvent e = validEvent();
            e.setId(inv.getArgument(0));
            return e;
        });

        CrmMarketEvent saved = service.insertCrmMarketEvent(validEvent());

        ArgumentCaptor<CrmMarketEvent> captor = ArgumentCaptor.forClass(CrmMarketEvent.class);
        verify(crmMarketEventMapper).insertCrmMarketEvent(captor.capture());
        CrmMarketEvent inserted = captor.getValue();
        assertTrue(inserted.getId().startsWith("evt-"));
        assertEquals("0", inserted.getDelFlag());
        assertEquals("/profile/upload/cover.jpg", inserted.getCoverUrl());
        assertNotNull(saved.getId());
    }

    @Test
    void insert_blankCoverUrl_normalizedToNull()
    {
        when(crmMarketEventMapper.insertCrmMarketEvent(any())).thenReturn(1);
        when(crmMarketEventMapper.selectCrmMarketEventById(anyString())).thenAnswer(inv -> {
            CrmMarketEvent e = validEvent();
            e.setId(inv.getArgument(0));
            return e;
        });

        CrmMarketEvent row = validEvent();
        row.setCoverUrl("   ");
        service.insertCrmMarketEvent(row);

        ArgumentCaptor<CrmMarketEvent> captor = ArgumentCaptor.forClass(CrmMarketEvent.class);
        verify(crmMarketEventMapper).insertCrmMarketEvent(captor.capture());
        assertNull(captor.getValue().getCoverUrl());
    }

    @Test
    void insert_rejectsMissingTitleCn()
    {
        CrmMarketEvent row = validEvent();
        row.setTitleCn(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmMarketEvent(row));
        verify(crmMarketEventMapper, never()).insertCrmMarketEvent(any());
    }

    @Test
    void insert_rejectsMissingHighlights()
    {
        CrmMarketEvent row = validEvent();
        row.setHighlights("");

        assertThrows(ServiceException.class, () -> service.insertCrmMarketEvent(row));
        verify(crmMarketEventMapper, never()).insertCrmMarketEvent(any());
    }

    @Test
    void insert_rejectsDuplicateId()
    {
        when(crmMarketEventMapper.selectCrmMarketEventById("evt-dup")).thenReturn(validEvent());

        CrmMarketEvent row = validEvent();
        row.setId("evt-dup");

        assertThrows(ServiceException.class, () -> service.insertCrmMarketEvent(row));
        verify(crmMarketEventMapper, never()).insertCrmMarketEvent(any());
    }

    @Test
    void insert_clientProvidedId_ok()
    {
        when(crmMarketEventMapper.insertCrmMarketEvent(any())).thenReturn(1);
        when(crmMarketEventMapper.selectCrmMarketEventById("evt-custom"))
                .thenReturn(null)
                .thenReturn(event("evt-custom"));

        CrmMarketEvent row = validEvent();
        row.setId("evt-custom");

        CrmMarketEvent saved = service.insertCrmMarketEvent(row);
        assertNotNull(saved);
        assertEquals("evt-custom", saved.getId());
    }

    @Test
    void update_rejectsMissing()
    {
        when(crmMarketEventMapper.selectCrmMarketEventById("x")).thenReturn(null);

        CrmMarketEvent row = validEvent();
        row.setId("x");

        assertThrows(ServiceException.class, () -> service.updateCrmMarketEvent(row));
        verify(crmMarketEventMapper, never()).updateCrmMarketEvent(any());
    }

    @Test
    void delete_softDeletes()
    {
        when(crmMarketEventMapper.logicalDeleteById(eq("e1"), eq("admin"))).thenReturn(1);

        int rows = service.deleteCrmMarketEventById("e1");
        assertEquals(1, rows);
        verify(crmMarketEventMapper).logicalDeleteById("e1", "admin");
    }

    @Test
    void batch_upsert_insertsAndUpdates()
    {
        CrmMarketEvent existing = validEvent();
        existing.setId("evt-existing");

        when(crmMarketEventMapper.selectCrmMarketEventById("evt-existing")).thenReturn(existing);
        when(crmMarketEventMapper.selectCrmMarketEventById("evt-new")).thenReturn(null);
        when(crmMarketEventMapper.insertCrmMarketEvent(any())).thenReturn(1);
        when(crmMarketEventMapper.updateCrmMarketEvent(any())).thenReturn(1);

        CrmMarketEvent updateRow = validEvent();
        updateRow.setId("evt-existing");
        updateRow.setTitleCn("新标题");

        CrmMarketEvent insertRow = validEvent();
        insertRow.setId("evt-new");
        insertRow.setTitleCn("新活动");

        MarketEventBatchRequest req = new MarketEventBatchRequest();
        req.setMode("upsert");
        req.setRows(Arrays.asList(updateRow, insertRow));

        MarketEventBatchResult result = service.batch(req);
        assertEquals(1, result.getInserted());
        assertEquals(1, result.getUpdated());
        assertEquals(0, result.getSkipped());
        verify(crmMarketEventMapper, times(1)).updateCrmMarketEvent(any());
        verify(crmMarketEventMapper, times(1)).insertCrmMarketEvent(any());
    }

    @Test
    void batch_insert_skipsConflict()
    {
        CrmMarketEvent existing = validEvent();
        existing.setId("evt-existing");
        when(crmMarketEventMapper.selectCrmMarketEventById("evt-existing")).thenReturn(existing);

        CrmMarketEvent row = validEvent();
        row.setId("evt-existing");

        MarketEventBatchRequest req = new MarketEventBatchRequest();
        req.setMode("insert");
        req.setRows(Collections.singletonList(row));

        MarketEventBatchResult result = service.batch(req);
        assertEquals(0, result.getInserted());
        assertEquals(0, result.getUpdated());
        assertEquals(1, result.getSkipped());
        verify(crmMarketEventMapper, never()).insertCrmMarketEvent(any());
        verify(crmMarketEventMapper, never()).updateCrmMarketEvent(any());
    }

    @Test
    void batch_rejectsInvalidMode()
    {
        MarketEventBatchRequest req = new MarketEventBatchRequest();
        req.setMode("merge");
        req.setRows(Collections.singletonList(validEvent()));

        assertThrows(ServiceException.class, () -> service.batch(req));
    }

    @Test
    void selectById_blankReturnsNull()
    {
        assertNull(service.selectCrmMarketEventById("  "));
        verify(crmMarketEventMapper, never()).selectCrmMarketEventById(anyString());
    }

    @Test
    void list_normalizesAllFilters()
    {
        when(crmMarketEventMapper.selectCrmMarketEventList(any())).thenReturn(Collections.emptyList());

        CrmMarketEvent query = new CrmMarketEvent();
        query.setEventType("ALL");
        query.setStatus("ALL");
        query.setProvince("ALL");
        query.setSearchQuery("  展会  ");

        service.selectCrmMarketEventList(query);

        ArgumentCaptor<CrmMarketEvent> captor = ArgumentCaptor.forClass(CrmMarketEvent.class);
        verify(crmMarketEventMapper).selectCrmMarketEventList(captor.capture());
        CrmMarketEvent normalized = captor.getValue();
        assertNull(normalized.getEventType());
        assertNull(normalized.getStatus());
        assertNull(normalized.getProvince());
        assertEquals("展会", normalized.getSearchQuery());
    }

    private static CrmMarketEvent validEvent()
    {
        CrmMarketEvent e = new CrmMarketEvent();
        e.setTitleCn("越南国际工业展");
        e.setTitleVn("Triển lãm Công nghiệp Quốc tế Việt Nam");
        e.setTitleEn("Vietnam International Industrial Expo");
        e.setEventType("展会/博览会 (Exhibition)");
        e.setStatus("即将举办 (Upcoming)");
        e.setOrganizer("Vietnam Exhibition Center");
        e.setIndustry("制造业");
        e.setStartDate("2026-09-01");
        e.setEndDate("2026-09-04");
        e.setProvince("河内");
        e.setVenue("Vietnam Exhibition Center");
        e.setTargetAudience("制造业企业");
        e.setHighlights("聚焦智能制造与供应链");
        e.setCoverUrl("/profile/upload/cover.jpg");
        return e;
    }

    private static CrmMarketEvent event(String id)
    {
        CrmMarketEvent e = validEvent();
        e.setId(id);
        return e;
    }
}
