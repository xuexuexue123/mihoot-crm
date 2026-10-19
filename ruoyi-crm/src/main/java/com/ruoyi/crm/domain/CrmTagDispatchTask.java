package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * crm_tag_dispatch_task
 */
public class CrmTagDispatchTask
{
    private Long id;
    private Long segmentId;
    private String dictValue;
    private String channel;
    private String channelTagId;
    private String appId;
    private String status;
    private Integer total;
    private Integer succeeded;
    private Integer failed;
    private String errorMessage;
    private String createBy;
    private Date createTime;
    private Date updateTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

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

    public String getChannelTagId()
    {
        return channelTagId;
    }

    public void setChannelTagId(String channelTagId)
    {
        this.channelTagId = channelTagId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getTotal()
    {
        return total;
    }

    public void setTotal(Integer total)
    {
        this.total = total;
    }

    public Integer getSucceeded()
    {
        return succeeded;
    }

    public void setSucceeded(Integer succeeded)
    {
        this.succeeded = succeeded;
    }

    public Integer getFailed()
    {
        return failed;
    }

    public void setFailed(Integer failed)
    {
        this.failed = failed;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
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

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
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
