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
import com.ruoyi.crm.domain.CrmSanctionSourceTag;
import com.ruoyi.crm.domain.dto.SanctionSourceTagBatchRequest;
import com.ruoyi.crm.domain.dto.SanctionSourceTagBatchResult;
import com.ruoyi.crm.mapper.CrmSanctionSourceTagMapper;

@ExtendWith(MockitoExtension.class)
class CrmSanctionSourceTagServiceImplTest
{
    @Mock
    private CrmSanctionSourceTagMapper crmSanctionSourceTagMapper;

    @InjectMocks
    private CrmSanctionSourceTagServiceImpl service;

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
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagByTagCode("OFAC-SDN")).thenReturn(null);
        when(crmSanctionSourceTagMapper.insertCrmSanctionSourceTag(any())).thenReturn(1);
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById(anyString())).thenAnswer(inv -> {
            CrmSanctionSourceTag e = validTag();
            e.setId(inv.getArgument(0));
            return e;
        });

        CrmSanctionSourceTag row = validTag();
        row.setId(null);

        CrmSanctionSourceTag saved = service.insertCrmSanctionSourceTag(row);

        ArgumentCaptor<CrmSanctionSourceTag> captor = ArgumentCaptor.forClass(CrmSanctionSourceTag.class);
        verify(crmSanctionSourceTagMapper).insertCrmSanctionSourceTag(captor.capture());
        CrmSanctionSourceTag inserted = captor.getValue();
        assertTrue(inserted.getId().startsWith("tag-"));
        assertEquals("0", inserted.getDelFlag());
        assertNotNull(saved);
        assertTrue(saved.getId().startsWith("tag-"));
    }

    @Test
    void insert_rejectsDuplicateTagCode()
    {
        CrmSanctionSourceTag existing = validTag();
        existing.setId("tag-other");
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagByTagCode("OFAC-SDN")).thenReturn(existing);

        CrmSanctionSourceTag row = validTag();
        row.setId(null);

        assertThrows(ServiceException.class, () -> service.insertCrmSanctionSourceTag(row));
        verify(crmSanctionSourceTagMapper, never()).insertCrmSanctionSourceTag(any());
    }

    @Test
    void insert_rejectsMissingTagCode()
    {
        CrmSanctionSourceTag row = validTag();
        row.setTagCode(" ");

        assertThrows(ServiceException.class, () -> service.insertCrmSanctionSourceTag(row));
        verify(crmSanctionSourceTagMapper, never()).insertCrmSanctionSourceTag(any());
    }

    @Test
    void update_allowsSameTagCode()
    {
        CrmSanctionSourceTag existing = validTag();
        existing.setId("TAG-001");
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById("TAG-001")).thenReturn(existing);
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagByTagCode("OFAC-SDN")).thenReturn(existing);
        when(crmSanctionSourceTagMapper.updateCrmSanctionSourceTag(any())).thenReturn(1);

        CrmSanctionSourceTag row = validTag();
        row.setId("TAG-001");
        row.setTagName("更新名称");

        service.updateCrmSanctionSourceTag(row);
        verify(crmSanctionSourceTagMapper).updateCrmSanctionSourceTag(any());
    }

    @Test
    void update_rejectsDuplicateTagCode()
    {
        CrmSanctionSourceTag existing = validTag();
        existing.setId("TAG-001");
        CrmSanctionSourceTag other = validTag();
        other.setId("TAG-002");
        other.setTagCode("EU-SAN");

        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById("TAG-001")).thenReturn(existing);
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagByTagCode("EU-SAN")).thenReturn(other);

        CrmSanctionSourceTag row = validTag();
        row.setId("TAG-001");
        row.setTagCode("EU-SAN");

        assertThrows(ServiceException.class, () -> service.updateCrmSanctionSourceTag(row));
        verify(crmSanctionSourceTagMapper, never()).updateCrmSanctionSourceTag(any());
    }

    @Test
    void delete_softDeletes()
    {
        when(crmSanctionSourceTagMapper.logicalDeleteById(eq("t1"), eq("admin"))).thenReturn(1);

        int rows = service.deleteCrmSanctionSourceTagById("t1");
        assertEquals(1, rows);
        verify(crmSanctionSourceTagMapper).logicalDeleteById("t1", "admin");
    }

    @Test
    void batch_upsert_insertsAndUpdates()
    {
        CrmSanctionSourceTag existing = validTag();
        existing.setId("tag-existing");

        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById("tag-existing")).thenReturn(existing);
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById("tag-new")).thenReturn(null);
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagByTagCode("OFAC-SDN")).thenReturn(existing);
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagByTagCode("EU-SAN")).thenReturn(null);
        when(crmSanctionSourceTagMapper.insertCrmSanctionSourceTag(any())).thenReturn(1);
        when(crmSanctionSourceTagMapper.updateCrmSanctionSourceTag(any())).thenReturn(1);

        CrmSanctionSourceTag updateRow = validTag();
        updateRow.setId("tag-existing");
        updateRow.setTagName("更新");

        CrmSanctionSourceTag insertRow = validTag();
        insertRow.setId("tag-new");
        insertRow.setTagCode("EU-SAN");
        insertRow.setTagName("EU Sanctions");

        SanctionSourceTagBatchRequest req = new SanctionSourceTagBatchRequest();
        req.setMode("upsert");
        req.setRows(Arrays.asList(updateRow, insertRow));

        SanctionSourceTagBatchResult result = service.batch(req);
        assertEquals(1, result.getInserted());
        assertEquals(1, result.getUpdated());
        assertEquals(0, result.getSkipped());
        verify(crmSanctionSourceTagMapper, times(1)).updateCrmSanctionSourceTag(any());
        verify(crmSanctionSourceTagMapper, times(1)).insertCrmSanctionSourceTag(any());
    }

    @Test
    void batch_insert_skipsConflict()
    {
        CrmSanctionSourceTag existing = validTag();
        existing.setId("tag-existing");
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById("tag-existing")).thenReturn(existing);

        CrmSanctionSourceTag row = validTag();
        row.setId("tag-existing");

        SanctionSourceTagBatchRequest req = new SanctionSourceTagBatchRequest();
        req.setMode("insert");
        req.setRows(Collections.singletonList(row));

        SanctionSourceTagBatchResult result = service.batch(req);
        assertEquals(0, result.getInserted());
        assertEquals(0, result.getUpdated());
        assertEquals(1, result.getSkipped());
        verify(crmSanctionSourceTagMapper, never()).insertCrmSanctionSourceTag(any());
        verify(crmSanctionSourceTagMapper, never()).updateCrmSanctionSourceTag(any());
    }

    @Test
    void selectById_blankReturnsNull()
    {
        assertNull(service.selectCrmSanctionSourceTagById("  "));
        verify(crmSanctionSourceTagMapper, never()).selectCrmSanctionSourceTagById(anyString());
    }

    @Test
    void list_trimsSearchQuery()
    {
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagList(any())).thenReturn(Collections.emptyList());

        CrmSanctionSourceTag query = new CrmSanctionSourceTag();
        query.setSearchQuery("  OFAC  ");

        service.selectCrmSanctionSourceTagList(query);

        ArgumentCaptor<CrmSanctionSourceTag> captor = ArgumentCaptor.forClass(CrmSanctionSourceTag.class);
        verify(crmSanctionSourceTagMapper).selectCrmSanctionSourceTagList(captor.capture());
        assertEquals("OFAC", captor.getValue().getSearchQuery());
    }

    @Test
    void insert_clientProvidedId_ok()
    {
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagByTagCode("OFAC-SDN")).thenReturn(null);
        when(crmSanctionSourceTagMapper.insertCrmSanctionSourceTag(any())).thenReturn(1);
        when(crmSanctionSourceTagMapper.selectCrmSanctionSourceTagById("TAG-001"))
                .thenReturn(null)
                .thenReturn(tag("TAG-001"));

        CrmSanctionSourceTag row = validTag();
        row.setId("TAG-001");

        CrmSanctionSourceTag saved = service.insertCrmSanctionSourceTag(row);
        assertNotNull(saved);
        assertEquals("TAG-001", saved.getId());
    }

    private static CrmSanctionSourceTag validTag()
    {
        CrmSanctionSourceTag e = new CrmSanctionSourceTag();
        e.setTagCode("OFAC-SDN");
        e.setTagName("OFAC SDN List");
        e.setAuthority("U.S. Treasury OFAC");
        e.setDescription("Specially Designated Nationals");
        return e;
    }

    private static CrmSanctionSourceTag tag(String id)
    {
        CrmSanctionSourceTag e = validTag();
        e.setId(id);
        return e;
    }
}
