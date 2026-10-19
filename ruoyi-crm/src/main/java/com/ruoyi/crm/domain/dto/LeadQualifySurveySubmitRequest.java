package com.ruoyi.crm.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 认定潜客外投提交请求
 */
public class LeadQualifySurveySubmitRequest
{
    private String token;

    private JsonNode data;

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public JsonNode getData()
    {
        return data;
    }

    public void setData(JsonNode data)
    {
        this.data = data;
    }
}
