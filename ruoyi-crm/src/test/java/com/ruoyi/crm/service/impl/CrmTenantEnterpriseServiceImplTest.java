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
import com.ruoyi.crm.domain.CrmIndustrialPark;
import com.ruoyi.crm.domain.CrmTenantEnterprise;
import com.ruoyi.crm.domain.dto.TenantEnterpriseBatchRequest;
import com.ruoyi.crm.domain.dto.TenantEnterpriseBatchResult;
import com.ruoyi.crm.mapper.CrmIndustrialParkMapper;
import com.ruoyi.crm.mapper.CrmTenantEnterpriseMapper;

@ExtendWith(MockitoExtension.class)
class CrmTenantEnterpriseServiceImplTest
{
    @Mock
    private CrmTenantEnterpriseMapper crmTenantEnterpriseMapper;

    @Mock
    private CrmIndustrialParkMapper crmIndustrialParkMapper;

    @InjectMocks
    private CrmTenantEnterpriseServiceImpl service;

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
    void insert_generatesIdAndBackfillsParkName()
    {
        CrmIndustrialPark park = new CrmIndustrialPark();
        park.setId("vn-ip-1");
        park.setNameCn("北宁工业园");
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById("vn-ip-1")).thenReturn(park);
        when(crmTenantEnterpriseMapper.insertCrmTenantEnterprise(any())).thenReturn(1);
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(anyString())).thenAnswer(inv -> {
            CrmTenantEnterprise e = validEnterprise();
            e.setId(inv.getArgument(0));
            e.setParkNameCn("北宁工业园");
            return e;
        });

        CrmTenantEnterprise row = validEnterprise();
        row.setParkNameCn("前端传入名");

        CrmTenantEnterprise saved = service.insertCrmTenantEnterprise(row);

        ArgumentCaptor<CrmTenantEnterprise> captor = ArgumentCaptor.forClass(CrmTenantEnterprise.class);
        verify(crmTenantEnterpriseMapper).insertCrmTenantEnterprise(captor.capture());
        CrmTenantEnterprise inserted = captor.getValue();
        assertTrue(inserted.getId().startsWith("ent-"));
        assertEquals("0", inserted.getDelFlag());
        assertEquals("北宁工业园", inserted.getParkNameCn());
        assertEquals("北宁工业园", saved.getParkNameCn());
    }

    @Test
    void insert_keepsParkNameWhenParkMissing()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById("vn-ip-missing")).thenReturn(null);
        when(crmTenantEnterpriseMapper.insertCrmTenantEnterprise(any())).thenReturn(1);
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(anyString())).thenAnswer(inv -> {
            CrmTenantEnterprise e = validEnterprise();
            e.setId(inv.getArgument(0));
            e.setParkId("vn-ip-missing");
            e.setParkNameCn("前端园区名");
            return e;
        });

        CrmTenantEnterprise row = validEnterprise();
        row.setParkId("vn-ip-missing");
        row.setParkNameCn("前端园区名");

        service.insertCrmTenantEnterprise(row);

        ArgumentCaptor<CrmTenantEnterprise> captor = ArgumentCaptor.forClass(CrmTenantEnterprise.class);
        verify(crmTenantEnterpriseMapper).insertCrmTenantEnterprise(captor.capture());
        assertEquals("前端园区名", captor.getValue().getParkNameCn());
    }

    @Test
    void insert_rejectsMissingParkId()
    {
        CrmTenantEnterprise row = validEnterprise();
        row.setParkId("");

        assertThrows(ServiceException.class, () -> service.insertCrmTenantEnterprise(row));
        verify(crmTenantEnterpriseMapper, never()).insertCrmTenantEnterprise(any());
    }

    @Test
    void insert_rejectsMissingNameCn()
    {
        CrmTenantEnterprise row = validEnterprise();
        row.setNameCn(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmTenantEnterprise(row));
        verify(crmTenantEnterpriseMapper, never()).insertCrmTenantEnterprise(any());
    }

    @Test
    void update_rejectsMissing()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById("x")).thenReturn(null);

        CrmTenantEnterprise row = validEnterprise();
        row.setId("x");

        assertThrows(ServiceException.class, () -> service.updateCrmTenantEnterprise(row));
        verify(crmTenantEnterpriseMapper, never()).updateCrmTenantEnterprise(any());
    }

    @Test
    void delete_softDeletes()
    {
        when(crmTenantEnterpriseMapper.logicalDeleteById(eq("e1"), eq("admin"))).thenReturn(1);

        int rows = service.deleteCrmTenantEnterpriseById("e1");
        assertEquals(1, rows);
        verify(crmTenantEnterpriseMapper).logicalDeleteById("e1", "admin");
    }

    @Test
    void batch_upsert_insertsAndUpdates()
    {
        CrmTenantEnterprise existing = validEnterprise();
        existing.setId("ent-existing");

        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById("ent-existing")).thenReturn(existing);
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById("ent-new")).thenReturn(null);
        when(crmTenantEnterpriseMapper.insertCrmTenantEnterprise(any())).thenReturn(1);
        when(crmTenantEnterpriseMapper.updateCrmTenantEnterprise(any())).thenReturn(1);

        CrmTenantEnterprise updateRow = validEnterprise();
        updateRow.setId("ent-existing");
        updateRow.setNameCn("新名");

        CrmTenantEnterprise insertRow = validEnterprise();
        insertRow.setId("ent-new");
        insertRow.setNameCn("新企业");

        TenantEnterpriseBatchRequest req = new TenantEnterpriseBatchRequest();
        req.setMode("upsert");
        req.setRows(Arrays.asList(updateRow, insertRow));

        TenantEnterpriseBatchResult result = service.batch(req);
        assertEquals(1, result.getInserted());
        assertEquals(1, result.getUpdated());
        assertEquals(0, result.getSkipped());
        verify(crmTenantEnterpriseMapper, times(1)).updateCrmTenantEnterprise(any());
        verify(crmTenantEnterpriseMapper, times(1)).insertCrmTenantEnterprise(any());
    }

    @Test
    void batch_insert_skipsConflict()
    {
        CrmTenantEnterprise existing = validEnterprise();
        existing.setId("ent-existing");
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById("ent-existing")).thenReturn(existing);

        CrmTenantEnterprise row = validEnterprise();
        row.setId("ent-existing");

        TenantEnterpriseBatchRequest req = new TenantEnterpriseBatchRequest();
        req.setMode("insert");
        req.setRows(Collections.singletonList(row));

        TenantEnterpriseBatchResult result = service.batch(req);
        assertEquals(0, result.getInserted());
        assertEquals(0, result.getUpdated());
        assertEquals(1, result.getSkipped());
        verify(crmTenantEnterpriseMapper, never()).insertCrmTenantEnterprise(any());
        verify(crmTenantEnterpriseMapper, never()).updateCrmTenantEnterprise(any());
    }

    @Test
    void batch_rejectsInvalidMode()
    {
        TenantEnterpriseBatchRequest req = new TenantEnterpriseBatchRequest();
        req.setMode("merge");
        req.setRows(Collections.singletonList(validEnterprise()));

        assertThrows(ServiceException.class, () -> service.batch(req));
    }

    @Test
    void selectById_blankReturnsNull()
    {
        assertNull(service.selectCrmTenantEnterpriseById("  "));
        verify(crmTenantEnterpriseMapper, never()).selectCrmTenantEnterpriseById(anyString());
    }

    @Test
    void list_normalizesAllFilters()
    {
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseList(any())).thenReturn(Collections.emptyList());

        CrmTenantEnterprise query = new CrmTenantEnterprise();
        query.setParkId("ALL");
        query.setIndustry("ALL");
        query.setEnterpriseType("ALL");
        query.setCountryOfOrigin("ALL");
        query.setStatus("ALL");
        query.setSearchQuery("  三星  ");

        service.selectCrmTenantEnterpriseList(query);

        ArgumentCaptor<CrmTenantEnterprise> captor = ArgumentCaptor.forClass(CrmTenantEnterprise.class);
        verify(crmTenantEnterpriseMapper).selectCrmTenantEnterpriseList(captor.capture());
        CrmTenantEnterprise normalized = captor.getValue();
        assertNull(normalized.getParkId());
        assertNull(normalized.getIndustry());
        assertNull(normalized.getEnterpriseType());
        assertNull(normalized.getEnterpriseTypeIn());
        assertNull(normalized.getCountryOfOrigin());
        assertNull(normalized.getStatus());
        assertEquals("三星", normalized.getSearchQuery());
    }

    @Test
    void list_expandsFdiFilterToIncludeLegacyWfoe()
    {
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseList(any())).thenReturn(Collections.emptyList());

        CrmTenantEnterprise query = new CrmTenantEnterprise();
        query.setEnterpriseType("外国直接投资 (FDI)");

        service.selectCrmTenantEnterpriseList(query);

        ArgumentCaptor<CrmTenantEnterprise> captor = ArgumentCaptor.forClass(CrmTenantEnterprise.class);
        verify(crmTenantEnterpriseMapper).selectCrmTenantEnterpriseList(captor.capture());
        assertEquals(
                Arrays.asList("外国直接投资 (FDI)", "外商独资企业 (WFOE)"),
                captor.getValue().getEnterpriseTypeIn());
    }

    @Test
    void list_expandsJvFilterToIncludeLegacyName()
    {
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseList(any())).thenReturn(Collections.emptyList());

        CrmTenantEnterprise query = new CrmTenantEnterprise();
        query.setEnterpriseType("合资企业 (JV)");

        service.selectCrmTenantEnterpriseList(query);

        ArgumentCaptor<CrmTenantEnterprise> captor = ArgumentCaptor.forClass(CrmTenantEnterprise.class);
        verify(crmTenantEnterpriseMapper).selectCrmTenantEnterpriseList(captor.capture());
        assertEquals(
                Arrays.asList("合资企业 (JV)", "中外合资企业 (JV)"),
                captor.getValue().getEnterpriseTypeIn());
    }

    @Test
    void insert_normalizesWfoeToFdiAndKeepsTaxCodeLegalRep()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmTenantEnterpriseMapper.insertCrmTenantEnterprise(any())).thenReturn(1);
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(anyString())).thenAnswer(inv -> {
            CrmTenantEnterprise e = validEnterprise();
            e.setId(inv.getArgument(0));
            e.setEnterpriseType("外国直接投资 (FDI)");
            e.setTaxCode("0123456789");
            e.setLegalRepresentative("Nguyen Van A");
            return e;
        });

        CrmTenantEnterprise row = validEnterprise();
        row.setEnterpriseType("外商独资企业 (WFOE)");
        row.setTaxCode(" 0123456789 ");
        row.setLegalRepresentative(" Nguyen Van A ");
        row.setStatus("已停止经营活动");

        service.insertCrmTenantEnterprise(row);

        ArgumentCaptor<CrmTenantEnterprise> captor = ArgumentCaptor.forClass(CrmTenantEnterprise.class);
        verify(crmTenantEnterpriseMapper).insertCrmTenantEnterprise(captor.capture());
        CrmTenantEnterprise inserted = captor.getValue();
        assertEquals("外国直接投资 (FDI)", inserted.getEnterpriseType());
        assertEquals("0123456789", inserted.getTaxCode());
        assertEquals("Nguyen Van A", inserted.getLegalRepresentative());
        assertEquals("已停止经营活动", inserted.getStatus());
    }

    @Test
    void insert_normalizesLegacyJvName()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmTenantEnterpriseMapper.insertCrmTenantEnterprise(any())).thenReturn(1);
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById(anyString())).thenAnswer(inv -> {
            CrmTenantEnterprise e = validEnterprise();
            e.setId(inv.getArgument(0));
            e.setEnterpriseType("合资企业 (JV)");
            return e;
        });

        CrmTenantEnterprise row = validEnterprise();
        row.setEnterpriseType("中外合资企业 (JV)");
        row.setStatus("已注销");

        service.insertCrmTenantEnterprise(row);

        ArgumentCaptor<CrmTenantEnterprise> captor = ArgumentCaptor.forClass(CrmTenantEnterprise.class);
        verify(crmTenantEnterpriseMapper).insertCrmTenantEnterprise(captor.capture());
        assertEquals("合资企业 (JV)", captor.getValue().getEnterpriseType());
        assertEquals("已注销", captor.getValue().getStatus());
    }

    @Test
    void insert_clientProvidedId_ok()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmTenantEnterpriseMapper.insertCrmTenantEnterprise(any())).thenReturn(1);
        when(crmTenantEnterpriseMapper.selectCrmTenantEnterpriseById("ent-custom"))
                .thenReturn(null)
                .thenReturn(enterprise("ent-custom"));

        CrmTenantEnterprise row = validEnterprise();
        row.setId("ent-custom");

        CrmTenantEnterprise saved = service.insertCrmTenantEnterprise(row);
        assertNotNull(saved);
        assertEquals("ent-custom", saved.getId());
    }

    @Test
    void normalizeEnterpriseType_mapsAliases()
    {
        assertEquals("外国直接投资 (FDI)", CrmTenantEnterpriseServiceImpl.normalizeEnterpriseType("FDI"));
        assertEquals("外国直接投资 (FDI)", CrmTenantEnterpriseServiceImpl.normalizeEnterpriseType("外商独资企业 (WFOE)"));
        assertEquals("合资企业 (JV)", CrmTenantEnterpriseServiceImpl.normalizeEnterpriseType("中外合资企业 (JV)"));
        assertEquals("越南国有企业 (SOE)", CrmTenantEnterpriseServiceImpl.normalizeEnterpriseType("越南国有企业 (SOE)"));
    }

    private static CrmTenantEnterprise validEnterprise()
    {
        CrmTenantEnterprise e = new CrmTenantEnterprise();
        e.setParkId("vn-ip-1");
        e.setParkNameCn("园区");
        e.setName("Samsung Electronics");
        e.setNameCn("三星电子");
        e.setEstablishedYear(2008);
        e.setRegisteredAddress("Bac Ninh");
        e.setIndustry("电子");
        e.setEnterpriseType("外国直接投资 (FDI)");
        e.setStatus("运营中");
        return e;
    }

    private static CrmTenantEnterprise enterprise(String id)
    {
        CrmTenantEnterprise e = validEnterprise();
        e.setId(id);
        return e;
    }
}
