package com.ruoyi.crm.domain.vo;

import com.fasterxml.jackson.databind.JsonNode;

public class PublicTenantWebsitePageVo
{
    private String path;
    private String title;
    private JsonNode schema;
    private String publishedAt;

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

    public JsonNode getSchema()
    {
        return schema;
    }

    public void setSchema(JsonNode schema)
    {
        this.schema = schema;
    }

    public String getPublishedAt()
    {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt)
    {
        this.publishedAt = publishedAt;
    }
}
