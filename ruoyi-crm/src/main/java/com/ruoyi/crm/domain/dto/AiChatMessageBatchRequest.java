package com.ruoyi.crm.domain.dto;

import java.util.List;

public class AiChatMessageBatchRequest
{
    private List<AiChatMessageItem> messages;

    public static class AiChatMessageItem
    {
        private String messageId;
        private String role;
        private String partsJson;
        private String metadataJson;

        public String getMessageId()
        {
            return messageId;
        }

        public void setMessageId(String messageId)
        {
            this.messageId = messageId;
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
    }

    public List<AiChatMessageItem> getMessages()
    {
        return messages;
    }

    public void setMessages(List<AiChatMessageItem> messages)
    {
        this.messages = messages;
    }
}
