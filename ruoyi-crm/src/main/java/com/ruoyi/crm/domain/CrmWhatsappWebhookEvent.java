package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Meta WhatsApp Webhook 事件日志
 */
public class CrmWhatsappWebhookEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tenantId;
    private String waId;
    private String phoneNumber;
    private String phoneNumberId;
    private String wabaId;
    private String eventType;
    private String messageId;
    private String messageType;
    private String content;
    private Integer direction;
    private Long sendTime;
    private String rawJson;
    private String adCampaignId;
    private String adSetId;
    private String adId;
    private String crmLeadId;
    private String crmContactId;
    private String status;
    private String processingError;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getWaId() { return waId; }
    public void setWaId(String waId) { this.waId = waId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPhoneNumberId() { return phoneNumberId; }
    public void setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; }

    public String getWabaId() { return wabaId; }
    public void setWabaId(String wabaId) { this.wabaId = wabaId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getDirection() { return direction; }
    public void setDirection(Integer direction) { this.direction = direction; }

    public Long getSendTime() { return sendTime; }
    public void setSendTime(Long sendTime) { this.sendTime = sendTime; }

    public String getRawJson() { return rawJson; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }

    public String getAdCampaignId() { return adCampaignId; }
    public void setAdCampaignId(String adCampaignId) { this.adCampaignId = adCampaignId; }

    public String getAdSetId() { return adSetId; }
    public void setAdSetId(String adSetId) { this.adSetId = adSetId; }

    public String getAdId() { return adId; }
    public void setAdId(String adId) { this.adId = adId; }

    public String getCrmLeadId() { return crmLeadId; }
    public void setCrmLeadId(String crmLeadId) { this.crmLeadId = crmLeadId; }

    public String getCrmContactId() { return crmContactId; }
    public void setCrmContactId(String crmContactId) { this.crmContactId = crmContactId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProcessingError() { return processingError; }
    public void setProcessingError(String processingError) { this.processingError = processingError; }
}
