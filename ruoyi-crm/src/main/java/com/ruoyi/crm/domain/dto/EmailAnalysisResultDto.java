package com.ruoyi.crm.domain.dto;

import java.util.List;

/**
 * 邮件 AI 分析结果（EmailAnalysisResult）
 */
public class EmailAnalysisResultDto
{
    private String executiveSummary;
    private String category;
    private String urgency;
    private EmailAnalysisSentimentDto sentiment;
    private String detectedIntent;
    private String suggestedAction;
    private List<EmailAnalysisEntityItemDto> entities;
    private EmailAnalysisSuggestedTicketDto suggestedTicket;
    private EmailAnalysisSuggestedLeadDto suggestedLead;

    public String getExecutiveSummary()
    {
        return executiveSummary;
    }

    public void setExecutiveSummary(String executiveSummary)
    {
        this.executiveSummary = executiveSummary;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getUrgency()
    {
        return urgency;
    }

    public void setUrgency(String urgency)
    {
        this.urgency = urgency;
    }

    public EmailAnalysisSentimentDto getSentiment()
    {
        return sentiment;
    }

    public void setSentiment(EmailAnalysisSentimentDto sentiment)
    {
        this.sentiment = sentiment;
    }

    public String getDetectedIntent()
    {
        return detectedIntent;
    }

    public void setDetectedIntent(String detectedIntent)
    {
        this.detectedIntent = detectedIntent;
    }

    public String getSuggestedAction()
    {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction)
    {
        this.suggestedAction = suggestedAction;
    }

    public List<EmailAnalysisEntityItemDto> getEntities()
    {
        return entities;
    }

    public void setEntities(List<EmailAnalysisEntityItemDto> entities)
    {
        this.entities = entities;
    }

    public EmailAnalysisSuggestedTicketDto getSuggestedTicket()
    {
        return suggestedTicket;
    }

    public void setSuggestedTicket(EmailAnalysisSuggestedTicketDto suggestedTicket)
    {
        this.suggestedTicket = suggestedTicket;
    }

    public EmailAnalysisSuggestedLeadDto getSuggestedLead()
    {
        return suggestedLead;
    }

    public void setSuggestedLead(EmailAnalysisSuggestedLeadDto suggestedLead)
    {
        this.suggestedLead = suggestedLead;
    }
}
