package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.ruoyi.crm.domain.dto.IndustrialParkBatchRequest;
import com.ruoyi.crm.domain.dto.IndustrialParkBatchResult;
import com.ruoyi.crm.mapper.CrmIndustrialParkMapper;

@ExtendWith(MockitoExtension.class)
class CrmIndustrialParkServiceImplTest
{
    @Mock
    private CrmIndustrialParkMapper crmIndustrialParkMapper;

    @InjectMocks
    private CrmIndustrialParkServiceImpl service;

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
    void insert_generatesIdAndDefaults()
    {
        when(crmIndustrialParkMapper.insertCrmIndustrialPark(any())).thenReturn(1);
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById(anyString())).thenAnswer(inv -> {
            CrmIndustrialPark p = new CrmIndustrialPark();
            p.setId(inv.getArgument(0));
            p.setNameCn("北宁工业园");
            p.setRegion("北部");
            return p;
        });

        CrmIndustrialPark row = new CrmIndustrialPark();
        row.setNameCn("北宁工业园");
        row.setRegion("北部");

        CrmIndustrialPark saved = service.insertCrmIndustrialPark(row);

        ArgumentCaptor<CrmIndustrialPark> captor = ArgumentCaptor.forClass(CrmIndustrialPark.class);
        verify(crmIndustrialParkMapper).insertCrmIndustrialPark(captor.capture());
        CrmIndustrialPark inserted = captor.getValue();
        assertTrue(inserted.getId().startsWith("vn-ip-"));
        assertEquals("0", inserted.getDelFlag());
        assertEquals("未核实", inserted.getVerificationStatus());
        assertNotNull(inserted.getEncouragedIndustries());
        assertTrue(inserted.getEncouragedIndustries().isEmpty());
        assertEquals(Boolean.FALSE, inserted.getHasNationalGrid());
        assertEquals(Boolean.FALSE, inserted.getHasStandbyPower());
        assertEquals("北宁工业园", saved.getNameCn());
    }

    @Test
    void insert_rejectsMissingNameCn()
    {
        CrmIndustrialPark row = new CrmIndustrialPark();
        row.setRegion("北部");

        assertThrows(ServiceException.class, () -> service.insertCrmIndustrialPark(row));
        verify(crmIndustrialParkMapper, never()).insertCrmIndustrialPark(any());
    }

    @Test
    void update_rejectsMissing()
    {
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById("x")).thenReturn(null);

        CrmIndustrialPark row = new CrmIndustrialPark();
        row.setId("x");
        row.setNameCn("园区");
        row.setRegion("南部");

        assertThrows(ServiceException.class, () -> service.updateCrmIndustrialPark(row));
        verify(crmIndustrialParkMapper, never()).updateCrmIndustrialPark(any());
    }

    @Test
    void delete_softDeletes()
    {
        when(crmIndustrialParkMapper.logicalDeleteById(eq("p1"), eq("admin"))).thenReturn(1);

        int rows = service.deleteCrmIndustrialParkById("p1");
        assertEquals(1, rows);
        verify(crmIndustrialParkMapper).logicalDeleteById("p1", "admin");
    }

    @Test
    void batch_upsert_insertsAndUpdates()
    {
        CrmIndustrialPark existing = new CrmIndustrialPark();
        existing.setId("vn-ip-existing");
        existing.setNameCn("旧名");
        existing.setRegion("北部");

        when(crmIndustrialParkMapper.selectCrmIndustrialParkById("vn-ip-existing")).thenReturn(existing);
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById("vn-ip-new")).thenReturn(null);
        when(crmIndustrialParkMapper.insertCrmIndustrialPark(any())).thenReturn(1);
        when(crmIndustrialParkMapper.updateCrmIndustrialPark(any())).thenReturn(1);

        CrmIndustrialPark updateRow = new CrmIndustrialPark();
        updateRow.setId("vn-ip-existing");
        updateRow.setNameCn("新名");
        updateRow.setRegion("北部");

        CrmIndustrialPark insertRow = new CrmIndustrialPark();
        insertRow.setId("vn-ip-new");
        insertRow.setNameCn("新园");
        insertRow.setRegion("南部");

        IndustrialParkBatchRequest req = new IndustrialParkBatchRequest();
        req.setMode("upsert");
        req.setRows(Arrays.asList(updateRow, insertRow));

        IndustrialParkBatchResult result = service.batch(req);
        assertEquals(1, result.getInserted());
        assertEquals(1, result.getUpdated());
        assertEquals(0, result.getSkipped());
        verify(crmIndustrialParkMapper, times(1)).updateCrmIndustrialPark(any());
        verify(crmIndustrialParkMapper, times(1)).insertCrmIndustrialPark(any());
    }

    @Test
    void batch_insert_skipsConflict()
    {
        CrmIndustrialPark existing = new CrmIndustrialPark();
        existing.setId("vn-ip-existing");
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById("vn-ip-existing")).thenReturn(existing);

        CrmIndustrialPark row = new CrmIndustrialPark();
        row.setId("vn-ip-existing");
        row.setNameCn("冲突");
        row.setRegion("中部");

        IndustrialParkBatchRequest req = new IndustrialParkBatchRequest();
        req.setMode("insert");
        req.setRows(Collections.singletonList(row));

        IndustrialParkBatchResult result = service.batch(req);
        assertEquals(0, result.getInserted());
        assertEquals(0, result.getUpdated());
        assertEquals(1, result.getSkipped());
        verify(crmIndustrialParkMapper, never()).insertCrmIndustrialPark(any());
        verify(crmIndustrialParkMapper, never()).updateCrmIndustrialPark(any());
    }

    @Test
    void batch_rejectsInvalidMode()
    {
        IndustrialParkBatchRequest req = new IndustrialParkBatchRequest();
        req.setMode("merge");
        CrmIndustrialPark row = new CrmIndustrialPark();
        row.setNameCn("a");
        row.setRegion("北部");
        req.setRows(Collections.singletonList(row));

        assertThrows(ServiceException.class, () -> service.batch(req));
    }

    @Test
    void selectById_blankReturnsNull()
    {
        assertEquals(null, service.selectCrmIndustrialParkById("  "));
        verify(crmIndustrialParkMapper, never()).selectCrmIndustrialParkById(anyString());
    }

    @Test
    void insert_clientProvidedId_ok()
    {
        when(crmIndustrialParkMapper.insertCrmIndustrialPark(any())).thenReturn(1);
        when(crmIndustrialParkMapper.selectCrmIndustrialParkById("vn-ip-custom"))
                .thenReturn(null)
                .thenReturn(park("vn-ip-custom"));

        CrmIndustrialPark row = new CrmIndustrialPark();
        row.setId("vn-ip-custom");
        row.setNameCn("自定义");
        row.setRegion("中部");

        CrmIndustrialPark saved = service.insertCrmIndustrialPark(row);
        assertNotNull(saved);
        assertEquals("vn-ip-custom", saved.getId());
    }

    private static CrmIndustrialPark park(String id)
    {
        CrmIndustrialPark p = new CrmIndustrialPark();
        p.setId(id);
        p.setNameCn("自定义");
        p.setRegion("中部");
        return p;
    }
}
