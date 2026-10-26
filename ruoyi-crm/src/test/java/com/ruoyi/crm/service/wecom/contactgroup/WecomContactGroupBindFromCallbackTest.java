package com.ruoyi.crm.service.wecom.contactgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.CrmWecomGroupProvisionTask;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupProvisionTaskMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderMapper;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@ExtendWith(MockitoExtension.class)
class WecomContactGroupBindFromCallbackTest
{
    private static final String TENANT = "tenant3";
    private static final String OWNER = "LuoHuaDong";
    private static final String CHAT_ID = "wrXXXXXXXX";
    private static final String EXT_A = "wmExternalA";
    private static final String EXT_B = "wmExternalB";

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
    void bindFromCallback_writesChatIdWhenOwnerAndExternalMatch()
    {
        CrmWecomContactGroup pending = group("g1", "c1", "task-1", WecomContactGroupConstants.GROUP_PENDING,
                new Date(1000));
        when(contactGroupMapper.selectByChatId(TENANT, CHAT_ID)).thenReturn(null);
        when(contactGroupMapper.selectPendingOrCreatedByOwnerAndExternal(TENANT, OWNER))
                .thenReturn(Collections.singletonList(pending));
        when(crmContactMapper.selectCrmContactByContactId("c1")).thenReturn(contact("c1", EXT_A));
        when(provisionTaskMapper.selectByTaskId("task-1")).thenReturn(task("task-1", "c1",
                WecomContactGroupConstants.TASK_PENDING));

        contactGroupService.bindFromCallback(OWNER, CHAT_ID, Collections.singletonList(EXT_A), "服务群");

        ArgumentCaptor<CrmWecomContactGroup> groupCap = ArgumentCaptor.forClass(CrmWecomContactGroup.class);
        verify(contactGroupMapper).update(groupCap.capture());
        assertEquals(CHAT_ID, groupCap.getValue().getChatId());
        assertEquals(WecomContactGroupConstants.GROUP_CREATED, groupCap.getValue().getStatus());
        assertEquals("服务群", groupCap.getValue().getGroupName());

        ArgumentCaptor<CrmWecomGroupProvisionTask> taskCap = ArgumentCaptor.forClass(CrmWecomGroupProvisionTask.class);
        verify(provisionTaskMapper).update(taskCap.capture());
        assertEquals(WecomContactGroupConstants.TASK_DONE, taskCap.getValue().getStatus());
    }

    @Test
    void bindFromCallback_sameChatIdIsIdempotent()
    {
        CrmWecomContactGroup existing = group("g1", "c1", "task-1", WecomContactGroupConstants.GROUP_CREATED,
                new Date());
        existing.setChatId(CHAT_ID);
        when(contactGroupMapper.selectByChatId(TENANT, CHAT_ID)).thenReturn(existing);

        contactGroupService.bindFromCallback(OWNER, CHAT_ID, Collections.singletonList(EXT_A), null);

        verify(contactGroupMapper, never()).update(any());
        verify(contactGroupMapper, never()).selectPendingOrCreatedByOwnerAndExternal(anyString(), anyString());
    }

    @Test
    void bindFromCallback_prefersPendingTaskAndNewestAmongMatches()
    {
        CrmWecomContactGroup olderPending = group("g-old", "c-old", "task-old",
                WecomContactGroupConstants.GROUP_PENDING, new Date(1000));
        CrmWecomContactGroup newerPending = group("g-new", "c-new", "task-new",
                WecomContactGroupConstants.GROUP_PENDING, new Date(5000));
        CrmWecomContactGroup createdHit = group("g-created", "c-created", "task-done",
                WecomContactGroupConstants.GROUP_CREATED, new Date(9000));

        when(contactGroupMapper.selectByChatId(TENANT, CHAT_ID)).thenReturn(null);
        when(contactGroupMapper.selectPendingOrCreatedByOwnerAndExternal(TENANT, OWNER))
                .thenReturn(Arrays.asList(createdHit, olderPending, newerPending));
        when(crmContactMapper.selectCrmContactByContactId("c-old")).thenReturn(contact("c-old", EXT_A));
        when(crmContactMapper.selectCrmContactByContactId("c-new")).thenReturn(contact("c-new", EXT_A));
        when(crmContactMapper.selectCrmContactByContactId("c-created")).thenReturn(contact("c-created", EXT_A));
        when(provisionTaskMapper.selectByTaskId("task-old")).thenReturn(task("task-old", "c-old",
                WecomContactGroupConstants.TASK_PENDING));
        when(provisionTaskMapper.selectByTaskId("task-new")).thenReturn(task("task-new", "c-new",
                WecomContactGroupConstants.TASK_PENDING));
        when(provisionTaskMapper.selectByTaskId("task-done")).thenReturn(task("task-done", "c-created",
                WecomContactGroupConstants.TASK_DONE));

        contactGroupService.bindFromCallback(OWNER, CHAT_ID, Collections.singletonList(EXT_A), null);

        ArgumentCaptor<CrmWecomContactGroup> groupCap = ArgumentCaptor.forClass(CrmWecomContactGroup.class);
        verify(contactGroupMapper).update(groupCap.capture());
        assertEquals("g-new", groupCap.getValue().getId());
        assertEquals(CHAT_ID, groupCap.getValue().getChatId());
    }

    @Test
    void bindFromCallback_noMatchWhenExternalMismatch()
    {
        CrmWecomContactGroup pending = group("g1", "c1", "task-1", WecomContactGroupConstants.GROUP_PENDING,
                new Date());
        when(contactGroupMapper.selectByChatId(TENANT, CHAT_ID)).thenReturn(null);
        when(contactGroupMapper.selectPendingOrCreatedByOwnerAndExternal(TENANT, OWNER))
                .thenReturn(Collections.singletonList(pending));
        when(crmContactMapper.selectCrmContactByContactId("c1")).thenReturn(contact("c1", EXT_A));

        contactGroupService.bindFromCallback(OWNER, CHAT_ID, Collections.singletonList(EXT_B), null);

        verify(contactGroupMapper, never()).update(any());
    }

    @Test
    void bindFromCallback_weakMatchWhenSingleCandidateAndNoExternals()
    {
        CrmWecomContactGroup pending = group("g1", "c1", "task-1", WecomContactGroupConstants.GROUP_PENDING,
                new Date());
        when(contactGroupMapper.selectByChatId(TENANT, CHAT_ID)).thenReturn(null);
        when(contactGroupMapper.selectPendingOrCreatedByOwnerAndExternal(TENANT, OWNER))
                .thenReturn(Collections.singletonList(pending));
        when(provisionTaskMapper.selectByTaskId("task-1")).thenReturn(task("task-1", "c1",
                WecomContactGroupConstants.TASK_PENDING));

        contactGroupService.bindFromCallback(OWNER, CHAT_ID, Collections.emptyList(), null);

        ArgumentCaptor<CrmWecomContactGroup> groupCap = ArgumentCaptor.forClass(CrmWecomContactGroup.class);
        verify(contactGroupMapper).update(groupCap.capture());
        assertEquals(CHAT_ID, groupCap.getValue().getChatId());
    }

    private static CrmWecomContactGroup group(String id, String contactId, String taskId, String status, Date updateTime)
    {
        CrmWecomContactGroup g = new CrmWecomContactGroup();
        g.setId(id);
        g.setTenantId(TENANT);
        g.setContactId(contactId);
        g.setOwnerUserid(OWNER);
        g.setProvisionTaskId(taskId);
        g.setStatus(status);
        g.setUpdateTime(updateTime);
        return g;
    }

    private static CrmContact contact(String contactId, String externalUserid)
    {
        CrmContact c = new CrmContact();
        c.setContactId(contactId);
        c.setWecomExternalUserid(externalUserid);
        return c;
    }

    private static CrmWecomGroupProvisionTask task(String taskId, String contactId, String status)
    {
        CrmWecomGroupProvisionTask t = new CrmWecomGroupProvisionTask();
        t.setTaskId(taskId);
        t.setTenantId(TENANT);
        t.setContactId(contactId);
        t.setStatus(status);
        return t;
    }
}
