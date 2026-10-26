package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;
import java.util.Map;

/**
 * 创建群发任务（映射 add_msg_template 子集）
 */
public class WecomMassSendCreateRequest
{
    private String chatType;
    private List<String> externalUserid;
    private List<String> sender;
    /** 文本消息 content.text */
    private String text;
    private Map<String, Object> attachments;
    /** 关联问卷 ID（可选） */
    private String surveyId;

    public String getChatType()
    {
        return chatType;
    }

    public void setChatType(String chatType)
    {
        this.chatType = chatType;
    }

    public List<String> getExternalUserid()
    {
        return externalUserid;
    }

    public void setExternalUserid(List<String> externalUserid)
    {
        this.externalUserid = externalUserid;
    }

    public List<String> getSender()
    {
        return sender;
    }

    public void setSender(List<String> sender)
    {
        this.sender = sender;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Map<String, Object> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments)
    {
        this.attachments = attachments;
    }

    public String getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(String surveyId)
    {
        this.surveyId = surveyId;
    }
}
