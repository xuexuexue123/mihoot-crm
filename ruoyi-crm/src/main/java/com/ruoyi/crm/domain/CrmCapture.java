package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * Captures 发布态（对应 crm_capture）
 */
public class CrmCapture
{
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_UNPUBLISHED = "unpublished";

    private String captureId;
    private String templateId;
    private String title;
    private String status;
    private String contentSnapshot;
    private String successMessage;
    private String allowedOrigins;
    private Date publishedAt;
    private Date unpublishedAt;
    private String createBy;
    private Date createTime;
    private String updateBy;
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

    public String getContentSnapshot()
    {
        return contentSnapshot;
    }

    public void setContentSnapshot(String contentSnapshot)
    {
        this.contentSnapshot = contentSnapshot;
    }

    public String getSuccessMessage()
    {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage)
    {
        this.successMessage = successMessage;
    }

    public String getAllowedOrigins()
    {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins)
    {
        this.allowedOrigins = allowedOrigins;
    }

    public Date getPublishedAt()
    {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt)
    {
        this.publishedAt = publishedAt;
    }

    public Date getUnpublishedAt()
    {
        return unpublishedAt;
    }

    public void setUnpublishedAt(Date unpublishedAt)
    {
        this.unpublishedAt = unpublishedAt;
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

    public boolean isPublished()
    {
        return STATUS_PUBLISHED.equals(status);
    }
}
