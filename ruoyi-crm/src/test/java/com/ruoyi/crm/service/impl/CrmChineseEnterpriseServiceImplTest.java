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
import com.ruoyi.crm.domain.CrmChineseEnterprise;
import com.ruoyi.crm.domain.CrmIndustrialPark;
import com.ruoyi.crm.domain.dto.ChineseEnterpriseBatchRequest;
import com.ruoyi.crm.domain.dto.ChineseEnterpriseBatchResult;
import com.ruoyi.crm.mapper.CrmChineseEnterpriseMapper;
import com.ruoyi.crm.mapper.CrmIndustrialParkMapper;

@ExtendWith(MockitoExtension.class)
class CrmChineseEnterpriseServiceImplTest
{
    @Mock
    private CrmChineseEnterpriseMapper crmChineseEnterpriseMapper;

    @Mock
    private CrmIndustrialParkMapper crmIndustrialParkMapper;

    @InjectMocks
    private CrmChineseEnterpriseServiceImpl service;

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
        when(crmChineseEnterpriseMapper.insertCrmChineseEnterprise(any())).thenReturn(1);
        when(crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById(anyString())).thenAnswer(inv -> {
            CrmChineseEnterprise e = validEnterprise();
            e.setId(inv.getArgument(0));
            e.setParkNameCn("北宁工业园");
            return e;
        });

        CrmChineseEnterprise row = validEnterprise();
        row.setParkNameCn("前端传入名");

        CrmChineseEnterprise saved = service.insertCrmChineseEnterprise(row);

        ArgumentCaptor<CrmChineseEnterprise> captor = ArgumentCaptor.forClass(CrmChineseEnterprise.class);
        verify(crmChineseEnterpriseMapper).insertCrmChineseEnterprise(captor.capture());
        CrmChineseEnterprise inserted = captor.getValue();
        assertTrue(inserted.getId().startsWith("cn-ent-"));
        assertEquals("0", inserted.getDelFlag());
        assertEquals("北宁工业园", inserted.getParkNameCn());
        assertEquals("北宁工业园", saved.getParkNameCn());
    }

    @Test
    void insert_keepsParkNameWhenParkMissing()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById("vn-ip-missing")).thenReturn(null);
        when(crmChineseEnterpriseMapper.insertCrmChineseEnterprise(any())).thenReturn(1);
        when(crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById(anyString())).thenAnswer(inv -> {
            CrmChineseEnterprise e = validEnterprise();
            e.setId(inv.getArgument(0));
            e.setParkId("vn-ip-missing");
            e.setParkNameCn("前端园区名");
            return e;
        });

        CrmChineseEnterprise row = validEnterprise();
        row.setParkId("vn-ip-missing");
        row.setParkNameCn("前端园区名");

        service.insertCrmChineseEnterprise(row);

        ArgumentCaptor<CrmChineseEnterprise> captor = ArgumentCaptor.forClass(CrmChineseEnterprise.class);
        verify(crmChineseEnterpriseMapper).insertCrmChineseEnterprise(captor.capture());
        assertEquals("前端园区名", captor.getValue().getParkNameCn());
    }

    @Test
    void insert_defaultsStatusWhenBlank()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmChineseEnterpriseMapper.insertCrmChineseEnterprise(any())).thenReturn(1);
        when(crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById(anyString())).thenAnswer(inv -> {
            CrmChineseEnterprise e = validEnterprise();
            e.setId(inv.getArgument(0));
            e.setStatus("运营中");
            return e;
        });

        CrmChineseEnterprise row = validEnterprise();
        row.setStatus("  ");

        service.insertCrmChineseEnterprise(row);

        ArgumentCaptor<CrmChineseEnterprise> captor = ArgumentCaptor.forClass(CrmChineseEnterprise.class);
        verify(crmChineseEnterpriseMapper).insertCrmChineseEnterprise(captor.capture());
        assertEquals("运营中", captor.getValue().getStatus());
    }

    @Test
    void insert_rejectsMissingParkId()
    {
        CrmChineseEnterprise row = validEnterprise();
        row.setParkId("");

        assertThrows(ServiceException.class, () -> service.insertCrmChineseEnterprise(row));
        verify(crmChineseEnterpriseMapper, never()).insertCrmChineseEnterprise(any());
    }

    @Test
    void insert_rejectsMissingVietnamEntityNameCn()
    {
        CrmChineseEnterprise row = validEnterprise();
        row.setVietnamEntityNameCn(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmChineseEnterprise(row));
        verify(crmChineseEnterpriseMapper, never()).insertCrmChineseEnterprise(any());
    }

    @Test
    void update_rejectsMissing()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById("x")).thenReturn(null);

        CrmChineseEnterprise row = validEnterprise();
        row.setId("x");

        assertThrows(ServiceException.class, () -> service.updateCrmChineseEnterprise(row));
        verify(crmChineseEnterpriseMapper, never()).updateCrmChineseEnterprise(any());
    }

    @Test
    void delete_softDeletes()
    {
        when(crmChineseEnterpriseMapper.logicalDeleteById(eq("e1"), eq("admin"))).thenReturn(1);

        int rows = service.deleteCrmChineseEnterpriseById("e1");
        assertEquals(1, rows);
        verify(crmChineseEnterpriseMapper).logicalDeleteById("e1", "admin");
    }

    @Test
    void batch_upsert_insertsAndUpdates()
    {
        CrmChineseEnterprise existing = validEnterprise();
        existing.setId("cn-ent-existing");

        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById("cn-ent-existing")).thenReturn(existing);
        when(crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById("cn-ent-new")).thenReturn(null);
        when(crmChineseEnterpriseMapper.insertCrmChineseEnterprise(any())).thenReturn(1);
        when(crmChineseEnterpriseMapper.updateCrmChineseEnterprise(any())).thenReturn(1);

        CrmChineseEnterprise updateRow = validEnterprise();
        updateRow.setId("cn-ent-existing");
        updateRow.setVietnamEntityNameCn("新名");

        CrmChineseEnterprise insertRow = validEnterprise();
        insertRow.setId("cn-ent-new");
        insertRow.setVietnamEntityNameCn("新企业");

        ChineseEnterpriseBatchRequest req = new ChineseEnterpriseBatchRequest();
        req.setMode("upsert");
        req.setRows(Arrays.asList(updateRow, insertRow));

        ChineseEnterpriseBatchResult result = service.batch(req);
        assertEquals(1, result.getInserted());
        assertEquals(1, result.getUpdated());
        assertEquals(0, result.getSkipped());
        verify(crmChineseEnterpriseMapper, times(1)).updateCrmChineseEnterprise(any());
        verify(crmChineseEnterpriseMapper, times(1)).insertCrmChineseEnterprise(any());
    }

    @Test
    void batch_insert_skipsConflict()
    {
        CrmChineseEnterprise existing = validEnterprise();
        existing.setId("cn-ent-existing");
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById("cn-ent-existing")).thenReturn(existing);

        CrmChineseEnterprise row = validEnterprise();
        row.setId("cn-ent-existing");

        ChineseEnterpriseBatchRequest req = new ChineseEnterpriseBatchRequest();
        req.setMode("insert");
        req.setRows(Collections.singletonList(row));

        ChineseEnterpriseBatchResult result = service.batch(req);
        assertEquals(0, result.getInserted());
        assertEquals(0, result.getUpdated());
        assertEquals(1, result.getSkipped());
        verify(crmChineseEnterpriseMapper, never()).insertCrmChineseEnterprise(any());
        verify(crmChineseEnterpriseMapper, never()).updateCrmChineseEnterprise(any());
    }

    @Test
    void batch_rejectsInvalidMode()
    {
        ChineseEnterpriseBatchRequest req = new ChineseEnterpriseBatchRequest();
        req.setMode("merge");
        req.setRows(Collections.singletonList(validEnterprise()));

        assertThrows(ServiceException.class, () -> service.batch(req));
    }

    @Test
    void selectById_blankReturnsNull()
    {
        assertNull(service.selectCrmChineseEnterpriseById("  "));
        verify(crmChineseEnterpriseMapper, never()).selectCrmChineseEnterpriseById(anyString());
    }

    @Test
    void list_normalizesAllFilters()
    {
        when(crmChineseEnterpriseMapper.selectCrmChineseEnterpriseList(any())).thenReturn(Collections.emptyList());

        CrmChineseEnterprise query = new CrmChineseEnterprise();
        query.setParkId("ALL");
        query.setProvinceVietnam("ALL");
        query.setParentProvinceChina("ALL");
        query.setIndustry("ALL");
        query.setStockListingStatus("ALL");
        query.setInvestmentMode("ALL");
        query.setStatus("ALL");
        query.setSearchQuery("  立讯  ");

        service.selectCrmChineseEnterpriseList(query);

        ArgumentCaptor<CrmChineseEnterprise> captor = ArgumentCaptor.forClass(CrmChineseEnterprise.class);
        verify(crmChineseEnterpriseMapper).selectCrmChineseEnterpriseList(captor.capture());
        CrmChineseEnterprise normalized = captor.getValue();
        assertNull(normalized.getParkId());
        assertNull(normalized.getProvinceVietnam());
        assertNull(normalized.getParentProvinceChina());
        assertNull(normalized.getIndustry());
        assertNull(normalized.getStockListingStatus());
        assertNull(normalized.getInvestmentMode());
        assertNull(normalized.getStatus());
        assertEquals("立讯", normalized.getSearchQuery());
    }

    @Test
    void insert_clientProvidedId_ok()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenReturn(null);
        when(crmChineseEnterpriseMapper.insertCrmChineseEnterprise(any())).thenReturn(1);
        when(crmChineseEnterpriseMapper.selectCrmChineseEnterpriseById("cn-ent-custom"))
                .thenReturn(null)
                .thenReturn(enterprise("cn-ent-custom"));

        CrmChineseEnterprise row = validEnterprise();
        row.setId("cn-ent-custom");

        CrmChineseEnterprise saved = service.insertCrmChineseEnterprise(row);
        assertNotNull(saved);
        assertEquals("cn-ent-custom", saved.getId());
    }

    private static CrmChineseEnterprise validEnterprise()
    {
        CrmChineseEnterprise e = new CrmChineseEnterprise();
        e.setParentCompanyChina("立讯精密");
        e.setParentProvinceChina("广东");
        e.setVietnamEntityName("Luxshare ICT Vietnam");
        e.setVietnamEntityNameCn("立讯越南");
        e.setParkId("vn-ip-1");
        e.setParkNameCn("园区");
        e.setProvinceVietnam("北宁");
        e.setIndustry("电子");
        e.setStockListingStatus("A股上市");
        e.setInvestmentMode("独资自建厂房");
        e.setStatus("运营中");
        return e;
    }

    private static CrmChineseEnterprise enterprise(String id)
    {
        CrmChineseEnterprise e = validEnterprise();
        e.setId(id);
        return e;
    }
}
