package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class CrmWecomMassTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String msgId;
    private String chatType;
    private String externalUseridsJson;
    private String senderListJson;
    private String contentJson;
    private String status;
    private String sendResultJson;
    private String crmSurveyId;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getMsgId()
    {
        return msgId;
    }

    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }

    public String getChatType()
    {
        return chatType;
    }

    public void setChatType(String chatType)
    {
        this.chatType = chatType;
    }

    public String getExternalUseridsJson()
    {
        return externalUseridsJson;
    }

    public void setExternalUseridsJson(String externalUseridsJson)
    {
        this.externalUseridsJson = externalUseridsJson;
    }

    public String getSenderListJson()
    {
        return senderListJson;
    }

    public void setSenderListJson(String senderListJson)
    {
        this.senderListJson = senderListJson;
    }

    public String getContentJson()
    {
        return contentJson;
    }

    public void setContentJson(String contentJson)
    {
        this.contentJson = contentJson;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getSendResultJson()
    {
        return sendResultJson;
    }

    public void setSendResultJson(String sendResultJson)
    {
        this.sendResultJson = sendResultJson;
    }

    public String getCrmSurveyId()
    {
        return crmSurveyId;
    }

    public void setCrmSurveyId(String crmSurveyId)
    {
        this.crmSurveyId = crmSurveyId;
    }
}
