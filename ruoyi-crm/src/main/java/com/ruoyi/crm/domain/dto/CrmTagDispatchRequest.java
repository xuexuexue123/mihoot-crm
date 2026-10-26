package com.ruoyi.crm.domain.dto;

/**
 * 分群标签下发到渠道
 */
public class CrmTagDispatchRequest
{
    private Long segmentId;
    private String dictValue;
    /** wecom / wechat_mp */
    private String channel;
    /** 公众号下发必填 */
    private String appId;

    public Long getSegmentId()
    {
        return segmentId;
    }

    public void setSegmentId(Long segmentId)
    {
        this.segmentId = segmentId;
    }

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

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }
}
