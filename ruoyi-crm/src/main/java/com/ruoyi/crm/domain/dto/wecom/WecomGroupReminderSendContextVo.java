package com.ruoyi.crm.domain.dto.wecom;

import java.util.Date;

/**
 * H5 群提醒发送页上下文（JWT GET send-context）。
 */
public class WecomGroupReminderSendContextVo
{
    private String id;
    private String renderedText;
    private String contactName;
    private String contactId;
    private String groupName;
    private String chatId;
    private String status;
    private Date dueAt;
    private String ruleId;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getRenderedText()
    {
        return renderedText;
    }

    public void setRenderedText(String renderedText)
    {
        this.renderedText = renderedText;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getChatId()
    {
        return chatId;
    }

    public void setChatId(String chatId)
    {
        this.chatId = chatId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getDueAt()
    {
        return dueAt;
    }

    public void setDueAt(Date dueAt)
    {
        this.dueAt = dueAt;
    }

    public String getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }
}
