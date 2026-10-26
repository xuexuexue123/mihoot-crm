package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

/**
 * 企微侧边栏瘦 resolve 响应。
 */
public class WecomSidebarResolveResultDto
{
    private String crmStatus;
    private String contactId;
    private WecomSidebarWecomBlockDto wecom;
    private WecomSidebarCrmBlockDto crm;

    public String getCrmStatus()
    {
        return crmStatus;
    }

    public void setCrmStatus(String crmStatus)
    {
        this.crmStatus = crmStatus;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
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

    public static class WecomSidebarWecomBlockDto
    {
        private String externalUserid;
        private String name;
        private String avatar;
        private String remark;
        private List<WecomSidebarCorpTagDto> corpTags;

        public String getExternalUserid()
        {
            return externalUserid;
        }

        public void setExternalUserid(String externalUserid)
        {
            this.externalUserid = externalUserid;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getAvatar()
        {
            return avatar;
        }

        public void setAvatar(String avatar)
        {
            this.avatar = avatar;
        }

        public String getRemark()
        {
            return remark;
        }

        public void setRemark(String remark)
        {
            this.remark = remark;
        }

        public List<WecomSidebarCorpTagDto> getCorpTags()
        {
            return corpTags;
        }

        public void setCorpTags(List<WecomSidebarCorpTagDto> corpTags)
        {
            this.corpTags = corpTags;
        }
    }

    public static class WecomSidebarCorpTagDto
    {
        private String groupName;
        private String tagName;

        public String getGroupName()
        {
            return groupName;
        }

        public void setGroupName(String groupName)
        {
            this.groupName = groupName;
        }

        public String getTagName()
        {
            return tagName;
        }

        public void setTagName(String tagName)
        {
            this.tagName = tagName;
        }
    }

    public static class WecomSidebarCrmBlockDto
    {
        private String displayName;
        private String phone;
        private List<String> tags;
        private WecomSidebarActiveOpportunityDto activeOpportunity;

        public String getDisplayName()
        {
            return displayName;
        }

        public void setDisplayName(String displayName)
        {
            this.displayName = displayName;
        }

        public String getPhone()
        {
            return phone;
        }

        public void setPhone(String phone)
        {
            this.phone = phone;
        }

        public List<String> getTags()
        {
            return tags;
        }

        public void setTags(List<String> tags)
        {
            this.tags = tags;
        }

        public WecomSidebarActiveOpportunityDto getActiveOpportunity()
        {
            return activeOpportunity;
        }

        public void setActiveOpportunity(WecomSidebarActiveOpportunityDto activeOpportunity)
        {
            this.activeOpportunity = activeOpportunity;
        }
    }

    public static class WecomSidebarActiveOpportunityDto
    {
        private String optyId;
        private String name;
        private String stage;

        public String getOptyId()
        {
            return optyId;
        }

        public void setOptyId(String optyId)
        {
            this.optyId = optyId;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getStage()
        {
            return stage;
        }

        public void setStage(String stage)
        {
            this.stage = stage;
        }
    }
}
