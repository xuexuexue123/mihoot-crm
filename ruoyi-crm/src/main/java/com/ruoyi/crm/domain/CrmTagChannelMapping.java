package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * crm_tag_channel_mapping
 */
public class CrmTagChannelMapping
{
    private Long id;
    private String dictValue;
    private String channel;
    private String channelTagId;
    private String channelTagName;
    private String status;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(String updateBy)
    {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
