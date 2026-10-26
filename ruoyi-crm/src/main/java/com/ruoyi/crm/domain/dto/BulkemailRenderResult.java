package com.ruoyi.crm.domain.dto;

/**
 * 邮件 subject + HTML 渲染结果
 */
public class BulkemailRenderResult
{
    private String subject;
    private String emailContent;

    public BulkemailRenderResult()
    {
    }

    public BulkemailRenderResult(String subject, String emailContent)
    {
        this.subject = subject;
        this.emailContent = emailContent;
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
}
