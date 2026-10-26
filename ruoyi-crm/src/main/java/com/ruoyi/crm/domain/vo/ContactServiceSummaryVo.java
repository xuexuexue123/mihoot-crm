package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.List;

/**
 * Contact 服务摘要 API 响应
 */
public class ContactServiceSummaryVo
{
    private String contactId;
    private String status;
    private String generatedBy;
    private Long version;
    private Date updatedAt;
    private List<String> sourceChannels;
    private List<String> lockedFields;
    private String model;
    private String errorMessage;
    private SummarySections summary;

    public static class SummarySections
    {
        private String currentNeed;
        private String confirmedFacts;
        private String commitmentsTodos;
        private String risksSentiment;
        private LastTouch lastTouch;

        public String getCurrentNeed() { return currentNeed; }
        public void setCurrentNeed(String currentNeed) { this.currentNeed = currentNeed; }
        public String getConfirmedFacts() { return confirmedFacts; }
        public void setConfirmedFacts(String confirmedFacts) { this.confirmedFacts = confirmedFacts; }
        public String getCommitmentsTodos() { return commitmentsTodos; }
        public void setCommitmentsTodos(String commitmentsTodos) { this.commitmentsTodos = commitmentsTodos; }
        public String getRisksSentiment() { return risksSentiment; }
        public void setRisksSentiment(String risksSentiment) { this.risksSentiment = risksSentiment; }
        public LastTouch getLastTouch() { return lastTouch; }
        public void setLastTouch(LastTouch lastTouch) { this.lastTouch = lastTouch; }
    }

    public static class LastTouch
    {
        private Date at;
        private String channel;
        private String summary;

        public Date getAt() { return at; }
        public void setAt(Date at) { this.at = at; }
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public List<String> getSourceChannels() { return sourceChannels; }
    public void setSourceChannels(List<String> sourceChannels) { this.sourceChannels = sourceChannels; }
    public List<String> getLockedFields() { return lockedFields; }
    public void setLockedFields(List<String> lockedFields) { this.lockedFields = lockedFields; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public SummarySections getSummary() { return summary; }
    public void setSummary(SummarySections summary) { this.summary = summary; }
}
