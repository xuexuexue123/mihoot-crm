package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class AiChatMessageVo
{
    private String messageId;

    private String sessionId;

    private String role;

    private String partsJson;

    private String metadataJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public String getPartsJson()
    {
        return partsJson;
    }

    public void setPartsJson(String partsJson)
    {
        this.partsJson = partsJson;
    }

    public String getMetadataJson()
    {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson)
    {
        this.metadataJson = metadataJson;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
