package com.ruoyi.crm.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class SaveTenantWebsitePageDraftRequest
{
    private JsonNode schema;

    public JsonNode getSchema()
    {
        return schema;
    }

    public void setSchema(JsonNode schema)
    {
        this.schema = schema;
    }
}
