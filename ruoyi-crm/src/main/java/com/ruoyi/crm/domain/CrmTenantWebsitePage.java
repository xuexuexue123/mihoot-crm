package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 租户官网页面 Schema（draft/published）
 */
public class CrmTenantWebsitePage
{
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";

    private String id;
    private String path;
    private String title;
    private String draftSchema;
    private String publishedSchema;
    private String status;
    private Integer schemaVersion;
    private Date publishedAt;
    private Date createdAt;
    private Date updatedAt;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDraftSchema()
    {
        return draftSchema;
    }

    public void setDraftSchema(String draftSchema)
    {
        this.draftSchema = draftSchema;
    }

    public String getPublishedSchema()
    {
        return publishedSchema;
    }

    public void setPublishedSchema(String publishedSchema)
    {
        this.publishedSchema = publishedSchema;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getSchemaVersion()
    {
        return schemaVersion;
    }

    public void setSchemaVersion(Integer schemaVersion)
    {
        this.schemaVersion = schemaVersion;
    }

    public Date getPublishedAt()
    {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt)
    {
        this.publishedAt = publishedAt;
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
