package com.ruoyi.crm.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Partial update body for PUT /crm/inbox/channels.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InboxChannelSettingsUpdateRequest
{
    private InboxChannelSettingsView.ChannelFlag whatsapp;
    private InboxChannelSettingsView.ChannelFlag messenger;
    private InboxChannelSettingsView.ChannelFlag wechatMp;
    private InboxChannelSettingsView.ChannelFlag webchat;
    private InboxChannelSettingsView.ChannelFlag wecomKf;

    public InboxChannelSettingsView.ChannelFlag getWhatsapp()
    {
        return whatsapp;
    }

    public void setWhatsapp(InboxChannelSettingsView.ChannelFlag whatsapp)
    {
        this.whatsapp = whatsapp;
    }

    public InboxChannelSettingsView.ChannelFlag getMessenger()
    {
        return messenger;
    }

    public void setMessenger(InboxChannelSettingsView.ChannelFlag messenger)
    {
        this.messenger = messenger;
    }

    public InboxChannelSettingsView.ChannelFlag getWechatMp()
    {
        return wechatMp;
    }

    public void setWechatMp(InboxChannelSettingsView.ChannelFlag wechatMp)
    {
        this.wechatMp = wechatMp;
    }

    public InboxChannelSettingsView.ChannelFlag getWebchat()
    {
        return webchat;
    }

    public void setWebchat(InboxChannelSettingsView.ChannelFlag webchat)
    {
        this.webchat = webchat;
    }

    public InboxChannelSettingsView.ChannelFlag getWecomKf()
    {
        return wecomKf;
    }

    public void setWecomKf(InboxChannelSettingsView.ChannelFlag wecomKf)
    {
        this.wecomKf = wecomKf;
    }
}
