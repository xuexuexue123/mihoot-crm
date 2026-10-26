package com.ruoyi.crm.domain.vo;

import java.util.Date;

/**
 * Captures 列表行
 */
public class CaptureSummaryVo
{
    private String captureId;
    private String templateId;
    private String title;
    private String status;
    private String publicPath;
    private Date publishedAt;
    private Date updateTime;

    public String getCaptureId()
    {
        return captureId;
    }

    public void setCaptureId(String captureId)
    {
        this.captureId = captureId;
    }

    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPublicPath()
    {
        return publicPath;
    }

    public void setPublicPath(String publicPath)
    {
        this.publicPath = publicPath;
    }

    public Date getPublishedAt()
    {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt)
    {
        this.publishedAt = publishedAt;
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
