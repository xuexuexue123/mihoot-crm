package com.ruoyi.crm.service.wecom.contactgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.CrmWecomGroupReminder;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderMapper;
import com.ruoyi.crm.service.support.TenantPublicAppUrlResolver;
import com.ruoyi.crm.wecom.WecomMessageService;
import com.ruoyi.system.mapper.SysUserMapper;

@ExtendWith(MockitoExtension.class)
class WecomGroupReminderStaffNotifyServiceTest
{
    @Mock
    private CrmWecomGroupReminderMapper reminderMapper;
    @Mock
    private CrmWecomContactGroupMapper contactGroupMapper;
    @Mock
    private CrmContactMapper contactMapper;
    @Mock
    private SysUserMapper sysUserMapper;
    @Mock
    private CrmWecomConfigMapper wecomConfigMapper;
    @Mock
    private WecomMessageService wecomMessageService;
    @Mock
    private TenantPublicAppUrlResolver publicAppUrlResolver;

    @InjectMocks
    private WecomGroupReminderStaffNotifyService service;

    private CrmWecomGroupReminder pending;
    private CrmContact contact;
    private CrmWecomConfig config;

    @BeforeEach
    void setUp()
    {
        pending = new CrmWecomGroupReminder();
        pending.setId("rem-1");
        pending.setTenantId("tenant3");
        pending.setContactId("c-1");
        pending.setStatus(WecomContactGroupConstants.REMINDER_PENDING);
        pending.setRenderedText("您好张三，请复诊");
        pending.setRetryCount(0);

        contact = new CrmContact();
        contact.setContactId("c-1");
        contact.setName("张三");
        contact.setUserId(10L);

        config = new CrmWecomConfig();
        config.setAgentId("1000002");
    }

    @Test
    void supportReadyAndMessage()
    {
        assertFalse(WecomGroupReminderSupport.isServiceGroupReady(null));
        CrmWecomContactGroup g = new CrmWecomContactGroup();
        g.setChatId("wrxxx");
        g.setStatus(WecomContactGroupConstants.GROUP_CREATED);
        assertTrue(WecomGroupReminderSupport.isServiceGroupReady(g));
        g.setStatus(WecomContactGroupConstants.GROUP_DISMISSED);
        assertFalse(WecomGroupReminderSupport.isServiceGroupReady(g));
        assertEquals("未命名服务群", WecomGroupReminderSupport.displayGroupName(g));
        g.setGroupName("张三-服务群");
        assertEquals("张三-服务群", WecomGroupReminderSupport.displayGroupName(g));

        String body = WecomGroupReminderSupport.buildStaffNotifyContent("张三", "张三-服务群", "wrxxx", "请复诊");
        assertTrue(body.contains("张三-服务群"));
        assertTrue(body.contains("wrxxx"));
        assertTrue(body.contains("请复诊"));
        assertTrue(WecomGroupReminderSupport.buildNoGroupNudgeContent("李四").contains("李四"));

        String desc = WecomGroupReminderSupport.buildTextcardDescription("张三", "张三-服务群", "请复诊");
        assertTrue(desc.contains("客户：张三"));
        assertTrue(desc.contains("服务群：张三-服务群"));
        assertTrue(desc.contains("请复诊"));
        StringBuilder longSb = new StringBuilder();
        for (int i = 0; i < 10; i++)
        {
            longSb.append("一二三四五六七八九十");
        }
        assertTrue(WecomGroupReminderSupport.truncate(longSb.toString(), 80).endsWith("…"));
        assertEquals("https://t.example.com/wecom/group-reminder-send?id=rem-1",
                WecomGroupReminderSupport.buildSendPageUrl("https://t.example.com/", "rem-1"));
        assertEquals("", WecomGroupReminderSupport.buildSendPageUrl("", "rem-1"));
    }

    @Test
    void resolveSnUseridPrefersSysUser()
    {
        SysUser u = new SysUser();
        u.setSnUserid("zhangsan");
        when(sysUserMapper.selectUserById(10L)).thenReturn(u);
        assertEquals("zhangsan", WecomGroupReminderSupport.resolveSnUserid(contact, sysUserMapper));
        contact.setUserId(null);
        contact.setWecomFollowUserid("follow1");
        assertEquals("follow1", WecomGroupReminderSupport.resolveSnUserid(contact, sysUserMapper));
        contact.setWecomFollowUserid(null);
        assertNull(WecomGroupReminderSupport.resolveSnUserid(contact, sysUserMapper));
    }

    @Test
    void processOneSendsTextcardAndMarksSent()
    {
        when(reminderMapper.selectById("rem-1")).thenReturn(pending);
        when(contactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        SysUser u = new SysUser();
        u.setSnUserid("zhangsan");
        when(sysUserMapper.selectUserById(10L)).thenReturn(u);
        CrmWecomContactGroup group = new CrmWecomContactGroup();
        group.setChatId("wrchat");
        group.setGroupName("张三-服务群");
        group.setStatus(WecomContactGroupConstants.GROUP_CREATED);
        when(contactGroupMapper.selectByContactId("tenant3", "c-1")).thenReturn(group);
        when(wecomConfigMapper.selectFirst()).thenReturn(config);
        when(publicAppUrlResolver.resolve("tenant3")).thenReturn("https://tenant3.example.com");

        service.processOne(pending);

        verify(wecomMessageService).sendTextcardToUser(eq(config), eq("zhangsan"),
                eq(WecomGroupReminderSupport.TEXTCARD_TITLE), anyString(),
                eq("https://tenant3.example.com/wecom/group-reminder-send?id=rem-1"),
                eq(WecomGroupReminderSupport.TEXTCARD_BTN));
        verify(wecomMessageService, never()).sendTextToUser(any(), anyString(), anyString());
        ArgumentCaptor<CrmWecomGroupReminder> cap = ArgumentCaptor.forClass(CrmWecomGroupReminder.class);
        verify(reminderMapper).update(cap.capture());
        assertEquals(WecomContactGroupConstants.REMINDER_SENT, cap.getValue().getStatus());
        assertEquals("wrchat", cap.getValue().getChatId());
    }

    @Test
    void processOneFailsWhenOriginMissing()
    {
        when(reminderMapper.selectById("rem-1")).thenReturn(pending);
        when(contactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        SysUser u = new SysUser();
        u.setSnUserid("zhangsan");
        when(sysUserMapper.selectUserById(10L)).thenReturn(u);
        CrmWecomContactGroup group = new CrmWecomContactGroup();
        group.setChatId("wrchat");
        group.setStatus(WecomContactGroupConstants.GROUP_CREATED);
        when(contactGroupMapper.selectByContactId("tenant3", "c-1")).thenReturn(group);
        when(publicAppUrlResolver.resolve("tenant3")).thenReturn("");

        service.processOne(pending);

        verify(wecomMessageService, never()).sendTextcardToUser(any(), anyString(), anyString(), anyString(),
                anyString(), anyString());
        ArgumentCaptor<CrmWecomGroupReminder> cap = ArgumentCaptor.forClass(CrmWecomGroupReminder.class);
        verify(reminderMapper).update(cap.capture());
        assertEquals(WecomContactGroupConstants.REMINDER_PENDING, cap.getValue().getStatus());
        assertTrue(cap.getValue().getErrmsg().contains("public app url"));
    }

    @Test
    void processOneSkipsWhenNoGroup()
    {
        when(reminderMapper.selectById("rem-1")).thenReturn(pending);
        when(contactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        SysUser u = new SysUser();
        u.setSnUserid("zhangsan");
        when(sysUserMapper.selectUserById(10L)).thenReturn(u);
        when(contactGroupMapper.selectByContactId("tenant3", "c-1")).thenReturn(null);
        when(wecomConfigMapper.selectFirst()).thenReturn(config);

        service.processOne(pending);

        ArgumentCaptor<CrmWecomGroupReminder> cap = ArgumentCaptor.forClass(CrmWecomGroupReminder.class);
        verify(reminderMapper).update(cap.capture());
        assertEquals(WecomContactGroupConstants.REMINDER_SKIPPED, cap.getValue().getStatus());
        assertEquals(WecomContactGroupConstants.SKIP_NO_SERVICE_GROUP, cap.getValue().getSkippedReason());
        verify(wecomMessageService).sendTextToUser(eq(config), eq("zhangsan"), anyString());
        verify(wecomMessageService, never()).sendTextcardToUser(any(), anyString(), anyString(), anyString(),
                anyString(), anyString());
    }

    @Test
    void processOneSkipsWhenNoUserid()
    {
        when(reminderMapper.selectById("rem-1")).thenReturn(pending);
        contact.setUserId(null);
        contact.setWecomFollowUserid(null);
        when(contactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        CrmWecomContactGroup group = new CrmWecomContactGroup();
        group.setChatId("wrchat");
        group.setStatus(WecomContactGroupConstants.GROUP_CREATED);
        when(contactGroupMapper.selectByContactId("tenant3", "c-1")).thenReturn(group);

        service.processOne(pending);

        ArgumentCaptor<CrmWecomGroupReminder> cap = ArgumentCaptor.forClass(CrmWecomGroupReminder.class);
        verify(reminderMapper).update(cap.capture());
        assertEquals(WecomContactGroupConstants.SKIP_NO_WECOM_USERID, cap.getValue().getSkippedReason());
        verify(wecomMessageService, never()).sendTextToUser(any(), anyString(), anyString());
        verify(wecomMessageService, never()).sendTextcardToUser(any(), anyString(), anyString(), anyString(),
                anyString(), anyString());
    }

    @Test
    void processOneRetriesThenFails()
    {
        pending.setRetryCount(2);
        when(reminderMapper.selectById("rem-1")).thenReturn(pending);
        when(contactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        SysUser u = new SysUser();
        u.setSnUserid("zhangsan");
        when(sysUserMapper.selectUserById(10L)).thenReturn(u);
        CrmWecomContactGroup group = new CrmWecomContactGroup();
        group.setChatId("wrchat");
        group.setStatus(WecomContactGroupConstants.GROUP_ACTIVE);
        when(contactGroupMapper.selectByContactId("tenant3", "c-1")).thenReturn(group);
        when(wecomConfigMapper.selectFirst()).thenReturn(config);
        when(publicAppUrlResolver.resolve("tenant3")).thenReturn("https://tenant3.example.com");
        doThrow(new ServiceException("send fail")).when(wecomMessageService).sendTextcardToUser(any(), anyString(),
                anyString(), anyString(), anyString(), anyString());

        service.processOne(pending);

        ArgumentCaptor<CrmWecomGroupReminder> cap = ArgumentCaptor.forClass(CrmWecomGroupReminder.class);
        verify(reminderMapper).update(cap.capture());
        assertEquals(WecomContactGroupConstants.REMINDER_FAILED, cap.getValue().getStatus());
        assertEquals(Integer.valueOf(3), cap.getValue().getRetryCount());
    }
}
