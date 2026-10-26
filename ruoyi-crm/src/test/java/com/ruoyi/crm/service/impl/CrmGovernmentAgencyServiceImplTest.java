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
import com.ruoyi.crm.domain.CrmGovernmentAgency;
import com.ruoyi.crm.domain.dto.GovernmentAgencyBatchRequest;
import com.ruoyi.crm.domain.dto.GovernmentAgencyBatchResult;
import com.ruoyi.crm.mapper.CrmGovernmentAgencyMapper;

@ExtendWith(MockitoExtension.class)
class CrmGovernmentAgencyServiceImplTest
{
    @Mock
    private CrmGovernmentAgencyMapper crmGovernmentAgencyMapper;

    @InjectMocks
    private CrmGovernmentAgencyServiceImpl service;

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
        when(crmGovernmentAgencyMapper.insertCrmGovernmentAgency(any())).thenReturn(1);
        when(crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById(anyString())).thenAnswer(inv -> {
            CrmGovernmentAgency e = validAgency();
            e.setId(inv.getArgument(0));
            return e;
        });

        CrmGovernmentAgency row = validAgency();
        row.setId(null);

        CrmGovernmentAgency saved = service.insertCrmGovernmentAgency(row);

        ArgumentCaptor<CrmGovernmentAgency> captor = ArgumentCaptor.forClass(CrmGovernmentAgency.class);
        verify(crmGovernmentAgencyMapper).insertCrmGovernmentAgency(captor.capture());
        CrmGovernmentAgency inserted = captor.getValue();
        assertTrue(inserted.getId().startsWith("gov-"));
        assertEquals("0", inserted.getDelFlag());
        assertNotNull(saved);
        assertTrue(saved.getId().startsWith("gov-"));
    }

    @Test
    void insert_nullApprovalItems_becomesEmptyList()
    {
        when(crmGovernmentAgencyMapper.insertCrmGovernmentAgency(any())).thenReturn(1);
        when(crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById(anyString())).thenAnswer(inv -> {
            CrmGovernmentAgency e = validAgency();
            e.setId(inv.getArgument(0));
            e.setApprovalItems(Collections.emptyList());
            return e;
        });

        CrmGovernmentAgency row = validAgency();
        row.setApprovalItems(null);

        service.insertCrmGovernmentAgency(row);

        ArgumentCaptor<CrmGovernmentAgency> captor = ArgumentCaptor.forClass(CrmGovernmentAgency.class);
        verify(crmGovernmentAgencyMapper).insertCrmGovernmentAgency(captor.capture());
        assertNotNull(captor.getValue().getApprovalItems());
        assertTrue(captor.getValue().getApprovalItems().isEmpty());
    }

    @Test
    void insert_rejectsMissingNameCn()
    {
        CrmGovernmentAgency row = validAgency();
        row.setNameCn(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmGovernmentAgency(row));
        verify(crmGovernmentAgencyMapper, never()).insertCrmGovernmentAgency(any());
    }

    @Test
    void insert_rejectsMissingLevel()
    {
        CrmGovernmentAgency row = validAgency();
        row.setLevel("");

        assertThrows(ServiceException.class, () -> service.insertCrmGovernmentAgency(row));
        verify(crmGovernmentAgencyMapper, never()).insertCrmGovernmentAgency(any());
    }

    @Test
    void insert_rejectsMissingCategory()
    {
        CrmGovernmentAgency row = validAgency();
        row.setCategory(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmGovernmentAgency(row));
        verify(crmGovernmentAgencyMapper, never()).insertCrmGovernmentAgency(any());
    }

    @Test
    void update_rejectsMissing()
    {
        when(crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById("x")).thenReturn(null);

        CrmGovernmentAgency row = validAgency();
        row.setId("x");

        assertThrows(ServiceException.class, () -> service.updateCrmGovernmentAgency(row));
        verify(crmGovernmentAgencyMapper, never()).updateCrmGovernmentAgency(any());
    }

    @Test
    void delete_softDeletes()
    {
        when(crmGovernmentAgencyMapper.logicalDeleteById(eq("g1"), eq("admin"))).thenReturn(1);

        int rows = service.deleteCrmGovernmentAgencyById("g1");
        assertEquals(1, rows);
        verify(crmGovernmentAgencyMapper).logicalDeleteById("g1", "admin");
    }

    @Test
    void batch_upsert_insertsAndUpdates()
    {
        CrmGovernmentAgency existing = validAgency();
        existing.setId("gov-existing");

        when(crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById("gov-existing")).thenReturn(existing);
        when(crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById("gov-new")).thenReturn(null);
        when(crmGovernmentAgencyMapper.insertCrmGovernmentAgency(any())).thenReturn(1);
        when(crmGovernmentAgencyMapper.updateCrmGovernmentAgency(any())).thenReturn(1);

        CrmGovernmentAgency updateRow = validAgency();
        updateRow.setId("gov-existing");
        updateRow.setNameCn("新名");

        CrmGovernmentAgency insertRow = validAgency();
        insertRow.setId("gov-new");
        insertRow.setNameCn("新机构");

        GovernmentAgencyBatchRequest req = new GovernmentAgencyBatchRequest();
        req.setMode("upsert");
        req.setRows(Arrays.asList(updateRow, insertRow));

        GovernmentAgencyBatchResult result = service.batch(req);
        assertEquals(1, result.getInserted());
        assertEquals(1, result.getUpdated());
        assertEquals(0, result.getSkipped());
        verify(crmGovernmentAgencyMapper, times(1)).updateCrmGovernmentAgency(any());
        verify(crmGovernmentAgencyMapper, times(1)).insertCrmGovernmentAgency(any());
    }

    @Test
    void batch_insert_skipsConflict()
    {
        CrmGovernmentAgency existing = validAgency();
        existing.setId("gov-existing");
        when(crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById("gov-existing")).thenReturn(existing);

        CrmGovernmentAgency row = validAgency();
        row.setId("gov-existing");

        GovernmentAgencyBatchRequest req = new GovernmentAgencyBatchRequest();
        req.setMode("insert");
        req.setRows(Collections.singletonList(row));

        GovernmentAgencyBatchResult result = service.batch(req);
        assertEquals(0, result.getInserted());
        assertEquals(0, result.getUpdated());
        assertEquals(1, result.getSkipped());
        verify(crmGovernmentAgencyMapper, never()).insertCrmGovernmentAgency(any());
        verify(crmGovernmentAgencyMapper, never()).updateCrmGovernmentAgency(any());
    }

    @Test
    void batch_rejectsInvalidMode()
    {
        GovernmentAgencyBatchRequest req = new GovernmentAgencyBatchRequest();
        req.setMode("merge");
        req.setRows(Collections.singletonList(validAgency()));

        assertThrows(ServiceException.class, () -> service.batch(req));
    }

    @Test
    void selectById_blankReturnsNull()
    {
        assertNull(service.selectCrmGovernmentAgencyById("  "));
        verify(crmGovernmentAgencyMapper, never()).selectCrmGovernmentAgencyById(anyString());
    }

    @Test
    void list_normalizesAllFilters()
    {
        when(crmGovernmentAgencyMapper.selectCrmGovernmentAgencyList(any())).thenReturn(Collections.emptyList());

        CrmGovernmentAgency query = new CrmGovernmentAgency();
        query.setLevel("ALL");
        query.setCategory("ALL");
        query.setProvince("ALL");
        query.setSearchQuery("  MPI  ");

        service.selectCrmGovernmentAgencyList(query);

        ArgumentCaptor<CrmGovernmentAgency> captor = ArgumentCaptor.forClass(CrmGovernmentAgency.class);
        verify(crmGovernmentAgencyMapper).selectCrmGovernmentAgencyList(captor.capture());
        CrmGovernmentAgency normalized = captor.getValue();
        assertNull(normalized.getLevel());
        assertNull(normalized.getCategory());
        assertNull(normalized.getProvince());
        assertEquals("MPI", normalized.getSearchQuery());
    }

    @Test
    void insert_clientProvidedId_ok()
    {
        when(crmGovernmentAgencyMapper.insertCrmGovernmentAgency(any())).thenReturn(1);
        when(crmGovernmentAgencyMapper.selectCrmGovernmentAgencyById("gov-custom"))
                .thenReturn(null)
                .thenReturn(agency("gov-custom"));

        CrmGovernmentAgency row = validAgency();
        row.setId("gov-custom");

        CrmGovernmentAgency saved = service.insertCrmGovernmentAgency(row);
        assertNotNull(saved);
        assertEquals("gov-custom", saved.getId());
    }

    private static CrmGovernmentAgency validAgency()
    {
        CrmGovernmentAgency e = new CrmGovernmentAgency();
        e.setNameCn("越南计划投资部");
        e.setNameVn("Bộ Kế hoạch và Đầu tư");
        e.setNameEn("Ministry of Planning and Investment");
        e.setAbbreviation("MPI");
        e.setLevel("中央部委/级");
        e.setCategory("投资审批与规划 (MPI/DPI)");
        e.setProvince("河内");
        e.setCoreResponsibilities("外资项目审批与规划");
        e.setApprovalItems(Arrays.asList("IRC", "ERC"));
        return e;
    }

    private static CrmGovernmentAgency agency(String id)
    {
        CrmGovernmentAgency e = validAgency();
        e.setId(id);
        return e;
    }
}
