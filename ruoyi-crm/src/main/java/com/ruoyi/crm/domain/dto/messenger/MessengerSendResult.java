package com.ruoyi.crm.domain.dto.messenger;

/**
 * Messenger 消息发送结果
 */
public class MessengerSendResult
{
    private String messageId;
    private String psid;
    private boolean success;
    private String errorMessage;

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getPsid() { return psid; }
    public void setPsid(String psid) { this.psid = psid; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public static MessengerSendResult success(String messageId, String psid)
    {
        MessengerSendResult r = new MessengerSendResult();
        r.messageId = messageId;
        r.psid = psid;
        r.success = true;
        return r;
    }

    public static MessengerSendResult failure(String psid, String errorMessage)
    {
        MessengerSendResult r = new MessengerSendResult();
        r.psid = psid;
        r.success = false;
        r.errorMessage = errorMessage;
        return r;
    }
}
