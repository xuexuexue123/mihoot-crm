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
import com.ruoyi.crm.domain.CrmProfessionalService;
import com.ruoyi.crm.domain.dto.ProfessionalServiceBatchRequest;
import com.ruoyi.crm.domain.dto.ProfessionalServiceBatchResult;
import com.ruoyi.crm.mapper.CrmProfessionalServiceMapper;

@ExtendWith(MockitoExtension.class)
class CrmProfessionalServiceServiceImplTest
{
    @Mock
    private CrmProfessionalServiceMapper crmProfessionalServiceMapper;

    @InjectMocks
    private CrmProfessionalServiceServiceImpl service;

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
        when(crmProfessionalServiceMapper.insertCrmProfessionalService(any())).thenReturn(1);
        when(crmProfessionalServiceMapper.selectCrmProfessionalServiceById(anyString())).thenAnswer(inv -> {
            CrmProfessionalService e = validService();
            e.setId(inv.getArgument(0));
            return e;
        });

        CrmProfessionalService row = validService();
        row.setId(null);

        CrmProfessionalService saved = service.insertCrmProfessionalService(row);

        ArgumentCaptor<CrmProfessionalService> captor = ArgumentCaptor.forClass(CrmProfessionalService.class);
        verify(crmProfessionalServiceMapper).insertCrmProfessionalService(captor.capture());
        CrmProfessionalService inserted = captor.getValue();
        assertTrue(inserted.getId().startsWith("srv-"));
        assertEquals("0", inserted.getDelFlag());
        assertNotNull(saved);
        assertTrue(saved.getId().startsWith("srv-"));
    }

    @Test
    void insert_nullCoreServices_becomesEmptyList()
    {
        when(crmProfessionalServiceMapper.insertCrmProfessionalService(any())).thenReturn(1);
        when(crmProfessionalServiceMapper.selectCrmProfessionalServiceById(anyString())).thenAnswer(inv -> {
            CrmProfessionalService e = validService();
            e.setId(inv.getArgument(0));
            e.setCoreServices(Collections.emptyList());
            return e;
        });

        CrmProfessionalService row = validService();
        row.setCoreServices(null);

        service.insertCrmProfessionalService(row);

        ArgumentCaptor<CrmProfessionalService> captor = ArgumentCaptor.forClass(CrmProfessionalService.class);
        verify(crmProfessionalServiceMapper).insertCrmProfessionalService(captor.capture());
        assertNotNull(captor.getValue().getCoreServices());
        assertTrue(captor.getValue().getCoreServices().isEmpty());
    }

    @Test
    void insert_rejectsMissingNameCn()
    {
        CrmProfessionalService row = validService();
        row.setNameCn(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmProfessionalService(row));
        verify(crmProfessionalServiceMapper, never()).insertCrmProfessionalService(any());
    }

    @Test
    void insert_rejectsMissingCategory()
    {
        CrmProfessionalService row = validService();
        row.setCategory("");

        assertThrows(ServiceException.class, () -> service.insertCrmProfessionalService(row));
        verify(crmProfessionalServiceMapper, never()).insertCrmProfessionalService(any());
    }

    @Test
    void insert_rejectsMissingBackground()
    {
        CrmProfessionalService row = validService();
        row.setBackground(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmProfessionalService(row));
        verify(crmProfessionalServiceMapper, never()).insertCrmProfessionalService(any());
    }

    @Test
    void insert_rejectsMissingKeyCapabilities()
    {
        CrmProfessionalService row = validService();
        row.setKeyCapabilities("");

        assertThrows(ServiceException.class, () -> service.insertCrmProfessionalService(row));
        verify(crmProfessionalServiceMapper, never()).insertCrmProfessionalService(any());
    }

    @Test
    void update_rejectsMissing()
    {
        when(crmProfessionalServiceMapper.selectCrmProfessionalServiceById("x")).thenReturn(null);

        CrmProfessionalService row = validService();
        row.setId("x");

        assertThrows(ServiceException.class, () -> service.updateCrmProfessionalService(row));
        verify(crmProfessionalServiceMapper, never()).updateCrmProfessionalService(any());
    }

    @Test
    void delete_softDeletes()
    {
        when(crmProfessionalServiceMapper.logicalDeleteById(eq("s1"), eq("admin"))).thenReturn(1);

        int rows = service.deleteCrmProfessionalServiceById("s1");
        assertEquals(1, rows);
        verify(crmProfessionalServiceMapper).logicalDeleteById("s1", "admin");
    }

    @Test
    void batch_upsert_insertsAndUpdates()
    {
        CrmProfessionalService existing = validService();
        existing.setId("srv-existing");

        when(crmProfessionalServiceMapper.selectCrmProfessionalServiceById("srv-existing")).thenReturn(existing);
        when(crmProfessionalServiceMapper.selectCrmProfessionalServiceById("srv-new")).thenReturn(null);
        when(crmProfessionalServiceMapper.insertCrmProfessionalService(any())).thenReturn(1);
        when(crmProfessionalServiceMapper.updateCrmProfessionalService(any())).thenReturn(1);

        CrmProfessionalService updateRow = validService();
        updateRow.setId("srv-existing");
        updateRow.setNameCn("新名");

        CrmProfessionalService insertRow = validService();
        insertRow.setId("srv-new");
        insertRow.setNameCn("新机构");

        ProfessionalServiceBatchRequest req = new ProfessionalServiceBatchRequest();
        req.setMode("upsert");
        req.setRows(Arrays.asList(updateRow, insertRow));

        ProfessionalServiceBatchResult result = service.batch(req);
        assertEquals(1, result.getInserted());
        assertEquals(1, result.getUpdated());
        assertEquals(0, result.getSkipped());
        verify(crmProfessionalServiceMapper, times(1)).updateCrmProfessionalService(any());
        verify(crmProfessionalServiceMapper, times(1)).insertCrmProfessionalService(any());
    }

    @Test
    void batch_insert_skipsConflict()
    {
        CrmProfessionalService existing = validService();
        existing.setId("srv-existing");
        when(crmProfessionalServiceMapper.selectCrmProfessionalServiceById("srv-existing")).thenReturn(existing);

        CrmProfessionalService row = validService();
        row.setId("srv-existing");

        ProfessionalServiceBatchRequest req = new ProfessionalServiceBatchRequest();
        req.setMode("insert");
        req.setRows(Collections.singletonList(row));

        ProfessionalServiceBatchResult result = service.batch(req);
        assertEquals(0, result.getInserted());
        assertEquals(0, result.getUpdated());
        assertEquals(1, result.getSkipped());
        verify(crmProfessionalServiceMapper, never()).insertCrmProfessionalService(any());
        verify(crmProfessionalServiceMapper, never()).updateCrmProfessionalService(any());
    }

    @Test
    void batch_rejectsInvalidMode()
    {
        ProfessionalServiceBatchRequest req = new ProfessionalServiceBatchRequest();
        req.setMode("merge");
        req.setRows(Collections.singletonList(validService()));

        assertThrows(ServiceException.class, () -> service.batch(req));
    }

    @Test
    void selectById_blankReturnsNull()
    {
        assertNull(service.selectCrmProfessionalServiceById("  "));
        verify(crmProfessionalServiceMapper, never()).selectCrmProfessionalServiceById(anyString());
    }

    @Test
    void list_normalizesAllFilters()
    {
        when(crmProfessionalServiceMapper.selectCrmProfessionalServiceList(any())).thenReturn(Collections.emptyList());

        CrmProfessionalService query = new CrmProfessionalService();
        query.setCategory("ALL");
        query.setBackground("ALL");
        query.setProvince("ALL");
        query.setSearchQuery("  Baker  ");

        service.selectCrmProfessionalServiceList(query);

        ArgumentCaptor<CrmProfessionalService> captor = ArgumentCaptor.forClass(CrmProfessionalService.class);
        verify(crmProfessionalServiceMapper).selectCrmProfessionalServiceList(captor.capture());
        CrmProfessionalService normalized = captor.getValue();
        assertNull(normalized.getCategory());
        assertNull(normalized.getBackground());
        assertNull(normalized.getProvince());
        assertEquals("Baker", normalized.getSearchQuery());
    }

    @Test
    void insert_clientProvidedId_ok()
    {
        when(crmProfessionalServiceMapper.insertCrmProfessionalService(any())).thenReturn(1);
        when(crmProfessionalServiceMapper.selectCrmProfessionalServiceById("srv-custom"))
                .thenReturn(null)
                .thenReturn(serviceRow("srv-custom"));

        CrmProfessionalService row = validService();
        row.setId("srv-custom");

        CrmProfessionalService saved = service.insertCrmProfessionalService(row);
        assertNotNull(saved);
        assertEquals("srv-custom", saved.getId());
    }

    private static CrmProfessionalService validService()
    {
        CrmProfessionalService e = new CrmProfessionalService();
        e.setNameCn("贝克麦坚时律师事务所");
        e.setNameVn("Baker McKenzie Vietnam");
        e.setNameEn("Baker McKenzie");
        e.setCategory("法律服务 (Law)");
        e.setBackground("国际大厂/Big4");
        e.setProvince("胡志明市");
        e.setKeyCapabilities("跨境并购、FDI 合规");
        e.setCoreServices(Arrays.asList("公司设立", "劳动合规"));
        e.setTargetClients("中资出海企业");
        return e;
    }

    private static CrmProfessionalService serviceRow(String id)
    {
        CrmProfessionalService e = validService();
        e.setId(id);
        return e;
    }
}
