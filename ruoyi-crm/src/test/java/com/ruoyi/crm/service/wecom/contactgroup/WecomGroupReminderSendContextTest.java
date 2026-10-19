package com.ruoyi.crm.service.wecom.contactgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.CrmWecomGroupReminder;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupReminderSendContextVo;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupReminderMapper;
import com.ruoyi.system.mapper.SysUserMapper;

@ExtendWith(MockitoExtension.class)
class WecomGroupReminderSendContextTest
{
    @Mock
    private CrmWecomGroupReminderMapper reminderMapper;
    @Mock
    private CrmWecomContactGroupMapper contactGroupMapper;
    @Mock
    private CrmContactMapper contactMapper;
    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private WecomGroupReminderService service;

    private CrmWecomGroupReminder row;
    private CrmContact contact;

    @BeforeEach
    void setUp()
    {
        row = new CrmWecomGroupReminder();
        row.setId("rem-1");
        row.setTenantId("tenant3");
        row.setContactId("c-1");
        row.setStatus(WecomContactGroupConstants.REMINDER_SENT);
        row.setRenderedText("完整话术内容");
        row.setDueAt(new Date());
        row.setRuleId("sys-wecom-recall-due");
        row.setChatId("wr-hist");

        contact = new CrmContact();
        contact.setContactId("c-1");
        contact.setName("Ninestars");
        contact.setUserId(10L);
    }

    @AfterEach
    void clearSecurity()
    {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(String snUserid, Set<String> perms)
    {
        SysUser user = new SysUser();
        user.setUserName("u1");
        user.setSnUserid(snUserid);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(99L);
        loginUser.setUser(user);
        loginUser.setPermissions(perms == null ? Collections.emptySet() : perms);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));
    }

    @Test
    void ownerCanGetContext()
    {
        loginAs("LuoHuaDong", Collections.emptySet());
        when(reminderMapper.selectById("rem-1")).thenReturn(row);
        when(contactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        SysUser owner = new SysUser();
        owner.setSnUserid("LuoHuaDong");
        when(sysUserMapper.selectUserById(10L)).thenReturn(owner);
        CrmWecomContactGroup group = new CrmWecomContactGroup();
        group.setChatId("wrchat");
        group.setGroupName("Ninestars-服务群");
        group.setStatus(WecomContactGroupConstants.GROUP_CREATED);
        when(contactGroupMapper.selectByContactId("tenant3", "c-1")).thenReturn(group);

        WecomGroupReminderSendContextVo vo = service.getSendContext("rem-1");
        assertEquals("完整话术内容", vo.getRenderedText());
        assertEquals("wrchat", vo.getChatId());
        assertEquals("Ninestars-服务群", vo.getGroupName());
        assertEquals("Ninestars", vo.getContactName());
    }

    @Test
    void listPermissionCanGetContext()
    {
        Set<String> perms = new HashSet<>();
        perms.add("crm:wecom:reminder:list");
        loginAs("other", perms);
        when(reminderMapper.selectById("rem-1")).thenReturn(row);
        when(contactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        CrmWecomContactGroup group = new CrmWecomContactGroup();
        group.setChatId("wrchat");
        group.setStatus(WecomContactGroupConstants.GROUP_CREATED);
        when(contactGroupMapper.selectByContactId("tenant3", "c-1")).thenReturn(group);

        WecomGroupReminderSendContextVo vo = service.getSendContext("rem-1");
        assertEquals("rem-1", vo.getId());
        assertEquals("wrchat", vo.getChatId());
    }

    @Test
    void strangerForbidden()
    {
        loginAs("stranger", Collections.emptySet());
        when(reminderMapper.selectById("rem-1")).thenReturn(row);
        when(contactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        SysUser owner = new SysUser();
        owner.setSnUserid("LuoHuaDong");
        when(sysUserMapper.selectUserById(10L)).thenReturn(owner);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.getSendContext("rem-1"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getCode().intValue());
    }

    @Test
    void missingIdNotFound()
    {
        loginAs("LuoHuaDong", Collections.emptySet());
        when(reminderMapper.selectById("missing")).thenReturn(null);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.getSendContext("missing"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getCode().intValue());
    }

    @Test
    void emptyRenderedTextUnprocessable()
    {
        loginAs("LuoHuaDong", Collections.emptySet());
        row.setRenderedText("  ");
        when(reminderMapper.selectById("rem-1")).thenReturn(row);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.getSendContext("rem-1"));
        assertEquals(422, ex.getCode().intValue());
    }

    @Test
    void cancelledStillReturns()
    {
        Set<String> perms = new HashSet<>();
        perms.add("crm:wecom:reminder:list");
        loginAs("admin", perms);
        row.setStatus("cancelled");
        when(reminderMapper.selectById("rem-1")).thenReturn(row);
        when(contactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        when(contactGroupMapper.selectByContactId("tenant3", "c-1")).thenReturn(null);

        WecomGroupReminderSendContextVo vo = service.getSendContext("rem-1");
        assertEquals("cancelled", vo.getStatus());
        assertEquals("wr-hist", vo.getChatId());
    }
}
