package com.ruoyi.crm.domain;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 企业仪表盘统计指标统一快照
 */
public class CrmDashboardSnapshot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String corpId;
    private Date snapshotDate;
    private String metricType;
    private String metricData;
    private String detailData;
    private String rawJson;
    private Date createdAt;
    private Date updatedAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCorpId()
    {
        return corpId;
    }

    public void setCorpId(String corpId)
    {
        this.corpId = corpId;
    }

    public Date getSnapshotDate()
    {
        return snapshotDate;
    }

    public void setSnapshotDate(Date snapshotDate)
    {
        this.snapshotDate = snapshotDate;
    }

    public String getMetricType()
    {
        return metricType;
    }

    public void setMetricType(String metricType)
    {
        this.metricType = metricType;
    }

    public String getMetricData()
    {
        return metricData;
    }

    public void setMetricData(String metricData)
    {
        this.metricData = metricData;
    }

    public String getDetailData()
    {
        return detailData;
    }

    public void setDetailData(String detailData)
    {
        this.detailData = detailData;
    }

    public String getRawJson()
    {
        return rawJson;
    }

    public void setRawJson(String rawJson)
    {
        this.rawJson = rawJson;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
