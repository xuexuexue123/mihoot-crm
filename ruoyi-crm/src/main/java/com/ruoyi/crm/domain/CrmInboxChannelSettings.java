package com.ruoyi.crm.domain;

import java.util.Date;
import com.ruoyi.crm.domain.enums.InboxChannel;

/**
 * Unified Inbox channel enable flags per tenant (crm_inbox_channel_settings).
 */
public class CrmInboxChannelSettings
{
    private String tenantId;

    private boolean whatsappEnabled = true;

    private boolean messengerEnabled = true;

    private boolean wechatMpEnabled = true;

    private boolean webchatEnabled = true;

    private boolean wecomKfEnabled = true;

    private String updatedBy;

    private Date updatedAt;

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public boolean isWhatsappEnabled()
    {
        return whatsappEnabled;
    }

    public void setWhatsappEnabled(boolean whatsappEnabled)
    {
        this.whatsappEnabled = whatsappEnabled;
    }

    public boolean isMessengerEnabled()
    {
        return messengerEnabled;
    }

    public void setMessengerEnabled(boolean messengerEnabled)
    {
        this.messengerEnabled = messengerEnabled;
    }

    public boolean isWechatMpEnabled()
    {
        return wechatMpEnabled;
    }

    public void setWechatMpEnabled(boolean wechatMpEnabled)
    {
        this.wechatMpEnabled = wechatMpEnabled;
    }

    public boolean isWebchatEnabled()
    {
        return webchatEnabled;
    }

    public void setWebchatEnabled(boolean webchatEnabled)
    {
        this.webchatEnabled = webchatEnabled;
    }

    public boolean isWecomKfEnabled()
    {
        return wecomKfEnabled;
    }

    public void setWecomKfEnabled(boolean wecomKfEnabled)
    {
        this.wecomKfEnabled = wecomKfEnabled;
    }

    public String getUpdatedBy()
    {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy)
    {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public boolean isEnabled(InboxChannel channel)
    {
        if (channel == null)
        {
            return true;
        }
        switch (channel)
        {
            case WHATSAPP:
                return whatsappEnabled;
            case MESSENGER:
                return messengerEnabled;
            case WECHAT_MP:
                return wechatMpEnabled;
            case WEBCHAT:
                return webchatEnabled;
            case WECOM_KF:
                return wecomKfEnabled;
            default:
                return true;
        }
    }
}
