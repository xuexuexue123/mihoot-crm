package com.ruoyi.crm.domain.vo;

import com.fasterxml.jackson.databind.JsonNode;

public class TenantWebsitePageDetailVo
{
    private String id;
    private String path;
    private String title;
    private String status;
    private Integer schemaVersion;
    private JsonNode draftSchema;
    private JsonNode publishedSchema;
    private String publishedAt;
    private String createdAt;
    private String updatedAt;

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

    public JsonNode getDraftSchema()
    {
        return draftSchema;
    }

    public void setDraftSchema(JsonNode draftSchema)
    {
        this.draftSchema = draftSchema;
    }

    public JsonNode getPublishedSchema()
    {
        return publishedSchema;
    }

    public void setPublishedSchema(JsonNode publishedSchema)
    {
        this.publishedSchema = publishedSchema;
    }

    public String getPublishedAt()
    {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt)
    {
        this.publishedAt = publishedAt;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(String createdAt)
    {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
