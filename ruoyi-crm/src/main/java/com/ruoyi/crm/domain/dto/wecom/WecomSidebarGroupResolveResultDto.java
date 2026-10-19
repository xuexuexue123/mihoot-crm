package com.ruoyi.crm.domain.dto.wecom;

import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto.WecomSidebarCrmBlockDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto.WecomSidebarWecomBlockDto;

/**
 * 企微群聊工具栏瘦 resolve 响应。
 */
public class WecomSidebarGroupResolveResultDto
{
    public static final String ENTRY_GROUP_CHAT_TOOLS = "group_chat_tools";

    public static final String STATUS_LINKED = "linked_contact";
    public static final String STATUS_UNLINKED_GROUP = "unlinked_group";
    public static final String STATUS_DUPLICATE_BINDING = "duplicate_binding";

    public static final String MSG_DUPLICATE_BINDING = "Duplicate contact group bindings for chatId";

    public static final String CONSISTENCY_OK = "ok";
    public static final String CONSISTENCY_MISMATCH = "mismatch";
    public static final String CONSISTENCY_NO_WECOM_IDENTITY = "no_wecom_identity";
    public static final String CONSISTENCY_GROUP_DETAIL_UNAVAILABLE = "group_detail_unavailable";

    private String entry = ENTRY_GROUP_CHAT_TOOLS;
    private String crmStatus;
    private String chatId;
    private WecomSidebarGroupBlockDto group;
    private String contactId;
    private WecomSidebarGroupConsistencyDto consistency;
    private WecomSidebarWecomBlockDto wecom;
    private WecomSidebarCrmBlockDto crm;

    public String getEntry()
    {
        return entry;
    }

    public void setEntry(String entry)
    {
        this.entry = entry;
    }

    public String getCrmStatus()
    {
        return crmStatus;
    }

    public void setCrmStatus(String crmStatus)
    {
        this.crmStatus = crmStatus;
    }

    public String getChatId()
    {
        return chatId;
    }

    public void setChatId(String chatId)
    {
        this.chatId = chatId;
    }

    public WecomSidebarGroupBlockDto getGroup()
    {
        return group;
    }

    public void setGroup(WecomSidebarGroupBlockDto group)
    {
        this.group = group;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public WecomSidebarGroupConsistencyDto getConsistency()
    {
        return consistency;
    }

    public void setConsistency(WecomSidebarGroupConsistencyDto consistency)
    {
        this.consistency = consistency;
    }

    public WecomSidebarWecomBlockDto getWecom()
    {
        return wecom;
    }

    public void setWecom(WecomSidebarWecomBlockDto wecom)
    {
        this.wecom = wecom;
    }

    public WecomSidebarCrmBlockDto getCrm()
    {
        return crm;
    }

    public void setCrm(WecomSidebarCrmBlockDto crm)
    {
        this.crm = crm;
    }

    public static class WecomSidebarGroupBlockDto
    {
        private String name;
        private String ownerUserid;
        private Integer memberCount;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getOwnerUserid()
        {
            return ownerUserid;
        }

        public void setOwnerUserid(String ownerUserid)
        {
            this.ownerUserid = ownerUserid;
        }

        public Integer getMemberCount()
        {
            return memberCount;
        }

        public void setMemberCount(Integer memberCount)
        {
            this.memberCount = memberCount;
        }
    }

    public static class WecomSidebarGroupConsistencyDto
    {
        private String status;

        public WecomSidebarGroupConsistencyDto()
        {
        }

        public WecomSidebarGroupConsistencyDto(String status)
        {
            this.status = status;
        }

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }
    }
}
