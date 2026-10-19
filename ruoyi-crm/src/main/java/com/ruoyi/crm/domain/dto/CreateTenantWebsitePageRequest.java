package com.ruoyi.crm.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class CreateTenantWebsitePageRequest
{
    private String path;
    private String title;
    private JsonNode schema;

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
}
