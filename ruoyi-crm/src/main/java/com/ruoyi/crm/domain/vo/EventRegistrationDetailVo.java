package com.ruoyi.crm.domain.vo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 活动报名详情（含 H5 表单快照）
 */
public class EventRegistrationDetailVo extends EventRegistrationRowVo
{
    private Map<String, Object> formData;
    private String customField;
    private String workEmail;
    private String fullName;
    private String shortDescription;

    /** MyBatis 映射 form_data::text，服务层解析为 formData */
    @JsonIgnore
    private String formDataJson;

    public Map<String, Object> getFormData()
    {
        return formData;
    }

    public void setFormData(Map<String, Object> formData)
    {
        this.formData = formData;
    }

    public String getCustomField()
    {
        return customField;
    }

    public void setCustomField(String customField)
    {
        this.customField = customField;
    }

    public String getWorkEmail()
    {
        return workEmail;
    }

    public void setWorkEmail(String workEmail)
    {
        this.workEmail = workEmail;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getFormDataJson()
    {
        return formDataJson;
    }

    public void setFormDataJson(String formDataJson)
    {
        this.formDataJson = formDataJson;
    }
}
