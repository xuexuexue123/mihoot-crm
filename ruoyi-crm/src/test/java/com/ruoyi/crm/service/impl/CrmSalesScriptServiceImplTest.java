package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.ruoyi.crm.domain.CrmSalesScript;
import com.ruoyi.crm.domain.CrmWecomMaterial;
import com.ruoyi.crm.mapper.CrmSalesScriptMapper;
import com.ruoyi.crm.mapper.CrmWecomMaterialMapper;

@ExtendWith(MockitoExtension.class)
class CrmSalesScriptServiceImplTest
{
    @Mock
    private CrmSalesScriptMapper crmSalesScriptMapper;

    @Mock
    private CrmWecomMaterialMapper crmWecomMaterialMapper;

    @InjectMocks
    private CrmSalesScriptServiceImpl service;

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
        when(crmSalesScriptMapper.insertCrmSalesScript(any())).thenReturn(1);

        CrmSalesScript row = new CrmSalesScript();
        row.setScriptType("marketing");
        row.setTitle("开场白");
        row.setContent("您好");

        service.insertCrmSalesScript(row);

        ArgumentCaptor<CrmSalesScript> captor = ArgumentCaptor.forClass(CrmSalesScript.class);
        verify(crmSalesScriptMapper).insertCrmSalesScript(captor.capture());
        CrmSalesScript saved = captor.getValue();
        assertEquals("marketing", saved.getScriptType());
        assertEquals("0", saved.getStatus());
        assertEquals(Integer.valueOf(0), saved.getSortOrder());
        assertEquals("0", saved.getDelFlag());
        org.junit.jupiter.api.Assertions.assertNotNull(saved.getScriptId());
    }

    @Test
    void insert_rejectsInvalidScriptType()
    {
        CrmSalesScript row = new CrmSalesScript();
        row.setScriptType("other");
        row.setTitle("t");
        row.setContent("c");

        assertThrows(ServiceException.class, () -> service.insertCrmSalesScript(row));
        verify(crmSalesScriptMapper, never()).insertCrmSalesScript(any());
    }

    @Test
    void insert_rejectsMissingMaterial()
    {
        when(crmWecomMaterialMapper.selectById(anyLong())).thenReturn(null);

        CrmSalesScript row = new CrmSalesScript();
        row.setScriptType("qa");
        row.setTitle("价格?");
        row.setContent("看方案");
        row.setMaterialId(999L);

        assertThrows(ServiceException.class, () -> service.insertCrmSalesScript(row));
        verify(crmSalesScriptMapper, never()).insertCrmSalesScript(any());
    }

    @Test
    void insert_acceptsExistingMaterial()
    {
        when(crmWecomMaterialMapper.selectById(123L)).thenReturn(new CrmWecomMaterial());
        when(crmSalesScriptMapper.insertCrmSalesScript(any())).thenReturn(1);

        CrmSalesScript row = new CrmSalesScript();
        row.setScriptType("sop");
        row.setTitle("术后");
        row.setContent("第一步\n第二步");
        row.setMaterialId(123L);

        service.insertCrmSalesScript(row);
        verify(crmSalesScriptMapper).insertCrmSalesScript(any());
    }

    @Test
    void update_rejectsMissingRow()
    {
        when(crmSalesScriptMapper.selectCrmSalesScriptByScriptId("s1")).thenReturn(null);

        CrmSalesScript row = new CrmSalesScript();
        row.setScriptId("s1");
        row.setScriptType("marketing");
        row.setTitle("t");
        row.setContent("c");

        assertThrows(ServiceException.class, () -> service.updateCrmSalesScript(row));
    }

    @Test
    void delete_logicalDelete()
    {
        when(crmSalesScriptMapper.logicalDeleteByScriptId("s1", "admin")).thenReturn(1);
        assertEquals(1, service.deleteCrmSalesScriptByScriptId("s1"));
        verify(crmSalesScriptMapper).logicalDeleteByScriptId("s1", "admin");
    }
}
