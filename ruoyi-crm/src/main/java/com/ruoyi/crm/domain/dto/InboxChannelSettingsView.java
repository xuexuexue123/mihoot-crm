package com.ruoyi.crm.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * API view for GET/PUT /crm/inbox/channels (camelCase, nested enabled flags).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InboxChannelSettingsView
{
    private ChannelFlag whatsapp;
    private ChannelFlag messenger;
    private ChannelFlag wechatMp;
    private ChannelFlag webchat;
    private ChannelFlag wecomKf;
    private String updatedAt;
    private String updatedBy;

    public static class ChannelFlag
    {
        private boolean enabled;

        public ChannelFlag()
        {
        }

        public ChannelFlag(boolean enabled)
        {
            this.enabled = enabled;
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }
    }

    public ChannelFlag getWhatsapp()
    {
        return whatsapp;
    }

    public void setWhatsapp(ChannelFlag whatsapp)
    {
        this.whatsapp = whatsapp;
    }

    public ChannelFlag getMessenger()
    {
        return messenger;
    }

    public void setMessenger(ChannelFlag messenger)
    {
        this.messenger = messenger;
    }

    public ChannelFlag getWechatMp()
    {
        return wechatMp;
    }

    public void setWechatMp(ChannelFlag wechatMp)
    {
        this.wechatMp = wechatMp;
    }

    public ChannelFlag getWebchat()
    {
        return webchat;
    }

    public void setWebchat(ChannelFlag webchat)
    {
        this.webchat = webchat;
    }

    public ChannelFlag getWecomKf()
    {
        return wecomKf;
    }

    public void setWecomKf(ChannelFlag wecomKf)
    {
        this.wecomKf = wecomKf;
    }

    public String getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy()
    {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy)
    {
        this.updatedBy = updatedBy;
    }
}
