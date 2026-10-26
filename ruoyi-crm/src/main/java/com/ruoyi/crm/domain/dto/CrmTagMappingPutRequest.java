package com.ruoyi.crm.domain.dto;

/**
 * 绑定 / 解绑渠道标签
 */
public class CrmTagMappingPutRequest
{
    private String dictValue;
    /** wecom / wechat_mp */
    private String channel;
    /** null 表示解绑 */
    private String channelTagId;
    private String channelTagName;

    public String getDictValue()
    {
        return dictValue;
    }

    public void setDictValue(String dictValue)
    {
        this.dictValue = dictValue;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getChannelTagId()
    {
        return channelTagId;
    }

    public void setChannelTagId(String channelTagId)
    {
        this.channelTagId = channelTagId;
    }

    public String getChannelTagName()
    {
        return channelTagName;
    }

    public void setChannelTagName(String channelTagName)
    {
        this.channelTagName = channelTagName;
    }
}
