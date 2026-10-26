package com.ruoyi.crm.domain.dto;

import java.util.Map;
import com.ruoyi.crm.domain.CrmOutreachlead;

/**
 * 官网活动报名请求（字段对齐 H5 outreachlead，无 registrationToken）
 */
public class PublicWebsiteEventRegisterRequest extends CrmOutreachlead
{
    private Map<String, Object> formData;
    private String pagePath;

    public Map<String, Object> getFormData()
    {
        return formData;
    }

    public void setFormData(Map<String, Object> formData)
    {
        this.formData = formData;
    }

    public String getPagePath()
    {
        return pagePath;
    }

    public void setPagePath(String pagePath)
    {
        this.pagePath = pagePath;
    }
}
