package com.ruoyi.crm.domain.dto;

import java.util.List;

/**
 * POST /crm/captures/publish
 */
public class CapturePublishRequest
{
    private String templateId;
    private String successMessage;
    private List<String> allowedOrigins;

    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
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
}
