package com.ruoyi.crm.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 受众 preview 单行
 */
public class BulkemailAudiencePreviewRow
{
    private String contactId;
    private String name;
    private String fullName;
    private String workEmail;
    private List<String> tags = new ArrayList<>();

    @JsonIgnore
    private String tagJson;

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getWorkEmail()
    {
        return workEmail;
    }

    public void setWorkEmail(String workEmail)
    {
        this.workEmail = workEmail;
    }

    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(List<String> tags)
    {
        this.tags = tags;
    }

    public String getTagJson()
    {
        return tagJson;
    }

    public void setTagJson(String tagJson)
    {
        this.tagJson = tagJson;
    }
}
