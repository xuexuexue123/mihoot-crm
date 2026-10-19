package com.ruoyi.crm.domain.dto;

/**
 * 渲染 preview 请求
 */
public class BulkemailRenderPreviewRequest
{
    private String bulkId;
    private String subject;
    private String emailContent;
    private String contactId;
    private Boolean personalizationEnabled;

    public String getBulkId()
    {
        return bulkId;
    }

    public void setBulkId(String bulkId)
    {
        this.bulkId = bulkId;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getEmailContent()
    {
        return emailContent;
    }

    public void setEmailContent(String emailContent)
    {
        this.emailContent = emailContent;
    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public Boolean getPersonalizationEnabled()
    {
        return personalizationEnabled;
    }

    public void setPersonalizationEnabled(Boolean personalizationEnabled)
    {
        this.personalizationEnabled = personalizationEnabled;
    }
}
