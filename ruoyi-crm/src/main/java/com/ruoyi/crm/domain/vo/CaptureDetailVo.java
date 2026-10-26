package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.List;

/**
 * Captures 管理端详情 / 发布结果
 */
public class CaptureDetailVo
{
    private String captureId;
    private String templateId;
    private String title;
    private String status;
    private String contentSnapshot;
    private String successMessage;
    private List<String> allowedOrigins;
    private String publicPath;
    private Date publishedAt;
    private Date unpublishedAt;
    private Date createTime;
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

    public List<String> getAllowedOrigins()
    {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins)
    {
        this.allowedOrigins = allowedOrigins;
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

    public Date getUnpublishedAt()
    {
        return unpublishedAt;
    }

    public void setUnpublishedAt(Date unpublishedAt)
    {
        this.unpublishedAt = unpublishedAt;
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
}
