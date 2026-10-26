package com.ruoyi.crm.service.wecom.contactgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatDetailResponse;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.service.ICrmWecomGroupChatService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@ExtendWith(MockitoExtension.class)
class WecomContactGroupCallbackHandlerEnrichTest
{
    private static final String CHAT_ID = "wrYYYYYYYY";
    private static final String OWNER = "LuoHuaDong";
    private static final String EXT = "wmExternalA";

    @Mock
    private WecomContactGroupService contactGroupService;

    @Mock
    private CrmWecomContactGroupMapper contactGroupMapper;

    @Mock
    private CrmContactMapper crmContactMapper;

    @Mock
    private CrmWecomConfigMapper wecomConfigMapper;

    @Mock
    private WecomAccessTokenService wecomAccessTokenService;

    @Mock
    private ICrmWecomGroupChatService groupChatService;

    @InjectMocks
    private WecomContactGroupCallbackHandler handler;

    @Test
    void create_enrichesFromGroupChatGetWhenXmlLacksOwnerAndMembers()
    {
        CrmWecomConfig cfg = new CrmWecomConfig();
        cfg.setCorpId("corp");
        when(wecomConfigMapper.selectFirst()).thenReturn(cfg);
        when(wecomAccessTokenService.getAgentAccessToken(cfg)).thenReturn("token");

        WecomGroupChatDetailResponse.GroupChatMember member = new WecomGroupChatDetailResponse.GroupChatMember();
        member.setType(2);
        member.setUserid(EXT);
        WecomGroupChatDetailResponse.WecomGroupChatDetail detail = new WecomGroupChatDetailResponse.WecomGroupChatDetail();
        detail.setOwner(OWNER);
        detail.setName("Ninestars-服务群");
        detail.setMemberList(Collections.singletonList(member));
        WecomGroupChatDetailResponse resp = new WecomGroupChatDetailResponse();
        resp.setErrcode(0);
        resp.setGroupChat(detail);
        when(groupChatService.getGroupChat(eq("token"), eq(CHAT_ID), anyInt())).thenReturn(resp);

        String xml = "<xml><Event><![CDATA[change_external_chat]]></Event>"
                + "<ChangeType><![CDATA[create]]></ChangeType>"
                + "<ChatId><![CDATA[" + CHAT_ID + "]]></ChatId></xml>";

        handler.handleFromCallbackXml(xml);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> externalsCap = ArgumentCaptor.forClass(List.class);
        verify(contactGroupService).bindFromCallback(eq(OWNER), eq(CHAT_ID), externalsCap.capture(),
                eq("Ninestars-服务群"));
        assertEquals(Collections.singletonList(EXT), externalsCap.getValue());
    }

    @Test
    void create_passesXmlExternalsWithoutGetWhenPresent()
    {
        String xml = "<xml><Event><![CDATA[change_external_chat]]></Event>"
                + "<ChangeType><![CDATA[create]]></ChangeType>"
                + "<ChatId><![CDATA[" + CHAT_ID + "]]></ChatId>"
                + "<Owner><![CDATA[" + OWNER + "]]></Owner>"
                + "<Item><Type><![CDATA[2]]></Type><UserID><![CDATA[" + EXT + "]]></UserID></Item>"
                + "</xml>";

        handler.handleFromCallbackXml(xml);

        verify(groupChatService, never()).getGroupChat(anyString(), anyString(), anyInt());
        verify(contactGroupService).bindFromCallback(eq(OWNER), eq(CHAT_ID), eq(Collections.singletonList(EXT)),
                org.mockito.ArgumentMatchers.<String>isNull());
    }

    @Test
    void update_skipsDismissWhenGetFailsAndXmlHasNoMembers()
    {
        TenantDataSourceSupport.bindTenantKey("tenant3");
        try
        {
            CrmWecomContactGroup group = new CrmWecomContactGroup();
            group.setId("g1");
            group.setContactId("c1");
            group.setChatId(CHAT_ID);
            when(contactGroupMapper.selectByChatId("tenant3", CHAT_ID)).thenReturn(group);
            CrmContact contact = new CrmContact();
            contact.setContactId("c1");
            contact.setWecomExternalUserid(EXT);
            when(crmContactMapper.selectCrmContactByContactId("c1")).thenReturn(contact);
            when(wecomConfigMapper.selectFirst()).thenReturn(null);

            String xml = "<xml><Event><![CDATA[change_external_chat]]></Event>"
                    + "<ChangeType><![CDATA[update]]></ChangeType>"
                    + "<ChatId><![CDATA[" + CHAT_ID + "]]></ChatId></xml>";

            handler.handleFromCallbackXml(xml);

            verify(contactGroupService, never()).markDismissedByChatId(anyString());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }
}
