package com.ruoyi.crm.domain.dto.whatsapp;

/**
 * WhatsApp 消息发送结果
 */
public class WhatsAppSendResult
{
    private String messageId;
    private String waId;
    private boolean success;
    private String errorMessage;

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getWaId() { return waId; }
    public void setWaId(String waId) { this.waId = waId; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public static WhatsAppSendResult success(String messageId, String waId)
    {
        WhatsAppSendResult r = new WhatsAppSendResult();
        r.messageId = messageId;
        r.waId = waId;
        r.success = true;
        return r;
    }

    public static WhatsAppSendResult failure(String waId, String errorMessage)
    {
        WhatsAppSendResult r = new WhatsAppSendResult();
        r.waId = waId;
        r.success = false;
        r.errorMessage = errorMessage;
        return r;
    }
}
