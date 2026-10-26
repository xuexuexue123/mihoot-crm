package com.ruoyi.crm.service.wecom.contactgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarGroupResolveResultDto;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupProvisionTaskMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderMapper;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@ExtendWith(MockitoExtension.class)
class WecomContactGroupByChatTest
{
    private static final String TENANT = "tenant3";
    private static final String CHAT_ID = "wrXXXXXXXX";

    @Mock
    private CrmWecomContactGroupMapper contactGroupMapper;

    @Mock
    private CrmWecomGroupProvisionTaskMapper provisionTaskMapper;

    @Mock
    private CrmWecomGroupReminderMapper reminderMapper;

    @Mock
    private CrmContactMapper crmContactMapper;

    @InjectMocks
    private WecomContactGroupService contactGroupService;

    @BeforeEach
    void bindTenant()
    {
        TenantDataSourceSupport.bindTenantKey(TENANT);
    }

    @AfterEach
    void clearTenant()
    {
        DynamicDataSourceContextHolder.clearDataSourceKey();
    }

    @Test
    void byChat_returnsNullWhenEmpty()
    {
        when(contactGroupMapper.selectListByChatId(TENANT, CHAT_ID)).thenReturn(Collections.emptyList());
        assertNull(contactGroupService.byChat(CHAT_ID));
    }

    @Test
    void byChat_returnsSingleRow()
    {
        CrmWecomContactGroup row = new CrmWecomContactGroup();
        row.setId("g1");
        row.setChatId(CHAT_ID);
        when(contactGroupMapper.selectListByChatId(TENANT, CHAT_ID)).thenReturn(Collections.singletonList(row));

        CrmWecomContactGroup result = contactGroupService.byChat(CHAT_ID);
        assertEquals("g1", result.getId());
    }

    @Test
    void byChat_throws409OnDuplicate()
    {
        CrmWecomContactGroup a = new CrmWecomContactGroup();
        a.setId("g1");
        CrmWecomContactGroup b = new CrmWecomContactGroup();
        b.setId("g2");
        when(contactGroupMapper.selectListByChatId(TENANT, CHAT_ID)).thenReturn(Arrays.asList(a, b));

        ServiceException ex = assertThrows(ServiceException.class, () -> contactGroupService.byChat(CHAT_ID));
        assertEquals(Integer.valueOf(409), ex.getCode());
        assertEquals(WecomSidebarGroupResolveResultDto.MSG_DUPLICATE_BINDING, ex.getMessage());
    }

    @Test
    void byChat_blankThrows400()
    {
        ServiceException ex = assertThrows(ServiceException.class, () -> contactGroupService.byChat(" "));
        assertEquals(Integer.valueOf(400), ex.getCode());
    }
}
