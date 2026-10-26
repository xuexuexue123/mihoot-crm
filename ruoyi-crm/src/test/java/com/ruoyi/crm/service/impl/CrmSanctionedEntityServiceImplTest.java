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
import com.ruoyi.crm.domain.CrmSanctionedEntity;
import com.ruoyi.crm.domain.dto.SanctionedEntityBatchRequest;
import com.ruoyi.crm.domain.dto.SanctionedEntityBatchResult;
import com.ruoyi.crm.mapper.CrmSanctionedEntityMapper;

@ExtendWith(MockitoExtension.class)
class CrmSanctionedEntityServiceImplTest
{
    @Mock
    private CrmSanctionedEntityMapper crmSanctionedEntityMapper;

    @InjectMocks
    private CrmSanctionedEntityServiceImpl service;

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
        when(crmSanctionedEntityMapper.insertCrmSanctionedEntity(any())).thenReturn(1);
        when(crmSanctionedEntityMapper.selectCrmSanctionedEntityById(anyString())).thenAnswer(inv -> {
            CrmSanctionedEntity e = validEntity();
            e.setId(inv.getArgument(0));
            return e;
        });

        CrmSanctionedEntity row = validEntity();
        row.setId(null);

        CrmSanctionedEntity saved = service.insertCrmSanctionedEntity(row);

        ArgumentCaptor<CrmSanctionedEntity> captor = ArgumentCaptor.forClass(CrmSanctionedEntity.class);
        verify(crmSanctionedEntityMapper).insertCrmSanctionedEntity(captor.capture());
        CrmSanctionedEntity inserted = captor.getValue();
        assertTrue(inserted.getId().startsWith("sanc-"));
        assertEquals("0", inserted.getDelFlag());
        assertNotNull(saved);
        assertTrue(saved.getId().startsWith("sanc-"));
    }

    @Test
    void insert_nullAka_becomesEmptyList()
    {
        when(crmSanctionedEntityMapper.insertCrmSanctionedEntity(any())).thenReturn(1);
        when(crmSanctionedEntityMapper.selectCrmSanctionedEntityById(anyString())).thenAnswer(inv -> {
            CrmSanctionedEntity e = validEntity();
            e.setId(inv.getArgument(0));
            e.setAka(Collections.emptyList());
            return e;
        });

        CrmSanctionedEntity row = validEntity();
        row.setAka(null);

        service.insertCrmSanctionedEntity(row);

        ArgumentCaptor<CrmSanctionedEntity> captor = ArgumentCaptor.forClass(CrmSanctionedEntity.class);
        verify(crmSanctionedEntityMapper).insertCrmSanctionedEntity(captor.capture());
        assertNotNull(captor.getValue().getAka());
        assertTrue(captor.getValue().getAka().isEmpty());
    }

    @Test
    void insert_rejectsMissingNameCn()
    {
        CrmSanctionedEntity row = validEntity();
        row.setNameCn(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmSanctionedEntity(row));
        verify(crmSanctionedEntityMapper, never()).insertCrmSanctionedEntity(any());
    }

    @Test
    void insert_rejectsMissingRiskLevel()
    {
        CrmSanctionedEntity row = validEntity();
        row.setRiskLevel("");

        assertThrows(ServiceException.class, () -> service.insertCrmSanctionedEntity(row));
        verify(crmSanctionedEntityMapper, never()).insertCrmSanctionedEntity(any());
    }

    @Test
    void insert_rejectsMissingEffectiveDate()
    {
        CrmSanctionedEntity row = validEntity();
        row.setEffectiveDate(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmSanctionedEntity(row));
        verify(crmSanctionedEntityMapper, never()).insertCrmSanctionedEntity(any());
    }

    @Test
    void update_rejectsMissing()
    {
        when(crmSanctionedEntityMapper.selectCrmSanctionedEntityById("x")).thenReturn(null);

        CrmSanctionedEntity row = validEntity();
        row.setId("x");

        assertThrows(ServiceException.class, () -> service.updateCrmSanctionedEntity(row));
        verify(crmSanctionedEntityMapper, never()).updateCrmSanctionedEntity(any());
    }

    @Test
    void delete_softDeletes()
    {
        when(crmSanctionedEntityMapper.logicalDeleteById(eq("s1"), eq("admin"))).thenReturn(1);

        int rows = service.deleteCrmSanctionedEntityById("s1");
        assertEquals(1, rows);
        verify(crmSanctionedEntityMapper).logicalDeleteById("s1", "admin");
    }

    @Test
    void batch_upsert_insertsAndUpdates()
    {
        CrmSanctionedEntity existing = validEntity();
        existing.setId("sanc-existing");

        when(crmSanctionedEntityMapper.selectCrmSanctionedEntityById("sanc-existing")).thenReturn(existing);
        when(crmSanctionedEntityMapper.selectCrmSanctionedEntityById("sanc-new")).thenReturn(null);
        when(crmSanctionedEntityMapper.insertCrmSanctionedEntity(any())).thenReturn(1);
        when(crmSanctionedEntityMapper.updateCrmSanctionedEntity(any())).thenReturn(1);

        CrmSanctionedEntity updateRow = validEntity();
        updateRow.setId("sanc-existing");
        updateRow.setNameCn("新名");

        CrmSanctionedEntity insertRow = validEntity();
        insertRow.setId("sanc-new");
        insertRow.setNameCn("新主体");

        SanctionedEntityBatchRequest req = new SanctionedEntityBatchRequest();
        req.setMode("upsert");
        req.setRows(Arrays.asList(updateRow, insertRow));

        SanctionedEntityBatchResult result = service.batch(req);
        assertEquals(1, result.getInserted());
        assertEquals(1, result.getUpdated());
        assertEquals(0, result.getSkipped());
        verify(crmSanctionedEntityMapper, times(1)).updateCrmSanctionedEntity(any());
        verify(crmSanctionedEntityMapper, times(1)).insertCrmSanctionedEntity(any());
    }

    @Test
    void batch_insert_skipsConflict()
    {
        CrmSanctionedEntity existing = validEntity();
        existing.setId("sanc-existing");
        when(crmSanctionedEntityMapper.selectCrmSanctionedEntityById("sanc-existing")).thenReturn(existing);

        CrmSanctionedEntity row = validEntity();
        row.setId("sanc-existing");

        SanctionedEntityBatchRequest req = new SanctionedEntityBatchRequest();
        req.setMode("insert");
        req.setRows(Collections.singletonList(row));

        SanctionedEntityBatchResult result = service.batch(req);
        assertEquals(0, result.getInserted());
        assertEquals(0, result.getUpdated());
        assertEquals(1, result.getSkipped());
        verify(crmSanctionedEntityMapper, never()).insertCrmSanctionedEntity(any());
        verify(crmSanctionedEntityMapper, never()).updateCrmSanctionedEntity(any());
    }

    @Test
    void batch_rejectsInvalidMode()
    {
        SanctionedEntityBatchRequest req = new SanctionedEntityBatchRequest();
        req.setMode("merge");
        req.setRows(Collections.singletonList(validEntity()));

        assertThrows(ServiceException.class, () -> service.batch(req));
    }

    @Test
    void selectById_blankReturnsNull()
    {
        assertNull(service.selectCrmSanctionedEntityById("  "));
        verify(crmSanctionedEntityMapper, never()).selectCrmSanctionedEntityById(anyString());
    }

    @Test
    void list_normalizesAllFilters()
    {
        when(crmSanctionedEntityMapper.selectCrmSanctionedEntityList(any())).thenReturn(Collections.emptyList());

        CrmSanctionedEntity query = new CrmSanctionedEntity();
        query.setRiskCategory("ALL");
        query.setRiskLevel("ALL");
        query.setEntityType("ALL");
        query.setSearchQuery("  OFAC  ");

        service.selectCrmSanctionedEntityList(query);

        ArgumentCaptor<CrmSanctionedEntity> captor = ArgumentCaptor.forClass(CrmSanctionedEntity.class);
        verify(crmSanctionedEntityMapper).selectCrmSanctionedEntityList(captor.capture());
        CrmSanctionedEntity normalized = captor.getValue();
        assertNull(normalized.getRiskCategory());
        assertNull(normalized.getRiskLevel());
        assertNull(normalized.getEntityType());
        assertEquals("OFAC", normalized.getSearchQuery());
    }

    @Test
    void insert_clientProvidedId_ok()
    {
        when(crmSanctionedEntityMapper.insertCrmSanctionedEntity(any())).thenReturn(1);
        when(crmSanctionedEntityMapper.selectCrmSanctionedEntityById("SANC-VN-001"))
                .thenReturn(null)
                .thenReturn(entity("SANC-VN-001"));

        CrmSanctionedEntity row = validEntity();
        row.setId("SANC-VN-001");

        CrmSanctionedEntity saved = service.insertCrmSanctionedEntity(row);
        assertNotNull(saved);
        assertEquals("SANC-VN-001", saved.getId());
    }

    private static CrmSanctionedEntity validEntity()
    {
        CrmSanctionedEntity e = new CrmSanctionedEntity();
        e.setNameCn("示例制裁主体");
        e.setNameVn("Doanh nghiep mau");
        e.setNameEn("Sample Entity");
        e.setAka(Arrays.asList("Alias A"));
        e.setEntityType("企业");
        e.setRiskCategory("美国OFAC/EU/UN全球制裁与UFLPA");
        e.setRiskReason("列入 SDN 名单");
        e.setIssuingAgency("OFAC");
        e.setRiskLevel("红灯 (高危)");
        e.setEffectiveDate("2024-01-01");
        return e;
    }

    private static CrmSanctionedEntity entity(String id)
    {
        CrmSanctionedEntity e = validEntity();
        e.setId(id);
        return e;
    }
}
