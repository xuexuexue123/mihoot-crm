package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CrmAnalyticsQueryLog
{
    private String logId;

    private String tenantId;

    private String userId;

    private String metric;

    private String requestJson;

    private String responseJson;

    private Integer durationMs;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getLogId()
    {
        return logId;
    }

    public void setLogId(String logId)
    {
        this.logId = logId;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getMetric()
    {
        return metric;
    }

    public void setMetric(String metric)
    {
        this.metric = metric;
    }

    public String getRequestJson()
    {
        return requestJson;
    }

    public void setRequestJson(String requestJson)
    {
        this.requestJson = requestJson;
    }

    public String getResponseJson()
    {
        return responseJson;
    }

    public void setResponseJson(String responseJson)
    {
        this.responseJson = responseJson;
    }

    public Integer getDurationMs()
    {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs)
    {
        this.durationMs = durationMs;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
