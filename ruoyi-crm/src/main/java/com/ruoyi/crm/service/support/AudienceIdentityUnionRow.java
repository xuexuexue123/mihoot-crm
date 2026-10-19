package com.ruoyi.crm.service.support;

/**
 * 渠道身份值 → unionid
 */
public class AudienceIdentityUnionRow
{
    private String channelValue;
    private String unionid;

    public String getChannelValue()
    {
        return channelValue;
    }

    public void setChannelValue(String channelValue)
    {
        this.channelValue = channelValue;
    }

    public String getUnionid()
    {
        return unionid;
    }

    public void setUnionid(String unionid)
    {
        this.unionid = unionid;
    }
}
