package com.ruoyi.crm.domain.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 设定定时发送请求
 */
public class BulkemailScheduleRequest
{
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date scheduledAt;

    public Date getScheduledAt()
    {
        return scheduledAt;
    }

    public void setScheduledAt(Date scheduledAt)
    {
        this.scheduledAt = scheduledAt;
    }
}
