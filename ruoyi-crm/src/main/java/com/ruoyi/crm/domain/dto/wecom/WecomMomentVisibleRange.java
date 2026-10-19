package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 朋友圈可见范围。
 */
public class WecomMomentVisibleRange
{
    @JsonProperty("sender_list")
    private WecomMomentSenderList senderList;

    @JsonProperty("external_contact_list")
    private WecomMomentExternalContactList externalContactList;

    public WecomMomentSenderList getSenderList()
    {
        return senderList;
    }

    public void setSenderList(WecomMomentSenderList senderList)
    {
        this.senderList = senderList;
    }

    public WecomMomentExternalContactList getExternalContactList()
    {
        return externalContactList;
    }

    public void setExternalContactList(WecomMomentExternalContactList externalContactList)
    {
        this.externalContactList = externalContactList;
    }
}
