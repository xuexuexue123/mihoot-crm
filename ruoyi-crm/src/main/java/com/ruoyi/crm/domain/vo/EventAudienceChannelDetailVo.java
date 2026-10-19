package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 活动受众渠道明细（dedupe=false）
 */
public class EventAudienceChannelDetailVo
{
    private String channel;
    private String sourceId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date touchedAt;

    private String rawStatus;

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
    }

    public Date getTouchedAt()
    {
        return touchedAt;
    }

    public void setTouchedAt(Date touchedAt)
    {
        this.touchedAt = touchedAt;
    }

    public String getRawStatus()
    {
        return rawStatus;
    }

    public void setRawStatus(String rawStatus)
    {
        this.rawStatus = rawStatus;
    }
}
