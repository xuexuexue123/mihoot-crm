package com.ruoyi.crm.domain.dto;

import java.util.List;

/**
 * PATCH Contact 服务摘要请求
 */
public class ContactServiceSummaryPatchDto
{
    private String currentNeed;
    private String confirmedFacts;
    private String commitmentsTodos;
    private String risksSentiment;
    private List<String> lockFields;

    public String getCurrentNeed() { return currentNeed; }
    public void setCurrentNeed(String currentNeed) { this.currentNeed = currentNeed; }
    public String getConfirmedFacts() { return confirmedFacts; }
    public void setConfirmedFacts(String confirmedFacts) { this.confirmedFacts = confirmedFacts; }
    public String getCommitmentsTodos() { return commitmentsTodos; }
    public void setCommitmentsTodos(String commitmentsTodos) { this.commitmentsTodos = commitmentsTodos; }
    public String getRisksSentiment() { return risksSentiment; }
    public void setRisksSentiment(String risksSentiment) { this.risksSentiment = risksSentiment; }
    public List<String> getLockFields() { return lockFields; }
    public void setLockFields(List<String> lockFields) { this.lockFields = lockFields; }
}
